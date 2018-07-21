package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerMotd extends DataPacket {
	
	public String message;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_MOTD; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeString(this.message);
	}

	@Override
	public void read(PacketInputStream stream){
		this.message = stream.readString();
	}
}
