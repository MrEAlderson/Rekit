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

import de.marcely.rekit.KickReason;
import de.marcely.rekit.Main;
import de.marcely.rekit.network.ChunkBuffer;
import de.marcely.rekit.network.QueuedPacket;
import de.marcely.rekit.network.SocketPump;
import de.marcely.rekit.network.UDPSocket;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.packet.chunk.PacketChunk;
import de.marcely.rekit.network.packet.chunk.PacketSendFlag;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class ProtocolHandler {
	
	public static final int TICKS = 50;
	
	public static final int PACKET_MIN_SIZE = 3;
	public static final int PACKET_MAX_SIZE = 1400;
	public static final int PACKET_HEADER_SIZE = 3;
	public static final int PACKET_DATA_OFFSET = 6;
	public static final int PACKET_MAX_SEQUENCE = 1024;
	
	public static final byte PACKET_TYPE_KEEP_ALIVE = 0;
	public static final byte PACKET_TYPE_CONNECT = 1;
	public static final byte PACKET_TYPE_CONNECT_ACCEPT = 2;
	public static final byte PACKET_TYPE_ACCEPT = 3;
	public static final byte PACKET_TYPE_CLOSE = 4;
	
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
				System.out.println("EEEEEEEEEEEE" + buffer.length);
				if(buffer.length <= PACKET_MIN_SIZE || buffer.length >= PACKET_MAX_SIZE){
					System.out.println("Packet is too big/small: " + buffer.length);
					return;
				}
				
				receiveQueue.add(new QueuedPacket(address, port, (byte) 0xFF, null, buffer));
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
		final Client client = getClient(rawPacket.getAddress(), rawPacket.getPort());
		byte[] buffer = rawPacket.getBuffer();
		final PacketFlag[] flags = PacketFlag.ofBitMask((byte) (buffer[0] >> 4));
		final int ack = ((buffer[0] & 0xF) << 8) | buffer[1];
		final byte chunksAmount = buffer[2];
		
		buffer = handlePacket(flags, buffer, ack, chunksAmount, rawPacket.getAddress(), rawPacket.getPort(), client);
		
		for(PacketChunk packet:PacketChunk.read(buffer, chunksAmount, client))
			handlePacket(flags, packet.buffer, ack, chunksAmount, rawPacket.getAddress(), rawPacket.getPort(), client);
	}
	
	/*private void handlePacket(Packet packet, InetAddress address, int port, @Nullable Client client){
		handlePacket(packet.flags, packet.data, packet.ack, packet.chunksAmount, address, port, client);
	}*/
	
	private byte[] handlePacket(PacketFlag[] flags, byte[] buffer, int ack, int chunksAmount, InetAddress address, int port, @Nullable Client client){
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
			
			handleConnectedPacket(new Packet(
					flags,
					ack,
					chunksAmount,
					buffer), client);
		}
		
		return buffer;
	}
	
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
		System.out.println(Util.bytesToHex(packet.buffer));
		
		/*if(packet.data.length != 9) return;
		
		chunk.buffer = Util.arraycopy(chunk.buffer, 4, 8);
		
		if(Util.compare(chunk.buffer, MasterServerPackets.SERVERBROWSE_GETINFO)){
			
		}else if(Util.compare(chunk.buffer, MasterServerPackets.SERVERBROWSE_GETINFO_64_LEGACY)){
			
		}*/
	}
	
	private void handleConnectedPacket(Packet packet, Client client){
		if(PacketFlag.has(packet.flags, PacketFlag.RESEND))
			resend();
		
		for(PacketFlag flag:packet.flags)
			System.out.println(flag);
		
		if(PacketFlag.has(packet.flags, PacketFlag.CONTROL)){
			final byte type = packet.data[0];
			
			System.out.println(type);
			
			switch(type){
			case PACKET_TYPE_CLOSE:
				String reason = "";
				
				if(packet.data.length >= 2)
					reason = new String(Util.arraycopy(packet.data, 1, packet.data.length), StandardCharsets.UTF_8);
				
				client.kick(new KickReason(reason));
				break;
				
			case PACKET_TYPE_CONNECT:
				if(client.getState() != ClientState.DISCONNECTED) return;
				
				client.setState(ClientState.PENDING);
				
				System.out.println("OK");
				
				sendControlPacket(client.getAddress().getAddress(), client.getAddress().getPort(), 0,
						PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			case PACKET_TYPE_CONNECT_ACCEPT:
				if(client.getState() != ClientState.CONNECT) return;
				
				client.setState(ClientState.ONLINE);
				
				System.out.println("ONLINE!");
				
				sendControlPacket(client.getAddress().getAddress(), client.getAddress().getPort(), 0,
						PACKET_TYPE_ACCEPT, "");
				break;
			}
		}
		
		client.setLastReceivedPacket(System.currentTimeMillis());
	}
	
	private void resend(){
		
	}
	
	private void tick(){
		// receive
		QueuedPacket packet = null;
		
		while((packet = receiveQueue.poll()) != null)
			handleQueuedPacket(packet);
		
		// send
		while((packet = sendQueue.poll()) != null){
			socket.sendRawPacket(packet.getAddress(), packet.getPort(), packet.getBuffer());
			
			// check ack
			if(packet.getAckID() != -1 && packet.getAckID() != 0)
				sendQueuedAcks.put(packet.getAckID(), packet);
		}
		
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
	
	private @Nullable KickReason runCanJoinEvent(InetAddress address, int port){
		for(PacketReceiver receiver:receivers){
			final KickReason reason = receiver.canJoin(address, port);
			
			if(reason != null)
				return reason;
		}
		
		return null;
	}
	
	private short getNextBestClientID(){
		short id = 0;
		
		while(id++ < Short.MAX_VALUE-1){
			if(clients.containsKey(id)) continue;
			
			return id;
		}
		
		return -1;
	}
}