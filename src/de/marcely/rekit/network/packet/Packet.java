package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public abstract class Packet {
	
	public static final byte[] MAGIC = new byte[]
			{ (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
	public static final byte SEPERATOR = 0x00;
	
	public final PacketType type;
	
	public Packet(PacketType type){
		this.type = type;
	}
	
	public abstract byte[] getRawData();
	
	public abstract void readRawData(byte[] data);
}
