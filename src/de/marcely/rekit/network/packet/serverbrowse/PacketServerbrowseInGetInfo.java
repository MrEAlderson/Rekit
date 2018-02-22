package de.marcely.rekit.network.packet.serverbrowse;

import java.util.Map.Entry;

import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.network.packet.SilentPacket;
import de.marcely.rekit.util.IntCompressor;

public class PacketServerbrowseInGetInfo extends SilentPacket {
	
	public Integer token;
	
	public PacketServerbrowseInGetInfo(){
		super(PacketType.SERVERBROWSE_IN_GETINFO);
	}
	
	@Override
	public void readRawData(byte[] data){
		if(data.length >= 1){
			final Entry<Integer, Integer> e = IntCompressor.unpack(data, 0);
			
			if(e != null)
				this.token = IntCompressor.unpack(data, 0).getValue();
		}
	}
}
