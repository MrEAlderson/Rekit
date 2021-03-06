package de.marcely.rekit.network.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.Main;
import de.marcely.rekit.Message;
import de.marcely.rekit.network.ChunkBuffer;
import de.marcely.rekit.network.QueuedPacket;
import de.marcely.rekit.network.SocketPump;
import de.marcely.rekit.network.UDPSocket;
import de.marcely.rekit.network.master.MasterServerPackets;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.packet.PacketSendFlag;
import de.marcely.rekit.network.packet.chunk.PacketChunk;
import de.marcely.rekit.network.packet.chunk.PacketChunkFlag;
import de.marcely.rekit.plugin.player.KickCauseType;
import de.marcely.rekit.util.BufferedWriteStream;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class ProtocolHandler {
	
	public static final int TICKS = 50;
	
	public static final int PACKET_MIN_SIZE = 3;
	public static final int PACKET_MAX_SIZE = 1400;
	public static final int PACKET_HEADER_SIZE = 3;
	public static final int PACKET_DATA_OFFSET = 6;
	public static final int PACKET_MAX_SEQUENCE = 1024;
	public static final int PACKET_MAX_PAYLOAD = PACKET_MAX_SIZE - PACKET_DATA_OFFSET;
	
	public static final byte PACKET_TYPE_KEEP_ALIVE = 0;
	public static final byte PACKET_TYPE_CONNECT = 1;
	public static final byte PACKET_TYPE_CONNECT_ACCEPT = 2;
	public static final byte PACKET_TYPE_ACCEPT = 3;
	public static final byte PACKET_TYPE_CLOSE = 4;
	
	public static final int VANILLA_MAX_CLIENTS = 16;
	
	@Getter private final UDPSocket socket;
	private final Server server;
	
	public List<PacketReceiver> receivers = new ArrayList<PacketReceiver>();
	private Map<Byte, QueuedPacket> sendQueuedAcks = new HashMap<>();
	public Map<String, ChunkBuffer> bufferedChunks = new HashMap<>();
	public java.util.Map<Integer, Client> ingameClients = new ConcurrentHashMap<>();
	public java.util.Map<String, Client> clients = new HashMap<>();
	
	public ProtocolHandler(int port, Server server){
		this.socket = new UDPSocket(port, PACKET_MAX_SIZE);
		this.server = server;
	}
	
	public boolean isRunning(){
		return this.socket.isRunning();
	}
	
	public boolean run(){
		if(isRunning()) return false;
		
		// run socket
		final boolean result = socket.run(new SocketPump(){
			public void receive(InetAddress address, int port, byte[] buffer) throws Exception {
				if(buffer.length <= PACKET_MIN_SIZE || buffer.length >= PACKET_MAX_SIZE){
					System.out.println("Packet is too big/small: " + buffer.length);
					return;
				}
				
				handleQueuedPacket(address, port, buffer);
			}
		});
		
		return result;
	}
	
	private void handleQueuedPacket(InetAddress address, int port, byte[] buffer){
		Client client = getClient(address, port);
		final PacketFlag[] flags = PacketFlag.ofBitMask((byte) (buffer[0] >> 4));
		final int ack = ((buffer[0] & 0xF) << 8) | buffer[1];
		final byte chunksAmount = buffer[2];
		
		if(PacketFlag.has(flags, PacketFlag.CONNLESS)){
			if(buffer.length < PACKET_DATA_OFFSET)
				return;
			
			buffer = Util.arraycopy(buffer, PACKET_DATA_OFFSET, buffer.length);
			
			handleUnconnectedPacket(new PacketChunk(
					new PacketSendFlag[]{ PacketSendFlag.CONNLESS },
					buffer),
						address, port);
		
		}else{
			buffer = Util.arraycopy(buffer, PACKET_HEADER_SIZE, buffer.length);
			
			if(PacketFlag.has(flags, PacketFlag.COMPRESSION)){
				if(PacketFlag.has(flags, PacketFlag.CONTROL))
					return;
				
				final byte[] data = new byte[PACKET_MAX_SIZE];
				
				if(Util.HUFFMAN.decompress(buffer, 0, buffer.length, data, 0, data.length) == -1){
					new UnsupportedOperationException("Huffman decoding failed").printStackTrace();
					return;
				}
			}
			
			if(PacketFlag.has(flags, PacketFlag.CONTROL) &&
					buffer[0] == PACKET_TYPE_CONNECT &&
					client == null){
				
				// get amount of client with same ip
				int sameIPsAmount = 0;
				
				for(Client c:clients.values()){
					if(c.getAddress().getAddress().getHostAddress().equals(address.getHostAddress()))
						sameIPsAmount++;
				}
				
				if(sameIPsAmount > this.server.getMaxSameIPsAmount()){
					sendControlPacket(address, port, 0,
							PACKET_TYPE_CLOSE, "Only " + this.server.getMaxSameIPsAmount() + " players with the same IP are allowed");
					return;
				}
				
				// check if server is full
				if(this.clients.size() >= this.server.getMaxPlayers()){
					sendControlPacket(address, port, 0,
							PACKET_TYPE_CLOSE, "This server is full");
					return;
				}
				
				// login
				client = new Client(Main.SERVER, new InetSocketAddress(address, port));
				
				this.clients.put(client.getIdentifier(), client);
				
				sendControlPacket(address, port, 0,
						PACKET_TYPE_CONNECT_ACCEPT, "");
			
				if(client != null){
					client.handleSysConnectedPacket(new Packet(
							flags,
							ack,
							chunksAmount,
							buffer));
				}
				
				return;
			}
			
			if(client != null){
				client.handleSysConnectedPacket(new Packet(
						flags,
						ack,
						chunksAmount,
						buffer));
			}
		}
		
		if(client != null && !PacketFlag.has(flags, PacketFlag.CONNLESS)){
			final List<PacketChunk> packets = PacketChunk.read(buffer, chunksAmount, client);
			
			for(PacketChunk packet:packets)
				client.handlePacket(packet);
		}
	}
	
	public void sendControlPacket(InetAddress address, int port, int ack, byte type, String extra){
		final Client client = getClient(address, port);
		
		if(client != null)
			client.lastSendPacket = System.currentTimeMillis();
		
		final byte[] extraRaw = extra.getBytes(StandardCharsets.UTF_8);
		final byte[] buffer = new byte[extraRaw.length+1];
		
		buffer[0] = type;
		System.arraycopy(extraRaw, 0, buffer, 1, extraRaw.length);
		
		sendPacket(address,
				port,
				PacketFlag.CONTROL.getMask(),
				ack,
				(byte) 1,
				buffer);
	}
	
	private void handleUnconnectedPacket(PacketChunk packet, InetAddress address, int port){
		if(packet.buffer.length != 9) return;
		
		if(Util.compare(MasterServerPackets.SERVERBROWSE_GETINFO, packet.buffer, true))
			sendServerInfo(address, port, packet.buffer[8], false);
		else if(Util.compare(MasterServerPackets.SERVERBROWSE_GETINFO_64_LEGACY, packet.buffer, true))
			sendServerInfo(address, port, packet.buffer[8], true);
	}
	
	private void sendServerInfo(InetAddress address, int port, int token, boolean legacy64){
		BufferedWriteStream stream = new BufferedWriteStream();
		
		int currentPlayer = 0;
		final int maxPlayersPerPacket = legacy64 ? 24 : VANILLA_MAX_CLIENTS;
		
		while(true){
			int playersAmount = this.clients.size()-currentPlayer;
			
			if(playersAmount > maxPlayersPerPacket)
				playersAmount = maxPlayersPerPacket;
			
			
			stream.write(legacy64 ?
					MasterServerPackets.SERVERBROWSE_INFO_64_LEGACY :
					MasterServerPackets.SERVERBROWSE_INFO);
			stream.writeTWString("" + token);
			stream.writeTWString(this.server.getGameVersion());
			
			if(legacy64){
				stream.writeTWString(this.server.getServerBrowseName());
			}else{
				stream.writeTWString(this.clients.size() <= VANILLA_MAX_CLIENTS ?
						this.server.getServerBrowseName() :
						this.server.getServerBrowseName() + " [" + this.clients.size() + "/" + this.server.getMaxPlayers() + "]");
			}
			
			stream.writeTWString(this.server.getMap().getName());
			stream.writeTWString(this.server.getServerBrowseType());
			stream.writeTWBoolean(this.server.isPasswordEnabled());
			
			if(!legacy64){
				// players
				stream.writeTWString("" + (this.clients.size() > VANILLA_MAX_CLIENTS ? VANILLA_MAX_CLIENTS : this.clients.size()));
				stream.writeTWString("" + (this.server.getMaxPlayers() > VANILLA_MAX_CLIENTS ? VANILLA_MAX_CLIENTS : this.server.getMaxPlayers()));
				// clients
				stream.writeTWString("" + (this.clients.size() > VANILLA_MAX_CLIENTS ? VANILLA_MAX_CLIENTS : this.clients.size()));
				stream.writeTWString("" + (this.server.getMaxPlayers() > VANILLA_MAX_CLIENTS ? VANILLA_MAX_CLIENTS : this.server.getMaxPlayers()));
			
			}else{
				// players
				stream.writeTWString("" + this.clients.size());
				stream.writeTWString("" + this.server.getMaxPlayers());
				// clients
				stream.writeTWString("" + this.clients.size());
				stream.writeTWString("" + this.server.getMaxPlayers());
				
				stream.writeTWInt(0); // offset
			}
			
			for(int i=0; i<playersAmount; i++){
				currentPlayer++;
				
				// TODO
				stream.writeTWString("Name max length16");
				stream.writeTWString("Name max12");
				stream.writeTWString("Coun6");
				stream.writeTWString("Scor6");
				stream.writeTWString("0"); // is spectator, "1" if yes
			}
			
			send(new PacketChunk(new PacketSendFlag[]{ PacketSendFlag.CONNLESS }, stream.toByteArray()),
				address, port, null);
			
			stream.close();
			
			if(currentPlayer+1 >= this.clients.size())
				break;
			else
				stream = new BufferedWriteStream();
		}
	}
	
	public void send(PacketChunk packet, InetAddress address, int port, @Nullable Client client){
		if(packet.buffer.length >= PACKET_MAX_PAYLOAD){
			new UnsupportedOperationException("Payload reached max size").printStackTrace();
			return;
		}
		
		if(PacketSendFlag.has(packet.flags, PacketSendFlag.CONNLESS)){
			final byte[] buffer = new byte[packet.buffer.length+PACKET_DATA_OFFSET];
			
			for(int i=0; i<=5; i++)
				buffer[i] = (byte) 0xFF;
			
			System.arraycopy(packet.buffer, 0, buffer, PACKET_DATA_OFFSET, packet.buffer.length);
			
			this.socket.sendRawPacket(address, port, buffer);
			
			return;
		}
		
		if(client == null) return;
		
		PacketChunkFlag[] flags = null;
		
		if(PacketSendFlag.has(packet.flags, PacketSendFlag.VITAL))
			flags = new PacketChunkFlag[]{ PacketChunkFlag.VITAL };
		else
			flags = new PacketChunkFlag[0];
		
		if(client.queueChunk(flags, packet.buffer)){
			if(PacketSendFlag.has(packet.flags, PacketSendFlag.FLUSH))
				client.flush();
		}else
			client.kick(Message.KICK_ERROR.msg, KickCauseType.SERVER);
	}
	
	public void tick() throws Exception {
		this.socket.update();
		
		for(Client client:clients.values())
			client.tick();
	}
	
	public boolean shutdown(){
		if(!isRunning()) return false;
		
		receivers.clear();
		sendQueuedAcks.clear();
		
		return socket.shutdown();
	}
	
	public boolean isConnected(InetAddress address, int port){
		return clients.containsKey(Util.getIdentifier(address, port));
	}
	
	public Client getClient(InetAddress address, int port){
		return clients.get(Util.getIdentifier(address, port));
	}
	
    public static boolean isSequenceInBackroom(int seq, int ack){
        final int bottom = ack - ProtocolHandler.PACKET_MAX_SEQUENCE/2;
        
        if(bottom < 0){
            if(seq <= ack)
                return true;
            else if(seq >= (bottom + ProtocolHandler.PACKET_MAX_SEQUENCE))
                return true;
        }else if (seq <= ack && seq >= bottom)
            return true;

        return false;
    }
    
    public void sendPacket(Packet packet, InetAddress address, int port){
    	sendPacket(address, port, packet.flagsMask, packet.ack, (byte) packet.chunksAmount, packet.data);
    }
    
	private void sendPacket(InetAddress address, int port, byte flagsMask, int ack, byte chunksAmount, byte[] data){
		final byte[] buffer = new byte[PACKET_MAX_SIZE];
		int finalSize = 0;
		int compressedSize = 0;
		
		compressedSize = Util.HUFFMAN.compress(data, 0, data.length, buffer, PACKET_HEADER_SIZE, buffer.length - PACKET_HEADER_SIZE);
		
		if(compressedSize == -1){
			new UnsupportedOperationException("Huffman encoding failed").printStackTrace();
			return;
		}
		
		if(compressedSize > 0 && compressedSize < data.length){
			finalSize = compressedSize;
			flagsMask |= PacketFlag.COMPRESSION.getMask();
		
		}else{
			finalSize = data.length;
			System.arraycopy(data, 0, buffer, PACKET_HEADER_SIZE, data.length);
			flagsMask &= ~PacketFlag.COMPRESSION.getMask();
		}
		
		finalSize += PACKET_HEADER_SIZE;
		
		buffer[0] = (byte) ((((int) flagsMask << 4) & 0xF0) | ((ack >> 8) & 0xF));
		buffer[1] = (byte) (ack & 0xFF);
		buffer[2] = chunksAmount;
		
		this.socket.sendRawPacket(address, port, Util.arraycopy(buffer, 0, finalSize));
	}
}