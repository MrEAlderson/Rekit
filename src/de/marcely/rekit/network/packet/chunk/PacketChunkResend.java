package de.marcely.rekit.network.packet.chunk;

public class PacketChunkResend {
	
	public final int sequence;
	public final PacketChunkFlag[] flags;
	public final byte[] data;
	public long firstSendTime, lastSendTime;
	
	public PacketChunkResend(int sequence, PacketChunkFlag[] flags, byte[] data, long firstSendTime, long lastSendTime){
		this.sequence = sequence;
		this.flags = flags;
		this.data = data;
		this.firstSendTime = firstSendTime;
		this.lastSendTime = lastSendTime;
	}
}
