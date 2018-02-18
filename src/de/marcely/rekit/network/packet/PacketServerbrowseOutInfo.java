package de.marcely.rekit.network.packet;

import de.marcely.rekit.network.BufferedPacketWriter;
import de.marcely.rekit.network.PacketType;
import de.marcely.rekit.network.ServerInfo;

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
		writer.writeString(serverInfo.version);
		writer.writeString(serverInfo.name);
		writer.writeString(serverInfo.map.name);
		writer.writeString(serverInfo.mod);
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
	
	// TODO: Improve it
	/*private byte[] getKey(String hex){
		if(hex.equals("00"))
			return new byte[]{ 0x30 };
		
		final List<Byte> bytes = new ArrayList<Byte>();
		
		for(int ci=0; ci<hex.length(); ci++){
			final char c = hex.charAt(ci);
			
			if(c == '0') continue;
			
			if(c >= '0' && c <= '9')
				bytes.add((byte) (0x30+c));
			else if(c == 'a'){
				bytes.add((byte) 0x31);
				bytes.add((byte) 0x36);
			}else if(c == 'b'){
				bytes.add((byte) 0x31);
			}
		}
	}*/
}
