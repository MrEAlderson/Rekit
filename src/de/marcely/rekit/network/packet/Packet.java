package de.marcely.rekit.network.packet;

import java.io.ByteArrayOutputStream;

public class Packet {
	
	public byte flagsMask;
	public int ack;
	public int chunksAmount;
	public byte[] data;
	
	public ByteArrayOutputStream stream;
	
	public Packet(){
		this.stream = new ByteArrayOutputStream();
	}
	
	public Packet(PacketFlag[] flags, int ack, int chunksAmount, byte[] data){
		this.flagsMask = PacketFlag.toBitMask(flags);
		this.ack = ack;
		this.chunksAmount = chunksAmount;
		this.data = data;
	}
	
	public boolean hasFlag(PacketFlag flag){
		return (this.flagsMask & flag.getMask()) == flag.getMask();
	}
}