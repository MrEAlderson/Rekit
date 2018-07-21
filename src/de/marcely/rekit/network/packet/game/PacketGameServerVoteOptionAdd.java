package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerVoteOptionAdd extends DataPacket {
	
	public String description;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_VOTE_OPTION_ADD; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeString(this.description);
	}

	@Override
	public void read(PacketInputStream stream){
		this.description = stream.readString();
	}
}
