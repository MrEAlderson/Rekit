package de.marcely.rekit.network.packet.master;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketMasterInHandshake extends SilentPacket {

	public PacketMasterInHandshake(){
		super(PacketType.MASTER_IN_HANDSHAKE);
	}
}
