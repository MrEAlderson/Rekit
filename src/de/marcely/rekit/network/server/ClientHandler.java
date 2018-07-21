package de.marcely.rekit.network.server;

import de.marcely.rekit.network.packet.DataPacket;
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
import de.marcely.rekit.util.BufferedReadStream;

public class ClientHandler implements PacketHandler {

	private final Client client;
	
	public ClientHandler(Client client){
		this.client = client;
	}
	
	@Override
	public void handleMsgInfo(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgRequestMapData(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgReady(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgEnterGame(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgInput(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgRconCMD(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgRconAuth(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgPing(BufferedReadStream stream){
		
	}

	@Override
	public void handleData(PacketGameClientCallVote packet){
		
	}

	@Override
	public void handleData(PacketGameClientChangeInfo packet){
		
	}

	@Override
	public void handleData(PacketGameClientEmoticon packet){
		
	}

	@Override
	public void handleData(PacketGameClientIsDDNet packet){
		
	}

	@Override
	public void handleData(PacketGameClientKill packet){
		
	}

	@Override
	public void handleData(PacketGameClientSay packet){
		
	}

	@Override
	public void handleData(PacketGameClientSetSpectatorMode packet){
		
	}

	@Override
	public void handleData(PacketGameClientSetTeam packet){
		
	}

	@Override
	public void handleData(PacketGameClientStartInfo packet){
		
	}

	@Override
	public void handleData(PacketGameClientVote packet){
		
	}
	
	public void handleData(DataPacket packet){
		switch(packet.getType()){
		case GAME_CL_SAY:
			handleData((PacketGameClientSay) packet);
			break;
		
		case GAME_CL_SET_TEAM:
			handleData((PacketGameClientSetTeam) packet);
			break;
		
		case GAME_CL_SET_SPECTATOR_MODE:
			handleData((PacketGameClientSetSpectatorMode) packet);
			break;
		
		case GAME_CL_START_INFO:
			handleData((PacketGameClientStartInfo) packet);
			break;
		
		case GAME_CL_CHANGE_INFO:
			handleData((PacketGameClientChangeInfo) packet);
			break;
		
		case GAME_CL_KILL:
			handleData((PacketGameClientKill) packet);
			break;
		
		case GAME_CL_EMOTICON:
			handleData((PacketGameClientEmoticon) packet);
			break;
		
		case GAME_CL_VOTE:
			handleData((PacketGameClientVote) packet);
			break;
		
		case GAME_CL_CALL_VOTE:
			handleData((PacketGameClientCallVote) packet);
			break;
		
		case GAME_CL_IS_DDNET:
			handleData((PacketGameClientIsDDNet) packet);
			break;
		
		default:
			break;
		}
	}
}
