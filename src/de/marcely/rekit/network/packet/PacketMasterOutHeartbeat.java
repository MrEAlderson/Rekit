package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.PacketType;

public class PacketMasterOutHeartbeat extends Packet {

	public PacketMasterOutHeartbeat(){
		super(PacketType.MASTER_OUT_HEARTBEAT);
	}

	@Override
	public byte[] getRawData(){ return new byte[]{ (byte) 0x20, (byte) 0x70 }; }

	@Override
	public void readRawData(byte[] data){ }
}
