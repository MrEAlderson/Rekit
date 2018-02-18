package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketServerbrowseOutResponse extends SilentPacket {

	public PacketServerbrowseOutResponse(){
		super(PacketType.SERVERBROWSE_OUT_RESPONSE);
	}
}
