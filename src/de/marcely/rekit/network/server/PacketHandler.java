package de.marcely.rekit.network.server;

import de.marcely.rekit.network.packet.game.*;
import de.marcely.rekit.util.BufferedReadStream;

public interface PacketHandler {
	
	public static final int MSG_CL_INFO = 1;
	
	public static final int MSG_SV_MAP_CHANGE = 2;
	public static final int MSG_SV_MAP_DATA = 3;
	public static final int MSG_SV_CON_READY = 4;
	public static final int MSG_SV_SNAP = 5;
	public static final int MSG_SV_SNAP_EMPTY = 6;
	public static final int MSG_SV_SNAP_SINGLE = 7;
	public static final int MSG_SV_SNAP_SMALL = 8;
	public static final int MSG_SV_INPUT_TIMINGS = 9;
	public static final int MSG_SV_RCON_AUTH_STATUS = 10;
	public static final int MSG_SV_RCON_LINE = 11;
	
	public static final int MSG_AUTH_CHALLANGE = 12;
	public static final int MSG_AUTH_RESULT = 13;
	
	public static final int MSG_CL_READY = 14;
	public static final int MSG_CL_ENTER_GAME = 15;
	public static final int MSG_CL_INPUT = 16;
	public static final int MSG_CL_RCON_CMD = 17;
	public static final int MSG_CL_RCON_AUTH = 18;
	public static final int MSG_CL_REQUEST_MAP_DATA = 19;
	public static final int MSG_CL_AUTH_START = 20;
	public static final int MSG_CL_AUTH_RESPONSE = 21;
	
	public static final int MSG_PING = 22;
	public static final int MSG_PONG = 23;
	public static final int MSG_ERROR = 24;
	public static final int MSG_RCON_CMD_ADD = 25;
	public static final int MSG_RCON_CMD_REMOVE = 26;

	
	public void handleMsgInfo(BufferedReadStream stream);
	
	public void handleMsgRequestMapData(BufferedReadStream stream);
	
	public void handleMsgReady(BufferedReadStream stream);
	
	public void handleMsgEnterGame(BufferedReadStream stream);
	
	public void handleMsgInput(BufferedReadStream stream);
	
	public void handleMsgRconCMD(BufferedReadStream stream);
	
	public void handleMsgRconAuth(BufferedReadStream stream);
	
	public void handleMsgPing(BufferedReadStream stream);
	
	
	public void handleData(PacketGameClientCallVote packet);
	
	public void handleData(PacketGameClientChangeInfo packet);
	
	public void handleData(PacketGameClientEmoticon packet);
	
	public void handleData(PacketGameClientIsDDNet packet);
	
	public void handleData(PacketGameClientKill packet);
	
	public void handleData(PacketGameClientSay packet);
	
	public void handleData(PacketGameClientSetSpectatorMode packet);
	
	public void handleData(PacketGameClientSetTeam packet);
	
	public void handleData(PacketGameClientStartInfo packet);
	
	public void handleData(PacketGameClientVote packet);
}
