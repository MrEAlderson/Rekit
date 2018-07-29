package de.marcely.rekit.network.server;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

import de.marcely.rekit.Message;
import de.marcely.rekit.entity.EntityPlayer;
import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketSendFlag;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.chunk.PacketChunk;
import de.marcely.rekit.network.packet.chunk.PacketChunkFlag;
import de.marcely.rekit.network.packet.chunk.PacketChunkHeader;
import de.marcely.rekit.network.packet.chunk.PacketChunkResend;
import de.marcely.rekit.plugin.player.KickCauseType;
import de.marcely.rekit.snapshot.SnapshotRate;
import de.marcely.rekit.snapshot.SnapshotStorage;
import de.marcely.rekit.util.BufferedReadStream;
import de.marcely.rekit.util.BufferedWriteStream;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class Client {
	
	private static final long TIMEOUT = 8000;
	private static final long KEEP_ALIVE_TIME = 1000;
	private static final int NETWORK_CHUNK_RESEND_SIZE = 32;
	private static final int MAX_BUFFER_SIZE = 1024 * 32;
	
	@Getter private final Server server;
	@Getter private final InetSocketAddress address;
	@Getter private final short id;
	@Getter private final ClientHandler handler;
	
	// stuff for the protocol
	public ClientState state = ClientState.DISCONNECTED;
	public long lastReceivedPacket = System.currentTimeMillis();
	public long lastSendPacket;
	@Getter private final long loginDate = System.currentTimeMillis();
	private ArrayDeque<PacketChunkResend> resendQueue = new ArrayDeque<>();
	private Packet resendPacket = new Packet();
	private int bufferSize = 0;
	public int ack = 0;
	private int sequence = 0;
	
	// stuff when ingame
	public SnapshotRate snapRate = SnapshotRate.INIT;
	public SnapshotStorage snapStorage = new SnapshotStorage();
	public int lastAckedSnapshot;
	
	public EntityPlayer player;
	public ServerClientState serverState = ServerClientState.NONE;
	public String gameVersion;
	public long gameLastChangedInfo;
	public String gameName;
	public String gameClan;
	public int gameCountry;
	public String gameSkinName;
	public boolean gameHasCustomColor;
	public Color gameBodyColor, gameFeetColor;
	
	public Client(Server server, InetSocketAddress address, short id){
		this.server = server;
		this.address = address;
		this.id = id;
		this.handler = new ClientHandler(this);
	}
	
	public String getIdentifier(){
		return Util.getIdentifier(address.getAddress(), address.getPort());
	}
	
	public boolean isConnected(){
		return server.protocol.isConnected(address.getAddress(), address.getPort());
	}
	
	public ClientState getState(){
		if(!isConnected())
			this.state = ClientState.DISCONNECTED;
		
		return this.state;
	}
	
	public boolean kick(KickCauseType cause){
		return kick("", cause); 
	}
	
	public boolean kick(String message, KickCauseType cause){
		if(!close())
			return false;
		
		server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), this.ack, ProtocolHandler.PACKET_TYPE_CLOSE, message);
		
		return true;
	}
	
	public boolean close(){
		if(!isConnected())
			return false;
		
		this.state = ClientState.DISCONNECTED;
		
		this.server.protocol.clients.remove(this.id);
		this.server.protocol.clients2.remove(getIdentifier());
		this.server.protocol.bufferedChunks.remove(getIdentifier());
		
		for(PacketReceiver receiver:this.server.protocol.receivers)
			receiver.onDisconnect(this);
		
		System.out.println("Disconnected");
		
		return true;
	}
	
	public void tick(){
		// check timeout
		if(System.currentTimeMillis() - lastReceivedPacket >= TIMEOUT){
			kick(Message.KICK_WEAK_CONNECTION_TIMEOUT.msg, KickCauseType.NETWORK);
			return;
		}
		
		// send resend queue
		if(resendQueue.size() >= 1){
			final PacketChunkResend packet = resendQueue.peek();
			
			if(System.currentTimeMillis() - packet.firstSendTime > TIMEOUT)
				kick(Message.KICK_WEAK_CONNECTION_ACK.msg, KickCauseType.NETWORK);
			else if(System.currentTimeMillis() - packet.lastSendTime > 1000)
				resendChunk(packet);
		}
		
		// flush
		if(state == ClientState.ONLINE &&
		   System.currentTimeMillis() - lastSendPacket >= KEEP_ALIVE_TIME / 2)
			flush();
		
		// send keep alive
		if(System.currentTimeMillis() - lastSendPacket >= KEEP_ALIVE_TIME){
			switch(state){
			case ONLINE:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), this.ack, ProtocolHandler.PACKET_TYPE_KEEP_ALIVE, "");
				break;
				
			case CONNECT:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), this.ack, ProtocolHandler.PACKET_TYPE_CONNECT, "");
				break;
				
			case PENDING:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), this.ack, ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			default:
				break;
			}
		}
	}
	
	public void signalResend(){
		this.resendPacket.flagsMask |= PacketFlag.RESEND.getMask();
	}
	
	public void handleSysConnectedPacket(Packet packet){
		if(packet.hasFlag(PacketFlag.RESEND))
			resend();
		
		if(packet.hasFlag(PacketFlag.CONTROL)){
			final byte type = packet.data[0];
			
			switch(type){
			case ProtocolHandler.PACKET_TYPE_CLOSE:
				String reason = "";
				
				if(packet.data.length >= 2)
					reason = new String(Util.arraycopy(packet.data, 1, packet.data.length), StandardCharsets.UTF_8);
				
				kick(reason, KickCauseType.PLAYER);
				break;
				
			case ProtocolHandler.PACKET_TYPE_CONNECT:
				if(getState() != ClientState.DISCONNECTED) return;
				
				this.state = ClientState.PENDING;
				
				this.server.protocol.sendControlPacket(getAddress().getAddress(), getAddress().getPort(), this.ack,
						ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			case ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT:
				if(getState() != ClientState.CONNECT) return;
				
				this.state = ClientState.ONLINE;
				this.serverState = ServerClientState.AUTH;
				
				this.server.protocol.sendControlPacket(getAddress().getAddress(), getAddress().getPort(), this.ack,
						ProtocolHandler.PACKET_TYPE_ACCEPT, "");
				break;
			}
		
		}else if(getState() == ClientState.PENDING){
			this.state = ClientState.ONLINE;
			this.serverState = ServerClientState.AUTH;
		}
		
		if(getState() == ClientState.ONLINE){
			this.lastReceivedPacket = System.currentTimeMillis();
			ackChunks(packet.ack);
		}
	}
	
	public void handlePacket(PacketChunk packet){
		final BufferedReadStream stream = new BufferedReadStream(packet.buffer);
		
		try{
			handleConnectedPacketStream(stream);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		stream.close();
	}
	
	private void handleConnectedPacketStream(BufferedReadStream stream) throws Exception {
		final int header = stream.readTWInt();
		final boolean isSystem = (header&1) != 0;
		final int type = header >> 1;
		
		System.out.println(isSystem + " :/ " + type + " " + stream.available());
		
		if(isSystem){
			switch(type){
			case PacketHandler.MSG_CL_INFO:
				this.handler.handleMsgInfo(stream);
				break;
			
			case PacketHandler.MSG_CL_REQUEST_MAP_DATA:
				this.handler.handleMsgRequestMapData(stream);
				break;
				
			case PacketHandler.MSG_CL_READY:
				this.handler.handleMsgReady(stream);
				break;
				
			case PacketHandler.MSG_CL_ENTER_GAME:
				this.handler.handleMsgEnterGame(stream);
				break;
				
			case PacketHandler.MSG_CL_INPUT:
				this.handler.handleMsgInput(stream);
				break;
				
			case PacketHandler.MSG_CL_RCON_CMD:
				this.handler.handleMsgRconCMD(stream);
				break;
				
			case PacketHandler.MSG_CL_RCON_AUTH:
				this.handler.handleMsgRconAuth(stream);
				break;
				
			case PacketHandler.MSG_PING:
				this.handler.handleMsgPing(stream);
				break;
			}
		
		}else if(this.serverState == ServerClientState.READY ||
			     this.serverState == ServerClientState.IN_GAME){
			
			final PacketType pType = PacketType.ofID(type);
			
			if(pType != null){
				final DataPacket packet = pType.newClientDataPacketInstance();
				
				if(packet != null){
					try{
						packet.read(new PacketInputStream(stream.read(stream.available())));
						
						handler.handleData(packet);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void resend(){
		for(PacketChunkResend packet:this.resendQueue)
			resendChunk(packet);
	}
	
	private void resendChunk(PacketChunkResend chunk){
		queueChunkEx(PacketChunkFlag.concat(new PacketChunkFlag[]{ PacketChunkFlag.RESEND }, chunk.flags), chunk.data, chunk.sequence);
		chunk.lastSendTime = System.currentTimeMillis();
	}
	
	/*private void ackChunks(int ack){
		final Iterator<PacketChunkResend>
		
		for(PacketChunkResend packet:this.resendQueue){
			if(ProtocolHandler.isSequenceInBackroom(packet.sequence, ack)){
				this.resendQueue.remove(packet);
				this.bufferSize -= NETWORK_CHUNK_RESEND_SIZE + packet.data.length;
			}else
				return;
		}
	}*/
	
	private void ackChunks(int ack){
		final PacketChunkResend first = this.resendQueue.peek();
		final boolean once = this.resendQueue.size() == 1;
		PacketChunkResend current = this.resendQueue.pollLast();
		
		while(true){
		    if(current == null)
		        break;
		    else if(current.equals(first) && !once){
		    	this.resendQueue.add(current);
		    	return;
		    }
		    
		    if(!ProtocolHandler.isSequenceInBackroom(current.sequence, ack)){
		        this.resendQueue.add(current);
		        return;
		    }else
		    	this.bufferSize -= NETWORK_CHUNK_RESEND_SIZE + current.data.length;
		    
		    if(once)
		    	return;
		    
		    current = this.resendQueue.pollLast();
		}
	}
	
	private boolean queueChunkEx(PacketChunkFlag[] flags, byte[] data, int sequence){
		if(resendPacket.stream.size() + data.length + ProtocolHandler.PACKET_HEADER_SIZE > ProtocolHandler.PACKET_MAX_PAYLOAD)
			flush();
		
		try{
			new PacketChunkHeader(flags, data.length, sequence).write(this.resendPacket.stream);
			this.resendPacket.stream.write(data);
			
			this.resendPacket.chunksAmount++;
			
			if(PacketChunkFlag.has(flags, PacketChunkFlag.VITAL) &&
			   !PacketChunkFlag.has(flags, PacketChunkFlag.RESEND)){
				
				this.bufferSize += NETWORK_CHUNK_RESEND_SIZE + data.length;
				
				if(this.bufferSize >= MAX_BUFFER_SIZE){
					kick(Message.KICK_WEAK_CONNECTION_OUT_OF_BUFFER.msg, KickCauseType.NETWORK);
					return false;
				}
				
				this.resendQueue.add(new PacketChunkResend(sequence, flags, data, System.currentTimeMillis(), System.currentTimeMillis()));
			}
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean queueChunk(PacketChunkFlag[] flags, byte[] data){
		if(PacketChunkFlag.has(flags, PacketChunkFlag.VITAL))
			this.sequence = (this.sequence+1) % ProtocolHandler.PACKET_MAX_SEQUENCE;
		
		return queueChunkEx(flags, data, this.sequence);
	}
	
	public void flush(){
		if(this.resendPacket.chunksAmount == 0 && this.resendPacket.flagsMask == 0x00)
			return;
		
		this.resendPacket.data = this.resendPacket.stream.toByteArray();
		this.resendPacket.ack = this.ack;
		
		sendPacket(this.resendPacket);
		
		this.resendPacket.ack = 0;
		this.resendPacket.chunksAmount = 0;
		this.resendPacket.data = null;
		this.resendPacket.flagsMask = 0x00;
		this.resendPacket.stream.reset();
		this.lastSendPacket = System.currentTimeMillis();
	}
	
	public void sendPacket(Packet packet){
		this.server.protocol.sendPacket(packet, this.address.getAddress(), this.address.getPort());
	}
	
	public void sendMsgEx(BufferedWriteStream stream, boolean isSystem, PacketSendFlag... flags){
		final PacketChunk packet = new PacketChunk(flags, stream.toByteArray());
		
		if(isSystem && packet.buffer[0] >= 5 && packet.buffer[0] <= 8)
			System.out.println(packet.buffer[0] + " " + this.ack);
		
		packet.buffer[0] <<= 1;
		
		if(isSystem)
			packet.buffer[0] |= 1;
		
		send(packet);
	}
	
	public void sendDataPacket(DataPacket packet, PacketSendFlag... flags){
		final PacketOutputStream stream = new PacketOutputStream();
		
		stream.writeInt(packet.getType().id);
		
		try{
			packet.write(stream);
			sendMsgEx(new BufferedWriteStream(stream), false, flags);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void send(PacketChunk packet){
		this.server.protocol.send(packet, address.getAddress(), address.getPort(), this);
	}
}