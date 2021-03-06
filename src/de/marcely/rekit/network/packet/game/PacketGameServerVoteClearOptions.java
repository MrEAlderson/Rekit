package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerVoteClearOptions extends DataPacket {

	@Override
	public PacketType getType(){ return PacketType.GAME_SV_VOTE_CLEAR_OPTIONS; }

	@Override
	public void write(PacketOutputStream stream){ }

	@Override
	public void read(PacketInputStream stream){ }
}
