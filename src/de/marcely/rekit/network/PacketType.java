package de.marcely.rekit.network;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.packet.*;
import de.marcely.rekit.util.Util;

public enum PacketType {
	
	MASTER_OUT_HANDSHAKE(new byte[]{ 'c', 'o', 'u', '2' }, PacketMasterOutHandshake.class, true),
	MASTER_IN_HANDSHAKE(new byte[]{ 's', 'i', 'z', '2' }, PacketMasterInHandshake.class, true),
	MASTER_OUT_HEARTBEAT(new byte[]{ 'b', 'e', 'a', '2' }, PacketMasterOutHeartbeat.class, true),
	SERVERBROWSE_IN_CHECK(new byte[]{ 'f', 'w', '?', '?' }, PacketServerbrowseInCheck.class, true),
	SERVERBROWSE_OUT_RESPONSE(new byte[]{ 'f', 'w', '!', '!' }, PacketServerbrowseOutResponse.class, true),
	SERVERBROWSE_IN_OK(new byte[]{ 'f', 'w', 'o', 'k' }, PacketServerbrowseInError.class, true),
	SERVERBROWSE_IN_ERROR(new byte[]{ 'f', 'w', 'e', 'r' }, PacketServerbrowseInError.class, true),
	SERVERBROWSE_IN_GETINFO(new byte[]{ 'g', 'i', 'e', '3' }, PacketServerbrowseInGetInfo.class, true),
	SERVERBROWSE_OUT_INFO(new byte[]{ 'i', 'n', 'f', '3' }, PacketServerbrowseOutInfo.class, true);
	
	public final byte[] id;
	public final Class<? extends Packet> clazz;
	public final boolean compressed;
	
	private PacketType(byte[] id, Class<? extends Packet> clazz){
		this(id, clazz, false);
	}
	
	private PacketType(byte[] id, Class<? extends Packet> clazz, boolean isServerbrowse){
		if(id.length != 4)
			new IllegalArgumentException().printStackTrace();
		
		if(isServerbrowse)
			id = Util.concat(new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, id);
		
		this.id = id;
		this.clazz = clazz;
		this.compressed = !isServerbrowse;
	}
	
	public static @Nullable PacketType byData(byte[] data){
		if(data.length < 4){
			new ArrayIndexOutOfBoundsException("Packet data is smaller than 4 bytes").printStackTrace();
			return null;
		}
		
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
