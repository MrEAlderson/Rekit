package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerVoteStatus extends DataPacket {
	
	public int yesAmount;
	public int noAmount;
	public int passAmount;
	public int totalAmount;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_VOTE_STATUS; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.yesAmount);
		stream.writeInt(this.noAmount);
		stream.writeInt(this.passAmount);
		stream.writeInt(this.totalAmount);
	}

	@Override
	public void read(PacketInputStream stream){
		this.yesAmount = stream.readInt();
		this.noAmount = stream.readInt();
		this.passAmount = stream.readInt();
		this.totalAmount = stream.readInt();
	}
}
