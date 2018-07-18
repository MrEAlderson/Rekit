package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameClientSetSpectatorMode extends DataPacket {
	
	public int spectatorID;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_CL_SET_SPECTATOR_MODE; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.spectatorID);
	}

	@Override
	public void read(PacketInputStream stream){
		this.spectatorID = stream.readInt();
	}
}
