package de.marcely.rekit.network.packet.chunk;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

public enum PacketChunkFlag {
	
	VITAL((byte) 0x1),
	RESEND((byte) 0x2);
	
	private final byte mask;
	
	private PacketChunkFlag(byte mask){
		this.mask = mask;
	}
	
	public static boolean has(PacketChunkFlag[] flags, PacketChunkFlag flag){
		for(PacketChunkFlag f:flags){
			if(flag == f)
				return true;
		}
		
		return false;
	}
	
	public static byte toBitMask(PacketChunkFlag[] flags){
		byte b = 0x00;
		
		for(PacketChunkFlag flag:flags)
			b |= flag.mask;
		
		return b;
	}
	
	public static @Nullable PacketChunkFlag[] ofBitMask(byte b){
		final List<PacketChunkFlag> flags = new ArrayList<>(values().length);
		
		for(PacketChunkFlag flag:values()){
			if((b&flag.mask) == flag.mask)
				flags.add(flag);
		}
		
		return flags.toArray(new PacketChunkFlag[flags.size()]);
	}
}
