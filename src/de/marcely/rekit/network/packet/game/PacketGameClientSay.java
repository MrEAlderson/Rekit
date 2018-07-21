package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameClientSay extends DataPacket {
	
	public boolean isTeam;
	public String message;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_CL_SAY; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeBoolean(this.isTeam);
		stream.writeString(this.message);
	}

	@Override
	public void read(PacketInputStream stream){
		this.isTeam = stream.readBoolean();
		this.message = stream.readString();
	}
}
