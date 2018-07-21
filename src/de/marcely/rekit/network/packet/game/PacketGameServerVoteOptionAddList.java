package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerVoteOptionAddList extends DataPacket {
	
	public String[] descriptions;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_VOTE_OPTION_ADD; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.descriptions.length);
		
		for(String description:this.descriptions)
			stream.writeString(description);
	}

	@Override
	public void read(PacketInputStream stream){
		this.descriptions = new String[stream.readInt()];
		
		for(int i=0; i<this.descriptions.length; i++)
			this.descriptions[i] = stream.readString();
	}
}
