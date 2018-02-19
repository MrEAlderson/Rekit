package de.marcely.rekit.network.packet;

public class PacketMasterInHandshake extends SilentPacket {

	public PacketMasterInHandshake(){
		super(PacketType.MASTER_IN_HANDSHAKE);
	}
}
