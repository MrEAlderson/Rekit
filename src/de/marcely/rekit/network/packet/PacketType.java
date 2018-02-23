package de.marcely.rekit.network.packet;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.packet.master.*;
import de.marcely.rekit.network.packet.serverbrowse.*;

public enum PacketType {
	
	MASTER_OUT_HANDSHAKE(new byte[]{ 'c', 'o', 'u', '2' }, PacketMasterOutHandshake.class, PacketFlag.CONNLESS),
	MASTER_IN_HANDSHAKE(new byte[]{ 's', 'i', 'z', '2' }, PacketMasterInHandshake.class, PacketFlag.CONNLESS),
	MASTER_OUT_HEARTBEAT(new byte[]{ 'b', 'e', 'a', '2' }, PacketMasterOutHeartbeat.class, PacketFlag.CONNLESS),
	
	SERVERBROWSE_IN_CHECK(new byte[]{ 'f', 'w', '?', '?' }, PacketServerbrowseInCheck.class, PacketFlag.CONNLESS),
	SERVERBROWSE_OUT_RESPONSE(new byte[]{ 'f', 'w', '!', '!' }, PacketServerbrowseOutResponse.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_OK(new byte[]{ 'f', 'w', 'o', 'k' }, PacketServerbrowseInError.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_ERROR(new byte[]{ 'f', 'w', 'e', 'r' }, PacketServerbrowseInError.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_GETINFO(new byte[]{ 'g', 'i', 'e', '3' }, PacketServerbrowseInGetInfo.class, PacketFlag.CONNLESS),
	SERVERBROWSE_OUT_INFO(new byte[]{ 'i', 'n', 'f', '3' }, PacketServerbrowseOutInfo.class, PacketFlag.CONNLESS);
	
	public static final byte CONTROL_KEEPALIVE = 0x0;
	public static final byte CONTROL_CONNECT = 0x1;
	public static final byte CONTROL_CONNECT_ACCEPT = 0x2;
	public static final byte CONTROL_ACCEPT = 0x3;
	public static final byte CONTROL_CLOSE = 0x4;
	
	public final byte[] id;
	public final Class<? extends Packet> clazz;
	public final PacketFlag[] flags;
	
	private PacketType(byte[] id, Class<? extends Packet> clazz, PacketFlag... flags){
		this.id = id;
		this.clazz = clazz;
		this.flags = flags;
	}
	
	public boolean hasFlag(PacketFlag flag){
		for(PacketFlag f:flags){
			if(f == flag)
				return true;
		}
		
		return false;
	}
	
	public static @Nullable PacketType byData(byte[] data){
		for(PacketType type:values()){
			for(int i=0; i<type.id.length; i++){
				if(type.id[i] == data[i]){
					if(i == type.id.length-1)
						return type;
				}else
					break;
			}
		}
		
		return null;
	}
}
