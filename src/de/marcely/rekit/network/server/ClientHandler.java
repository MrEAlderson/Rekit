package de.marcely.rekit.network.server;

import java.awt.Color;

import de.marcely.rekit.Message;
import de.marcely.rekit.TWWorld;
import de.marcely.rekit.entity.EntityPlayer;
import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketSendFlag;
import de.marcely.rekit.network.packet.PacketType;
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
import de.marcely.rekit.network.packet.game.PacketGameServerReadyToEnter;
import de.marcely.rekit.network.packet.game.PacketGameServerTuneParams;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.SpawnCause;
import de.marcely.rekit.plugin.player.KickCauseType;
import de.marcely.rekit.util.BufferedReadStream;
import de.marcely.rekit.util.BufferedWriteStream;
import de.marcely.rekit.util.TWStream.SanitizeType;
import de.marcely.rekit.util.Vector2;

public class ClientHandler implements PacketHandler {

	private final Client client;
	
	public ClientHandler(Client client){
		this.client = client;
	}
	
	@Override
	public void handleMsgInfo(BufferedReadStream stream){
		if(client.serverState != ServerClientState.AUTH)
			return;
		
		client.gameVersion = stream.readTWString(SanitizeType.SANITIZE_CC);
		
		if(!client.gameVersion.startsWith(client.getServer().getNetworkVersion())){
			this.client.kick(Message.KICK_WRONG_VERSION.msg
					.replace("%1", client.getServer().getNetworkVersion())
					.replace("%2", client.gameVersion), KickCauseType.SERVER);
			return;
		}
		
		final String password = stream.readTWString(SanitizeType.SANITIZE_CC);
		
		if(client.getServer().isPasswordEnabled() && !client.getServer().getPassword().equals(password)){
			this.client.kick(Message.KICK_WRONG_PASSWORD.msg, KickCauseType.SERVER);
			return;
		}
		
		client.serverState = ServerClientState.CONNECTING;
		
		sendMap();
	}

	@Override
	public void handleMsgRequestMapData(BufferedReadStream stream){
		
	}

	@Override
	public void handleMsgReady(BufferedReadStream stream){
		if(this.client.serverState != ServerClientState.CONNECTING)
			return;
		
		this.client.serverState = ServerClientState.READY;
		
		final BufferedWriteStream outStream = new BufferedWriteStream();
		
		outStream.writeTWInt(PacketHandler.MSG_SV_CON_READY);
		
		this.client.sendMsgEx(outStream, true, PacketSendFlag.VITAL, PacketSendFlag.FLUSH);
	}

	@Override
	public void handleMsgEnterGame(BufferedReadStream stream){
		if(this.client.serverState != ServerClientState.READY)
			return;
		
		System.out.println("Player with the ID " + client.getId() + " entered the game");
		
		final EntityPlayer player = this.client.getServer().getWorld().spawn(EntityType.PLAYER, new Vector2(0, 0), SpawnCause.WOLRLD);
		
		player.client = this.client;
		player.reset();
		player.respawn();
		
		this.client.player = player;
		this.client.serverState = ServerClientState.IN_GAME;
		((TWWorld) this.client.getServer().getWorld()).players.add(player);
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
		this.client.gameLastChangedInfo = System.currentTimeMillis();
		this.client.gameName = packet.name;
		this.client.gameClan = packet.clan;
		this.client.gameCountry = packet.country;
		this.client.gameSkinName = packet.skin;
		this.client.gameHasCustomColor = packet.hasCustomColor;
		this.client.gameBodyColor = new Color(packet.bodyColor);
		this.client.gameFeetColor = new Color(packet.feetColor);
		
		sendTunings();
		this.client.sendDataPacket(new PacketGameServerReadyToEnter(), PacketSendFlag.VITAL, PacketSendFlag.FLUSH);
	}

	@Override
	public void handleData(PacketGameClientVote packet){
		
	}
	
	public void handleData(DataPacket packet){
		if(client.serverState == ServerClientState.READY && packet.getType() != PacketType.GAME_CL_START_INFO)
			return;
		
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
	
	public void sendMap(){
		final BufferedWriteStream stream = new BufferedWriteStream();
		
		stream.writeTWInt(PacketHandler.MSG_SV_MAP_CHANGE);
		stream.writeTWString(client.getServer().getMap().getName());
		stream.writeTWInt((int) client.getServer().getMap().getChecksum());
		stream.writeTWInt(client.getServer().getMap().getSize());
		
		this.client.sendMsgEx(stream, true, PacketSendFlag.VITAL, PacketSendFlag.FLUSH);
	}
	
	public void sendTunings(){
		final PacketGameServerTuneParams packet = new PacketGameServerTuneParams();
		
		packet.values = this.client.getServer().getWorld().getTuningParameterValues();
		
		this.client.sendDataPacket(packet, PacketSendFlag.VITAL);
	}
}
