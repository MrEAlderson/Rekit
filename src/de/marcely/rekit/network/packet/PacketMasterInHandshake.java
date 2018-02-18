package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketMasterInHandshake extends SilentPacket {

	public PacketMasterInHandshake(){
		super(PacketType.MASTER_IN_HANDSHAKE);
	}
}
