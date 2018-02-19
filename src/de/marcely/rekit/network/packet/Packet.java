package de.marcely.rekit.network.packet;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.util.Util;

public abstract class Packet {
	
	public static final byte SEPERATOR = 0x00;
	public static final int HEADER_SIZE = 6;
	public static final int MAX_SIZE = 1400-HEADER_SIZE;
	
	public final PacketType type;
	
	public PacketFlag _flag = PacketFlag.NONE;
	public boolean _ack;
	
	public Packet(PacketType type){
		this.type = type;
	}
	
	public abstract byte[] getRawData();
	
	public abstract void readRawData(byte[] data);
	
	public static @Nullable Packet concact(PacketType type, PacketChunk[] chunks){
		byte[] data = new byte[0];
		
		for(int i=0; i<chunks.length; i++)
			data = Util.concat(data, chunks[i].getBuffer());
		
		try{
			final Packet packet = type.clazz.newInstance();
			packet.readRawData(data);
			
			return packet;
		}catch(InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		}
		
		return null;
	}
}
