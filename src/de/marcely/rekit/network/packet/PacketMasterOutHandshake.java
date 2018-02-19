package de.marcely.rekit.network.packet;

public class PacketMasterOutHandshake extends SilentPacket {

	public PacketMasterOutHandshake(){
		super(PacketType.MASTER_OUT_HANDSHAKE);
	}
}
