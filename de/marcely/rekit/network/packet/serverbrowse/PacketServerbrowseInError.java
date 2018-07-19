package de.marcely.rekit.network.packet.serverbrowse;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;

public class PacketServerbrowseInError extends SilentPacket {

	public PacketServerbrowseInError(){
		super(PacketType.SERVERBROWSE_IN_ERROR);
	}
}
