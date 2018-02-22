package de.marcely.rekit.network.packet.master;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketMasterOutHandshake extends SilentPacket {

	public PacketMasterOutHandshake(){
		super(PacketType.MASTER_OUT_HANDSHAKE);
	}
}
