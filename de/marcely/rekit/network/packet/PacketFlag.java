package de.marcely.rekit.network.packet;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import lombok.Getter;

public enum PacketFlag {
	
	CONTROL((byte) 0x1),
	CONNLESS((byte) 0x2),
	RESEND((byte) 0x4),
	COMPRESSION((byte) 0x8);
	
	@Getter private final byte mask;
	
	private PacketFlag(byte mask){
		this.mask = mask;
	}
	
	public static boolean has(PacketFlag[] flags, PacketFlag flag){
		for(PacketFlag f:flags){
			if(flag == f)
				return true;
		}
		
		return false;
	}
	
	public static byte toBitMask(PacketFlag[] flags){
		byte b = 0x00;
		
		for(PacketFlag flag:flags)
			b |= flag.mask;
		
		return b;
	}
	
	public static @Nullable PacketFlag[] ofBitMask(byte b){
		final List<PacketFlag> flags = new ArrayList<>(values().length);
		
		for(PacketFlag flag:values()){
			if((b&flag.mask) == flag.mask)
				flags.add(flag);
		}
		
		return flags.toArray(new PacketFlag[flags.size()]);
	}
}
