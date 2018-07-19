package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameClientChangeInfo extends DataPacket {

	public String name;
	public String clan;
	public int country;
	public String skin;
	public boolean hasCustomColor;
	public int bodyColor;
	public int feetColor;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_CL_CHANGE_INFO; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeString(this.name);
		stream.writeString(this.clan);
		stream.writeInt(this.country);
		stream.writeString(this.skin);
		stream.writeBoolean(this.hasCustomColor);
		stream.writeInt(this.bodyColor);
		stream.writeInt(this.feetColor);
	}

	@Override
	public void read(PacketInputStream stream){
		this.name = stream.readString();
		this.clan = stream.readString();
		this.country = stream.readInt();
		this.skin = stream.readString();
		this.hasCustomColor = stream.readBoolean();
		this.bodyColor = stream.readInt();
		this.feetColor = stream.readInt();
	}
}
