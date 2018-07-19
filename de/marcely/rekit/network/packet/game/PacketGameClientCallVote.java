package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameClientCallVote extends DataPacket {
	
	public String type;
	public String value;
	public String reason;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_CL_CALL_VOTE; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeString(this.type);
		stream.writeString(this.value);
		stream.writeString(this.reason);
	}

	@Override
	public void read(PacketInputStream stream){
		this.type = stream.readString();
		this.value = stream.readString();
		this.reason = stream.readString();
	}
}
