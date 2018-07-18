package de.marcely.rekit.network.packet;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import de.marcely.rekit.util.IntCompressor;

public class PacketInputStream extends ByteArrayInputStream {
	
	public PacketInputStream(byte[] buf){
		super(buf);
	}

	public int readInt(){
		final Entry<Integer, Integer> result = IntCompressor.unpack(this.buf, this.pos);
		
		this.pos = result.getKey();
		
		return result.getValue();
	}
	
	public String readString(){
		int end = this.pos;
		
		while(this.buf[end] != 0x00)
			end++;
		
		final byte[] result = new byte[end-this.pos];
		
		if(result.length > 0)
			System.arraycopy(this.buf, this.pos, result, 0, result.length);
		
		this.pos = end+1;
		
		return new String(result, StandardCharsets.UTF_8);
	}
	
	public boolean readBoolean(){
		return readInt() >= 1;
	}
}
