package de.marcely.rekit.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketChunk;
import lombok.Getter;

public class ProtocolHandler {
	
	private static final int TICKS = 50;
	private static final long RESEND_ACK = 1000;
	private static final long GARBAGE_COLLECTOR = 10000;
	private static final long CHUNKBUFFER_LIFETIME = 30000;
	
	@Getter private final UDPSocket socket;
	
	public List<PacketReceiver> receivers = new ArrayList<PacketReceiver>();
	private Queue<QueuedPacket> receiveQueue = new ConcurrentLinkedQueue<QueuedPacket>(), sendQueue = new ConcurrentLinkedQueue<QueuedPacket>();
	private Map<Byte, QueuedPacket> queuedAcks = new HashMap<>();
	private Map<String, ChunkBuffer> bufferedChunks = new HashMap<>();
	
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
				if(buffer.length < 6) return;
				
				final byte[] data = Arrays.copyOfRange(buffer, 6, buffer.length);
				
				receiveQueue.add(new QueuedPacket(address, port, (byte) 0xFF, null, data));
				/*if(startsWithMagic){
					if(dp.getLength() < 3+Packet.MAGIC.length) return;
					final byte[] data = Arrays.copyOfRange(buffer, Packet.MAGIC.length, dp.getLength());
					final PacketType type = PacketType.byData(data);
					
					if(type != null){
						final Packet packet = type.clazz.newInstance();
						
						packet.readRawData(Arrays.copyOfRange(data, type.id.length, data.length));
						
						for(PacketReceiver receiver:receivers)
							receiver.onReceive(dp.getAddress(), dp.getPort(), packet);
					}
					
				}else{
					final byte[] data = Arrays.copyOfRange(buffer, 0, dp.getLength());
					
					final PacketChunk chunk = PacketChunk.ofData(dp.getAddress(), data);
					System.out.println(chunk.getChunksAmount());
				}*/
				// logger.debug("Received unkown packet (" + Util.bytesToHex(Arrays.copyOfRange(data, 0, 4)) + ", " + dp.getAddress().getHostAddress() + ") [" + new String(data) + "]");
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
	
	private void handle(QueuedPacket rawPacket){
		final PacketChunk chunk = PacketChunk.ofData(new InetSocketAddress(rawPacket.getAddress(), rawPacket.getPort()), rawPacket.getBuffer());
		
		if(chunk.getChunksAmount() == 1)
			fireReceivers(rawPacket);
		else{
			// add to buffer
			final ChunkBuffer buffer = getChunkBuffer(rawPacket.getAddress().getHostAddress() + ":" + rawPacket.getPort());
			
			buffer.getChunks().add(chunk);
			
			// convert chunks to a packet
			if(buffer.getChunks().size() == chunk.getChunksAmount()){
				
			}
		}
	}
	
	private void fireReceivers(QueuedPacket packet){
		
	}
	
	private void schedule(){
		// receive
		QueuedPacket packet = null;
		
		while((packet = receiveQueue.poll()) != null){
			// TODO Send ack if required
			handle(packet);
		}
		
		// send
		while((packet = sendQueue.poll()) != null){
			socket.sendRawPacket(packet.getAddress(), packet.getPort(), packet.getBuffer());
			
			// check ack
			if(packet.getAckID() != -1)
				queuedAcks.put(packet.getAckID(), packet);
		}
		
		// resend ack packets
		if(System.currentTimeMillis() > lastAck+RESEND_ACK){
			for(QueuedPacket packet1:queuedAcks.values())
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
		queuedAcks.clear();
		
		if(scheduler != null){
			scheduler.cancel();
			scheduler = null;
		}
		
		return socket.shutdown();
	}
	
	public boolean sendPacketChunk(PacketChunk chunk, TransferType type){
		// return socket.sendRawPacket(chunk.getAddress().getAddress(), chunk.getAddress().getPort(), chunk.toData(type));
		return sendQueue.add(new QueuedPacket(chunk.getAddress().getAddress(), chunk.getAddress().getPort(), chunk.getAckID(), chunk.getFlag(), chunk.toData(type)));
	}
	
	public int sendPacket(InetAddress address, int port, Packet packet, TransferType type){
		final byte[] rawData = packet.getRawData();
		final int chunksAmount = rawData.length/Packet.MAX_SIZE+1;
		
		int successAmount = 0, triedAmount = 0;
		for(int chunkID=0; chunkID<chunksAmount; chunkID++){
			int size = rawData.length-chunkID*Packet.MAX_SIZE;
			if(size > Packet.MAX_SIZE) size = Packet.MAX_SIZE;
			else if(size == 0 && triedAmount >= 1) return successAmount;
			
			final int offset = chunkID*Packet.MAX_SIZE;
			final PacketChunk chunk = new PacketChunk(new InetSocketAddress(address, port), packet._flag, (byte) -1, (byte) chunksAmount,
					Arrays.copyOfRange(rawData, offset, offset+size));
			
			if(triedAmount == 0) chunk._preBuffer = packet.type.id;
			successAmount += sendPacketChunk(chunk, type) ? 0 : 1;
			triedAmount++;
		}
		
		return successAmount;
	}
}