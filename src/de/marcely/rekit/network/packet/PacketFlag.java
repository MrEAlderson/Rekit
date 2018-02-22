package de.marcely.rekit.network.packet;

import com.sun.istack.internal.Nullable;

import lombok.Getter;

public enum PacketFlag {
	
	CONTROL((byte) 0x1),
	CONNLESS((byte) 0x2),
	RESEND((byte) 0x4),
	COMPRESSION((byte) 0x8);
	
	@Getter private final byte id;
	
	private PacketFlag(byte id){
		this.id = id;
	}
	
	public static @Nullable PacketFlag ofID(byte id){
		for(PacketFlag flag:values()){
			if(flag.id == id)
				return flag;
		}
		
		return null;
	}
}
