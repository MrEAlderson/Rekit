package de.marcely.rekit.network.packet.serverbrowse;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketServerbrowseInOk extends SilentPacket {

	public PacketServerbrowseInOk(){
		super(PacketType.SERVERBROWSE_IN_OK);
	}
}
