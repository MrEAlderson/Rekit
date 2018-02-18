package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketServerbrowseInCheck extends SilentPacket {

	public PacketServerbrowseInCheck(){
		super(PacketType.SERVERBROWSE_IN_CHECK);
	}
}
