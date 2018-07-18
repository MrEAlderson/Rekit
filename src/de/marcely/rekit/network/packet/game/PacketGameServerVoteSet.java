package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerVoteSet extends DataPacket {
	
	public int timeout;
	public String description;
	public String reason;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_VOTE_SET; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.timeout);
		stream.writeString(this.description);
		stream.writeString(this.reason);
	}

	@Override
	public void read(PacketInputStream stream){
		this.timeout = stream.readInt();
		this.description = stream.readString();
		this.reason = stream.readString();
	}
}
