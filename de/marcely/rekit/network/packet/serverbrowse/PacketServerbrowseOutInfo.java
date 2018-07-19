package de.marcely.rekit.network.packet.serverbrowse;

import de.marcely.rekit.network.BufferedPacketWriter;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.server.ServerInfo;

public class PacketServerbrowseOutInfo extends Packet {
	
	public int token;
	public ServerInfo serverInfo;
	
	public PacketServerbrowseOutInfo(){
		super(PacketType.SERVERBROWSE_OUT_INFO);
	}

	@Override
	public byte[] getRawData(){
		final BufferedPacketWriter writer = new BufferedPacketWriter();
		
		writer.writeToken(token);
		writer.writeString(serverInfo.version, 32);
		writer.writeString(serverInfo.name, 64);
		writer.writeString(serverInfo.map.name, 32);
		writer.writeString(serverInfo.mod, 128);
		writer.writeByte((byte) 0x30); // 0 is password set / skill level
		writer.writeByte((byte) 0x30); // 0 players
		writer.writeByte((byte) 0x38); // 8 max players
		writer.writeByte((byte) 0x30); // 0 clients
		writer.writeByte((byte) 0x38); // 8 max clients
		
		writer.close();
		return writer.toByteArray();
	}

	@Override
	public void readRawData(byte[] data){ }
}
