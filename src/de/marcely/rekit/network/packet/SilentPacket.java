package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class SilentPacket extends Packet {

	public SilentPacket(PacketType type){
		super(type);
	}

	@Override
	public byte[] getRawData(){ return new byte[0]; }

	@Override
	public void readRawData(byte[] data){ }
}
