package de.marcely.rekit.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.KickReason.KickReasonType;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.packet.chunk.PacketChunkFlag;
import de.marcely.rekit.network.packet.chunk.PacketChunkHeader;
import de.marcely.rekit.network.packet.chunk.PacketChunkResend;
import de.marcely.rekit.util.Util;
import lombok.Getter;
import lombok.Setter;

public class Client {
	
	private static final long TIMEOUT = 8000;
	private static final long KEEP_ALIVE_TIME = 1000;
	private static final int NETWORK_CHUNK_RESEND_SIZE = 32;
	private static final int MAX_BUFFER_SIZE = 1024 * 32;
	
	@Getter private final Server server;
	@Getter private final InetSocketAddress address;
	@Getter private final short id;
	
	@Setter private ClientState state = ClientState.DISCONNECTED;
	@Getter @Setter private long lastReceivedPacket = System.currentTimeMillis();
	@Getter private final long loginDate = System.currentTimeMillis();
	private long lastKeepAlive = System.currentTimeMillis();
	private Queue<PacketChunkResend> resendQueue = new ArrayDeque<>();
	private Packet resendPacket = new Packet();
	private int bufferSize = 0;
	public int ack = 0;
	private int sequence = 0;
	
	public Client(Server server, InetSocketAddress address, short id){
		this.server = server;
		this.address = address;
		this.id = id;
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
	
	public boolean kick(KickReason reason){
		if(!close())
			return false;
		
		server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CLOSE, reason.getMessage());
		
		return true;
	}
	
	public boolean close(){
		if(!isConnected())
			return false;
		
		setState(ClientState.DISCONNECTED);
		
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
			kick(new KickReason(KickReasonType.WEAK_CONNECTION_TIMEOUT));
			return;
		}
		
		// send resend queue
		if(resendQueue.size() >= 1){
			final PacketChunkResend packet = resendQueue.peek();
			
			if(System.currentTimeMillis() - packet.firstSendTime > TIMEOUT)
				kick(new KickReason(KickReasonType.WEAK_CONNECTION_ACK));
			else
				resendChunk(packet);
		}
		
		// send keep alive
		if(System.currentTimeMillis() - lastKeepAlive >= KEEP_ALIVE_TIME){
			switch(state){
			case ONLINE:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_KEEP_ALIVE, "");
				break;
				
			case CONNECT:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CONNECT, "");
				break;
				
			case PENDING:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			default:
				break;
			}
			
			lastKeepAlive = System.currentTimeMillis();
		}
	}
	
	public void signalResend(){
		this.resendPacket.flagsMask |= PacketFlag.RESEND.getMask();
	}
	
	public void handleConnectedPacket(Packet packet){
		if(packet.hasFlag(PacketFlag.RESEND))
			resend();
		
		if(packet.hasFlag(PacketFlag.CONTROL)){
			final byte type = packet.data[0];
			
			switch(type){
			case ProtocolHandler.PACKET_TYPE_CLOSE:
				String reason = "";
				
				if(packet.data.length >= 2)
					reason = new String(Util.arraycopy(packet.data, 1, packet.data.length), StandardCharsets.UTF_8);
				
				kick(new KickReason(reason));
				break;
				
			case ProtocolHandler.PACKET_TYPE_CONNECT:
				if(getState() != ClientState.DISCONNECTED) return;
				
				setState(ClientState.PENDING);
				
				this.server.protocol.sendControlPacket(getAddress().getAddress(), getAddress().getPort(), 0,
						ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			case ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT:
				if(getState() != ClientState.CONNECT) return;
				
				setState(ClientState.ONLINE);
				
				this.server.protocol.sendControlPacket(getAddress().getAddress(), getAddress().getPort(), 0,
						ProtocolHandler.PACKET_TYPE_ACCEPT, "");
				break;
			}
		
		}else if(getState() == ClientState.PENDING)
			setState(ClientState.ONLINE);
		
		if(getState() == ClientState.ONLINE){
			setLastReceivedPacket(System.currentTimeMillis());
			ackChunks(packet.ack);
		}
	}
	
	private void resend(){
		for(PacketChunkResend packet:this.resendQueue)
			resendChunk(packet);
	}
	
	private void resendChunk(PacketChunkResend chunk){
		queueChunkEx(Util.concat(new PacketChunkFlag[]{ PacketChunkFlag.RESEND }, chunk.flags), chunk.data, chunk.sequence);
		chunk.lastSendTime = System.currentTimeMillis();
	}
	
	private void ackChunks(int ack){
		for(PacketChunkResend packet:this.resendQueue){
			if(ProtocolHandler.isSequenceInBackroom(packet.sequence, ack)){
				this.resendQueue.remove(packet);
				return;
			}
		}
	}
	
	private void queueChunkEx(PacketChunkFlag[] flags, byte[] data, int sequence){
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
					kick(new KickReason(KickReasonType.WEAK_CONNECTION_OUT_OF_BUFFER));
					return;
				}
				
				this.resendQueue.add(new PacketChunkResend(sequence, flags, data, System.currentTimeMillis(), System.currentTimeMillis()));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void queueChunk(PacketChunkFlag[] flags, byte[] data){
		if(PacketChunkFlag.has(flags, PacketChunkFlag.VITAL))
			this.sequence = (this.sequence+1) % ProtocolHandler.PACKET_MAX_SEQUENCE;
		
		queueChunkEx(flags, data, this.sequence);
	}
	
	private void flush(){
		if(this.resendQueue.size() == 0 && this.resendPacket.flagsMask == 0x00)
			return;
		
		this.resendPacket.data = this.resendPacket.stream.toByteArray();
		this.resendPacket.ack = this.ack;
		
		sendPacket(this.resendPacket);
		
		this.resendPacket.ack = 0;
		this.resendPacket.chunksAmount = 0;
		this.resendPacket.data = null;
		this.resendPacket.flagsMask = 0x00;
		this.resendPacket.stream.reset();
	}
	
	public void sendPacket(Packet packet){
		this.server.protocol.sendPacket(packet, this.address.getAddress(), this.address.getPort());
	}
}