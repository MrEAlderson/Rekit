package de.marcely.rekit.network.packet.serverbrowse;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketServerbrowseOutResponse extends SilentPacket {

	public PacketServerbrowseOutResponse(){
		super(PacketType.SERVERBROWSE_OUT_RESPONSE);
	}
}
