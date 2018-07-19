package de.marcely.rekit.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.util.IntCompressor;

public class BufferedPacketReader {
	
	private byte[][] parts;
	private int offset = 0;
	
	public BufferedPacketReader(byte[] buffer){
		final List<byte[]> list = new ArrayList<>();
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		// get parts
		for(byte b:buffer){
			if(b != Packet.SEPERATOR)
				stream.write(b);
			else{
				list.add(stream.toByteArray());
				stream.reset();
			}
		}
		
		if(stream.size() >= 1) list.add(stream.toByteArray());
		try{
			stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		parts = list.toArray(new byte[list.size()][]);
	}
	
	public byte[] readPart(){
		return parts[offset++];
	}
	
	public int readPackedInt(){
		return IntCompressor.unpack(readPart(), 0).getValue();
	}
}
