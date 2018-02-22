package de.marcely.rekit.network.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.Main;
import de.marcely.rekit.network.BufferedPacketWriter;
import de.marcely.rekit.network.ChunkBuffer;
import de.marcely.rekit.network.QueuedPacket;
import de.marcely.rekit.network.SocketPump;
import de.marcely.rekit.network.UDPSocket;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketChunk;
import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class ProtocolHandler {
	
	private static final int TICKS = 50;
	private static final long RESEND_ACK = 1000;
	private static final long GARBAGE_COLLECTOR = 10000;
	private static final long CHUNKBUFFER_LIFETIME = 30000;
	
	@Getter private final UDPSocket socket;
	
	public List<PacketReceiver> receivers = new ArrayList<PacketReceiver>();
	private Queue<QueuedPacket> receiveQueue = new ConcurrentLinkedQueue<QueuedPacket>(), sendQueue = new ConcurrentLinkedQueue<QueuedPacket>();
	private Map<Byte, QueuedPacket> sendQueuedAcks = new HashMap<>();
	private Map<String, ChunkBuffer> bufferedChunks = new HashMap<>();
	private java.util.Map<Short, Client> clients = new HashMap<>();
	private java.util.Map<String, Client> clients2 = new HashMap<>();
	
	private Timer scheduler;
	private long lastAck;
	
	public ProtocolHandler(int port){
		this.socket = new UDPSocket(port);
	}
	
	public boolean isRunning(){
		return this.socket.isRunning();
	}
	
	public boolean run(){
		if(isRunning()) return false;
		
		lastAck = System.currentTimeMillis();
		
		// run socket
		final boolean result = socket.run(new SocketPump(){
			public void receive(InetAddress address, int port, byte[] buffer) throws Exception {
				if(buffer.length < 4){
					System.out.println("too short: " + Util.bytesToHex(buffer));
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
					schedule();
				}
			}, schedulerRepeating, schedulerRepeating);
			
			// garbage collector
			this.scheduler.schedule(new TimerTask(){
				public void run(){
					scheduleGarbageCollector();
				}
			}, GARBAGE_COLLECTOR, GARBAGE_COLLECTOR);
		}
		
		return result;
	}
	
	private void handleQueuedPacket(QueuedPacket rawPacket){
		System.out.println("received:" + Util.bytesToHex(rawPacket.getBuffer()));
		
		final PacketChunk chunk = PacketChunk.ofData(rawPacket.getAddress(), rawPacket.getPort(), rawPacket.getBuffer());
		
		if(chunk == null) return; // corrupted packet
		
		if(chunk.getChunksAmount() <= 1)
			handleRawPacket(rawPacket.getAddress(), rawPacket.getPort(), chunk.getBuffer(), chunk);
		else{
			// add to buffer
			final ChunkBuffer buffer = getChunkBuffer(rawPacket.getAddress().getHostAddress() + ":" + rawPacket.getPort());
			
			buffer.getChunks().add(chunk);
			
			// connect the chunks
			if(buffer.getChunks().size() == chunk.getChunksAmount()){
				final BufferedPacketWriter stream = new BufferedPacketWriter();
				
				for(PacketChunk c:buffer.getChunks())
					stream.write(c.getBuffer());
				
				stream.close();
				
				handleRawPacket(rawPacket.getAddress(), rawPacket.getPort(), stream.toByteArray(), chunk);
			}
		}
	}
	
	private void handleRawPacket(InetAddress address, int port, byte[] data, PacketChunk lastChunk){
		if(lastChunk.getFlags().length == 1 && lastChunk.getFlags()[0] == PacketFlag.CONTROL){
			handleControlPacket(address, port, data);
			return;
		}
		
		// convert to packet
		final PacketType type = PacketType.byData(data);
		
		if(type == null){
			System.out.println("Error: received weird packet (Unkown packet (" + Util.bytesToHex(data) + ")");
			return;
		}
		
		Packet packet = null;
		try{
			packet = type.clazz.newInstance();
		}catch(InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		
		packet.readRawData(Arrays.copyOfRange(data, type.id.length, data.length));
		
		// fire event
		for(PacketReceiver receiver:receivers)
			receiver.onReceive(address, port, packet);
	}
	
	private void handleControlPacket(InetAddress address, int port, byte[] data){
		switch(data[0]){
		case PacketType.CONTROL_CONNECT:
			final KickReason result = runCanJoinEvent(address, port);
			
			if(result == null){
				final Client client = new Client(Main.SERVER, new InetSocketAddress(address, port), getNextBestClientID());
				
				this.clients.put(client.getId(), client);
				this.clients2.put(client.getIdentifier(), client);
				
				for(PacketReceiver receiver:receivers)
					receiver.onConnect(client);
			}else
				sendPacketControl(address, port, PacketType.CONTROL_CLOSE, result.getMessage());
			
			break;
		}
	}
	
	private void schedule(){
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
		
		// resend ack packets
		if(System.currentTimeMillis() > lastAck+RESEND_ACK){
			for(QueuedPacket packet1:sendQueuedAcks.values())
				socket.sendRawPacket(packet1.getAddress(), packet1.getPort(), packet1.getBuffer());
			
			lastAck = System.currentTimeMillis();
		}
	}
	
	private void scheduleGarbageCollector(){
		for(ChunkBuffer buffer:new ArrayList<>(bufferedChunks.values())){
			if(System.currentTimeMillis()-buffer.getLastUpdate() > CHUNKBUFFER_LIFETIME)
				bufferedChunks.remove(buffer.getIdentifier());
		}
	}
	
	private ChunkBuffer getChunkBuffer(String identifier){
		if(bufferedChunks.containsKey(identifier)){
			final ChunkBuffer buffer = bufferedChunks.get(identifier);
			
			buffer.setLastUpdate(System.currentTimeMillis());
			
			return buffer;
		}
		
		final ChunkBuffer buffer = new ChunkBuffer(identifier);
		
		bufferedChunks.put(identifier, buffer);
		
		return buffer;
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
	
	public boolean sendPacketChunk(PacketChunk chunk){
		return sendQueue.add(new QueuedPacket(chunk.getAddress(), chunk.getPort(), chunk.getAckID(), chunk.getFlags(), chunk.toData()));
	}
	
	public int sendPacket(InetAddress address, int port, Packet packet){
		final byte[] rawData = packet.getRawData();
		final int chunksAmount = rawData.length/Packet.MAX_SIZE+1;
		
		int successAmount = 0, triedAmount = 0;
		for(int chunkID=0; chunkID<chunksAmount; chunkID++){
			int size = rawData.length-chunkID*Packet.MAX_SIZE;
			if(size > Packet.MAX_SIZE) size = Packet.MAX_SIZE;
			else if(size == 0 && triedAmount >= 1) return successAmount;
			
			final int offset = chunkID*Packet.MAX_SIZE;
			final PacketChunk chunk = new PacketChunk(address, port, packet.type.flags, (byte) -1, (byte) chunksAmount,
					Arrays.copyOfRange(rawData, offset, offset+size));
			
			if(triedAmount == 0) chunk._preBuffer = packet.type.id;
			successAmount += sendPacketChunk(chunk) ? 0 : 1;
			triedAmount++;
		}
		
		return successAmount;
	}
	
	public void sendPacketControl(InetAddress address, int port, byte id, @Nullable String extra){
		if(extra == null) extra = "";
		
		sendPacketChunk(new PacketChunk(address, port, new PacketFlag[]{ PacketFlag.CONTROL }, (byte) 0x00, (byte) 1,
				Util.concat(new byte[]{ id }, extra.getBytes(StandardCharsets.UTF_8))));
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