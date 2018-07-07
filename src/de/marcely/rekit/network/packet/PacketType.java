package de.marcely.rekit.network.packet;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.packet.master.*;
import de.marcely.rekit.network.packet.serverbrowse.*;

public enum PacketType {
	
	// connless
	MASTER_OUT_HANDSHAKE(new byte[]{ 'c', 'o', 'u', '2' }, PacketMasterOutHandshake.class, PacketFlag.CONNLESS),
	MASTER_IN_HANDSHAKE(new byte[]{ 's', 'i', 'z', '2' }, PacketMasterInHandshake.class, PacketFlag.CONNLESS),
	MASTER_OUT_HEARTBEAT(new byte[]{ 'b', 'e', 'a', '2' }, PacketMasterOutHeartbeat.class, PacketFlag.CONNLESS),
	
	SERVERBROWSE_IN_CHECK(new byte[]{ 'f', 'w', '?', '?' }, PacketServerbrowseInCheck.class, PacketFlag.CONNLESS),
	SERVERBROWSE_OUT_RESPONSE(new byte[]{ 'f', 'w', '!', '!' }, PacketServerbrowseOutResponse.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_OK(new byte[]{ 'f', 'w', 'o', 'k' }, PacketServerbrowseInError.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_ERROR(new byte[]{ 'f', 'w', 'e', 'r' }, PacketServerbrowseInError.class, PacketFlag.CONNLESS),
	SERVERBROWSE_IN_GETINFO(new byte[]{ 'g', 'i', 'e', '3' }, PacketServerbrowseInGetInfo.class, PacketFlag.CONNLESS),
	SERVERBROWSE_OUT_INFO(new byte[]{ 'i', 'n', 'f', '3' }, PacketServerbrowseOutInfo.class, PacketFlag.CONNLESS),
	
	GAME_SV_MOTD(1, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_BROADCAST(2, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_CHAT(3, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_KILL_MSG(4, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_SOUND_GLOBAL(5, null, PacketFlag.CONTROL),
	GAME_SV_TUNE_PARAMS(6, null, PacketFlag.CONTROL),
	GAME_SV_EXTRA_PROJECTILE(7, null, PacketFlag.CONTROL),
	GAME_SV_READY_TO_ENTER(8, null, PacketFlag.CONTROL),
	GAME_SV_WEAPON_PICKUP(9, null, PacketFlag.CONTROL),
	GAME_SV_EMOITCON(10, null, PacketFlag.CONTROL),
	GAME_SV_VOTE_CLEAR_OPTIONS(11, null, PacketFlag.CONTROL),
	GAME_SV_VOTE_OPTION_ADD(12, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_VOTE_OPTION_REMOVE(13, null, PacketFlag.CONTROL),
	GAME_SV_VOTE_SET(14, null, PacketFlag.CONTROL),
	GAME_SV_VOTE_STATUS(15, null, PacketFlag.CONTROL),
	GAME_CL_SAY(16, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_SET_TEAM(17, null, PacketFlag.CONTROL),
	GAME_CL_SET_SPECTATOR_MODE(18, null, PacketFlag.CONTROL),
	GAME_CL_START_INFO(19, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_CHANGE_INFO(20, null, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_KILL(21, null, PacketFlag.CONTROL),
	GAME_CL_EMOTICON(22, null, PacketFlag.CONTROL),
	GAME_CL_VOTE(23, null, PacketFlag.CONTROL),
	GAME_CL_CALL_VOTE(24, null, PacketFlag.CONTROL),
	GAME_CL_IS_DDNET(25, null, PacketFlag.CONTROL);
	
	public static final byte CONTROL_KEEPALIVE = 0x0;
	public static final byte CONTROL_CONNECT = 0x1;
	public static final byte CONTROL_CONNECT_ACCEPT = 0x2;
	public static final byte CONTROL_ACCEPT = 0x3;
	public static final byte CONTROL_CLOSE = 0x4;
	
	public int id;
	public byte[] idConnless;
	public final Class<? extends Packet> clazz;
	public final PacketFlag[] flags;
	
	private PacketType(byte[] id, Class<? extends Packet> clazz, PacketFlag... flags){
		this.idConnless = id;
		this.clazz = clazz;
		this.flags = flags;
	}
	
	private PacketType(int id, Class<? extends Packet> clazz, PacketFlag... flags){
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
	
	public static @Nullable PacketType byConnlessData(byte[] data){
		for(PacketType type:values()){
			if(type.idConnless == null) continue;
			
			for(int i=0; i<type.idConnless.length; i++){
				if(type.idConnless[i] == data[i]){
					if(i == type.idConnless.length-1)
						return type;
				}else
					break;
			}
		}
		
		return null;
	}
	
	public static @Nullable PacketType byID(int id){
		System.out.println(id);
		
		for(PacketType type:values()){
			if(type.idConnless != null) continue;
			
			if(type.id == id)
				return type;
		}
		
		return null;
	}
}
