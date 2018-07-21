package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerChat extends DataPacket {
	
	public boolean isTeam;
	public int clientID;
	public String message;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_CHAT; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeBoolean(this.isTeam);
		stream.writeInt(this.clientID);
		stream.writeString(this.message);
	}

	@Override
	public void read(PacketInputStream stream){
		this.isTeam = stream.readBoolean();
		this.clientID = stream.readInt();
		this.message = stream.readString();
	}
}
