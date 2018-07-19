package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerKillMsg extends DataPacket {

	public int killerClientID;
	public int victimClientID;
	public int weaponType;
	public int specialMode;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_KILL_MSG; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.killerClientID);
		stream.writeInt(this.victimClientID);
		stream.writeInt(this.weaponType);
		stream.writeInt(this.specialMode);
	}

	@Override
	public void read(PacketInputStream stream){
		this.killerClientID = stream.readInt();
		this.victimClientID = stream.readInt();
		this.weaponType = stream.readInt();
		this.specialMode = stream.readInt();
	}
}
