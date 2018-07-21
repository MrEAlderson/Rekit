package de.marcely.rekit.network.packet;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.packet.game.PacketGameClientCallVote;
import de.marcely.rekit.network.packet.game.PacketGameClientChangeInfo;
import de.marcely.rekit.network.packet.game.PacketGameClientEmoticon;
import de.marcely.rekit.network.packet.game.PacketGameClientIsDDNet;
import de.marcely.rekit.network.packet.game.PacketGameClientKill;
import de.marcely.rekit.network.packet.game.PacketGameClientSay;
import de.marcely.rekit.network.packet.game.PacketGameClientSetSpectatorMode;
import de.marcely.rekit.network.packet.game.PacketGameClientSetTeam;
import de.marcely.rekit.network.packet.game.PacketGameClientStartInfo;
import de.marcely.rekit.network.packet.game.PacketGameClientVote;

public enum PacketType {
	
	// connless
	GAME_SV_MOTD(1, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_BROADCAST(2, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_CHAT(3, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_KILL_MSG(4, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_SOUND_GLOBAL(5, PacketFlag.CONTROL),
	GAME_SV_TUNE_PARAMS(6, PacketFlag.CONTROL),
	GAME_SV_EXTRA_PROJECTILE(7, PacketFlag.CONTROL),
	GAME_SV_READY_TO_ENTER(8, PacketFlag.CONTROL),
	GAME_SV_WEAPON_PICKUP(9, PacketFlag.CONTROL),
	GAME_SV_EMOTICON(10, PacketFlag.CONTROL),
	GAME_SV_VOTE_CLEAR_OPTIONS(11, PacketFlag.CONTROL),
	GAME_SV_VOTE_OPTION_ADD_LIST(12, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_VOTE_OPTION_ADD(13, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_SV_VOTE_OPTION_REMOVE(14, PacketFlag.CONTROL),
	GAME_SV_VOTE_SET(15, PacketFlag.CONTROL),
	GAME_SV_VOTE_STATUS(16, PacketFlag.CONTROL),
	GAME_CL_SAY(17, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_SET_TEAM(18, PacketFlag.CONTROL),
	GAME_CL_SET_SPECTATOR_MODE(19, PacketFlag.CONTROL),
	GAME_CL_START_INFO(20, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_CHANGE_INFO(21, PacketFlag.CONTROL, PacketFlag.COMPRESSION),
	GAME_CL_KILL(22, PacketFlag.CONTROL),
	GAME_CL_EMOTICON(23, PacketFlag.CONTROL),
	GAME_CL_VOTE(24, PacketFlag.CONTROL),
	GAME_CL_CALL_VOTE(25, PacketFlag.CONTROL),
	GAME_CL_IS_DDNET(26, PacketFlag.CONTROL);
	
	public static final byte CONTROL_KEEPALIVE = 0x0;
	public static final byte CONTROL_CONNECT = 0x1;
	public static final byte CONTROL_CONNECT_ACCEPT = 0x2;
	public static final byte CONTROL_ACCEPT = 0x3;
	public static final byte CONTROL_CLOSE = 0x4;
	
	public int id;
	public final PacketFlag[] flags;
	
	private PacketType(int id, PacketFlag... flags){
		this.id = id;
		this.flags = flags;
	}
	
	public boolean hasFlag(PacketFlag flag){
		for(PacketFlag f:flags){
			if(f == flag)
				return true;
		}
		
		return false;
	}
	
	public static @Nullable PacketType ofID(int id){
		for(PacketType type:values()){
			if(type.id == id)
				return type;
		}
		
		return null;
	}
	
	public @Nullable DataPacket newClientDataPacketInstance(){
		switch(this){
		case GAME_CL_SAY:
			return new PacketGameClientSay();
		case GAME_CL_SET_TEAM:
			return new PacketGameClientSetTeam();
		case GAME_CL_SET_SPECTATOR_MODE:
			return new PacketGameClientSetSpectatorMode();
		case GAME_CL_START_INFO:
			return new PacketGameClientStartInfo();
		case GAME_CL_CHANGE_INFO:
			return new PacketGameClientChangeInfo();
		case GAME_CL_KILL:
			return new PacketGameClientKill();
		case GAME_CL_EMOTICON:
			return new PacketGameClientEmoticon();
		case GAME_CL_VOTE:
			return new PacketGameClientVote();
		case GAME_CL_CALL_VOTE:
			return new PacketGameClientCallVote();
		case GAME_CL_IS_DDNET:
			return new PacketGameClientIsDDNet();
		default:
			return null;
		}
	}
}
