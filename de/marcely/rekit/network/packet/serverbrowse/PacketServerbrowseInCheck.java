package de.marcely.rekit.network.packet.serverbrowse;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketServerbrowseInCheck extends SilentPacket {

	public PacketServerbrowseInCheck(){
		super(PacketType.SERVERBROWSE_IN_CHECK);
	}
}
