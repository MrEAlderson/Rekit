package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerReadyToEnter extends DataPacket {

	@Override
	public PacketType getType(){ return PacketType.GAME_SV_READY_TO_ENTER; }

	@Override
	public void write(PacketOutputStream stream){ }

	@Override
	public void read(PacketInputStream stream){ }
}
