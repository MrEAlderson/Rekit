package de.marcely.rekit.network.packet.chunk;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

public enum PacketSendFlag {
	
	VITAL((byte) 0x1),
	CONNLESS((byte) 0x2),
	FLUSH((byte) 0x4);
	
	private final byte mask;
	
	private PacketSendFlag(byte mask){
		this.mask = mask;
	}
	
	public static boolean has(PacketSendFlag[] flags, PacketSendFlag flag){
		for(PacketSendFlag f:flags){
			if(flag == f)
				return true;
		}
		
		return false;
	}
	
	public static byte toBitMask(PacketSendFlag[] flags){
		byte b = 0x00;
		
		for(PacketSendFlag flag:flags)
			b |= flag.mask;
		
		return b;
	}
	
	public static @Nullable PacketSendFlag[] ofBitMask(byte b){
		final List<PacketSendFlag> flags = new ArrayList<>(values().length);
		
		for(PacketSendFlag flag:values()){
			if((b&flag.mask) == flag.mask)
				flags.add(flag);
		}
		
		return flags.toArray(new PacketSendFlag[flags.size()]);
	}
}
