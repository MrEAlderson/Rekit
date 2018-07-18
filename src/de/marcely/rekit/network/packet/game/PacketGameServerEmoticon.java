package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerEmoticon extends DataPacket {
	
	public int clientID;
	public int type;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_EMOTICON; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.clientID);
		stream.writeInt(this.type);
	}

	@Override
	public void read(PacketInputStream stream){
		this.clientID = stream.readInt();
		this.type = stream.readInt();
	}
}
