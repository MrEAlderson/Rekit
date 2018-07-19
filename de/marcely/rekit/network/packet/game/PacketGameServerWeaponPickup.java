package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;

public class PacketGameServerWeaponPickup extends DataPacket {
	
	public int type;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_WEAPON_PICKUP; }

	@Override
	public void write(PacketOutputStream stream){
		stream.writeInt(this.type);
	}

	@Override
	public void read(PacketInputStream stream){
		this.type = stream.readInt();
	}
}
