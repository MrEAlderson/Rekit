package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketServerbrowseInError extends SilentPacket {

	public PacketServerbrowseInError(){
		super(PacketType.SERVERBROWSE_IN_ERROR);
	}
}
