package de.marcely.rekit.network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import de.marcely.rekit.network.packet.chunk.PacketChunk;
import de.marcely.rekit.network.packet.chunk.PacketChunkFlag;
import de.marcely.rekit.network.packet.chunk.PacketSendFlag;
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
	private Queue<QueuedPacket> receiveQueue = new ConcurrentLinkedQueue<QueuedPacket>(), sendQueue = new ConcurrentLinkedQueue<QueuedPacket>();
	private Map<Byte, QueuedPacket> sendQueuedAcks = new HashMap<>();
	public Map<String, ChunkBuffer> bufferedChunks = new HashMap<>();
	public java.util.Map<Short, Client> clients = new ConcurrentHashMap<>();
	public java.util.Map<String, Client> clients2 = new HashMap<>();
	
	private Timer scheduler;
	
	public ProtocolHandler(int port, Server server){
		this.socket = new UDPSocket(port);
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
				
				receiveQueue.add(new QueuedPacket(address, port, buffer));
			}
		});
		
		if(result){
			// run scheduler
			this.scheduler = new Timer();
			
			final int schedulerRepeating = 1000/TICKS;
			
			this.scheduler.schedule(new TimerTask(){
				public void run(){
					tick();
				}
			}, schedulerRepeating, schedulerRepeating);
		}
		
		return result;
	}
	
	private void handleQueuedPacket(QueuedPacket rawPacket){
		Client client = getClient(rawPacket.getAddress(), rawPacket.getPort());
		byte[] buffer = rawPacket.getBuffer();
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
						rawPacket.getAddress(), rawPacket.getPort());
		
		}else{
			buffer = Util.arraycopy(buffer, PACKET_HEADER_SIZE, buffer.length);
			
			if(PacketFlag.has(flags, PacketFlag.COMPRESSION)){
				if(PacketFlag.has(flags, PacketFlag.CONTROL))
					return;
				
				try{
					buffer = Util.huffmanDecompress(buffer);
				}catch(IOException e){
					e.printStackTrace();
					return;
				}
			}
			
			if(PacketFlag.has(flags, PacketFlag.CONTROL) &&
					buffer[0] == PACKET_TYPE_CONNECT &&
					client == null){
				
				// get amount of client with same ip
				int sameIPsAmount = 0;
				
				for(Client c:clients.values()){
					if(c.getAddress().getAddress().getHostAddress().equals(rawPacket.getAddress().getHostAddress()))
						sameIPsAmount++;
				}
				
				if(sameIPsAmount > this.server.getMaxSameIPsAmount()){
					sendControlPacket(rawPacket.getAddress(), rawPacket.getPort(), 0,
							PACKET_TYPE_CLOSE, "Only " + this.server.getMaxSameIPsAmount() + " players with the same IP are allowed");
					return;
				}
				
				// check if server is full
				if(this.clients.size() >= this.server.getMaxPlayers()){
					sendControlPacket(rawPacket.getAddress(), rawPacket.getPort(), 0,
							PACKET_TYPE_CLOSE, "This server is full");
					return;
				}
				
				// login
				client = new Client(Main.SERVER, new InetSocketAddress(rawPacket.getAddress(), rawPacket.getPort()), getNextBestClientID());
				
				this.clients.put(client.getId(), client);
				this.clients2.put(client.getIdentifier(), client);
				
				sendControlPacket(rawPacket.getAddress(), rawPacket.getPort(), 0,
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
	
	/*private void handlePacket(Packet packet, InetAddress address, int port, @Nullable Client client){
		handlePacket(packet.flags, packet.data, packet.ack, packet.chunksAmount, address, port, client);
	}*/
	
	/*private byte[] handlePacket(PacketFlag[] flags, byte[] buffer, int ack, int chunksAmount, InetAddress address, int port, @Nullable Client client){
		if(PacketFlag.has(flags, PacketFlag.CONNLESS)){
			if(buffer.length < PACKET_DATA_OFFSET)
				return buffer;
			
			buffer = Util.arraycopy(buffer, PACKET_DATA_OFFSET, buffer.length);
			
			handleUnconnectedPacket(new PacketChunk(
					new PacketSendFlag[]{ PacketSendFlag.CONNLESS },
					buffer),
						address, port);
		
		}else{
			if(PacketFlag.has(flags, PacketFlag.COMPRESSION)){
				if(PacketFlag.has(flags, PacketFlag.CONTROL))
					return buffer;
				
				try{
					buffer = Util.huffmanDecompress(Util.arraycopy(buffer, PACKET_HEADER_SIZE, buffer.length));
				}catch(IOException e){
					e.printStackTrace();
					return buffer;
				}
			}else
				buffer = Util.arraycopy(buffer, PACKET_HEADER_SIZE, buffer.length);
			
			if(PacketFlag.has(flags, PacketFlag.CONTROL) &&
					buffer[0] == PACKET_TYPE_CONNECT){
				
				// get amount of client with same ip
				int sameIPsAmount = 0;
				
				for(Client c:clients.values()){
					if(c.getAddress().getAddress().getHostAddress().equals(address.getHostAddress()))
						sameIPsAmount++;
				}
				
				if(sameIPsAmount > this.server.getMaxSameIPsAmount()){
					sendControlPacket(address, port, 0,
							PACKET_TYPE_CLOSE, "Only " + this.server.getMaxSameIPsAmount() + " players with the same IP are allowed");
					return buffer;
				}
				
				// check if server is full
				if(this.clients.size() >= this.server.getMaxPlayers()){
					sendControlPacket(address, port, 0,
							PACKET_TYPE_CLOSE, "This server is full");
					return buffer;
				}
				
				// login
				client = new Client(Main.SERVER, new InetSocketAddress(address, port), getNextBestClientID());
				
				this.clients.put(client.getId(), client);
				this.clients2.put(client.getIdentifier(), client);
				
				sendControlPacket(address, port, 0,
						PACKET_TYPE_CONNECT_ACCEPT, "");
			}
			
			client.handleConnectedPacket(new Packet(
					flags,
					ack,
					chunksAmount,
					buffer));
		}
		
		return buffer;
	}*/
	
	public void sendControlPacket(InetAddress address, int port, int ack, byte type, String extra){
		final byte[] extraRaw = extra.getBytes(StandardCharsets.UTF_8);
		final byte[] buffer = new byte[extraRaw.length+1];
		
		buffer[0] = type;
		System.arraycopy(extraRaw, 0, buffer, 1, extraRaw.length);
		
		sendPacket(address,
				port,
				new PacketFlag[]{ PacketFlag.CONTROL },
				ack,
				(byte) 1,
				buffer);
	}
	
	private void sendPacket(InetAddress address, int port, PacketFlag[] flags, int ack, byte chunksAmount, byte[] data){
		if(PacketFlag.has(flags, PacketFlag.COMPRESSION)){
			try{
				data = Util.huffmanCompress(data);
			}catch(IOException e){
				e.printStackTrace();
				return;
			}
		}
		
		final byte[] buffer = new byte[PACKET_HEADER_SIZE+data.length];
		
		buffer[0] = (byte) ((((int) PacketFlag.toBitMask(flags) << 4) & 0xF0) | ((ack >> 8) & 0xF));
		buffer[1] = (byte) (ack & 0xFF);
		buffer[2] = chunksAmount;
		
		System.arraycopy(data, 0, buffer, PACKET_HEADER_SIZE, data.length);
		
		this.socket.sendRawPacket(address, port, buffer);
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
	
	private void tick(){
		// receive
		QueuedPacket packet = null;
		
		while((packet = receiveQueue.poll()) != null)
			handleQueuedPacket(packet);
		
		// send
		while((packet = sendQueue.poll()) != null)
			socket.sendRawPacket(packet.getAddress(), packet.getPort(), packet.getBuffer());
		
		for(Client client:clients.values())
			client.tick();
	}
	
	public boolean shutdown(){
		if(!isRunning()) return false;
		
		receivers.clear();
		receiveQueue.clear();
		sendQueue.clear();
		sendQueuedAcks.clear();
		
		if(scheduler != null){
			scheduler.cancel();
			scheduler = null;
		}
		
		return socket.shutdown();
	}
	
	public boolean isConnected(InetAddress address, int port){
		return clients2.containsKey(Util.getIdentifier(address, port));
	}
	
	public Client getClient(InetAddress address, int port){
		return clients2.get(Util.getIdentifier(address, port));
	}
	
	private short getNextBestClientID(){
		short id = 0;
		
		while(id++ < Short.MAX_VALUE-1){
			if(clients.containsKey(id)) continue;
			
			return id;
		}
		
		return -1;
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
    	byte[] compressedData = null;
    	final byte[] header = new byte[PACKET_HEADER_SIZE];
    	
    	System.out.println(Util.bytesToHex(packet.data));
    	
    	try{
			compressedData = Util.huffmanCompress(packet.data);
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
    	
        header[0] = (byte) ((((int) (packet.flagsMask | PacketFlag.COMPRESSION.getMask()) << 4) & 0xF0) | ((packet.ack >> 8) & 0xF));
        header[1] = (byte) (packet.ack & 0xFF);
        header[2] = (byte) packet.chunksAmount;
        
        this.socket.sendRawPacket(address, port, Util.concat(header, compressedData));
    }
}