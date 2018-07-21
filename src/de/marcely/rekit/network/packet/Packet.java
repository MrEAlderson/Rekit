package de.marcely.rekit.network.packet;

public class Packet {
	
	public PacketFlag[] flags;
	public int ack;
	public int chunksAmount;
	public byte[] data;
	
	public Packet(PacketFlag[] flags, int ack, int chunksAmount, byte[] data){
		this.flags = flags;
		this.ack = ack;
		this.chunksAmount = chunksAmount;
		this.data = data;
	}
}