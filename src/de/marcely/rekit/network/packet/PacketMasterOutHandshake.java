package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketMasterOutHandshake extends SilentPacket {

	public PacketMasterOutHandshake(){
		super(PacketType.MASTER_OUT_HANDSHAKE);
	}
}
