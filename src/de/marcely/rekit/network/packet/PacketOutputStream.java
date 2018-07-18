package de.marcely.rekit.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.marcely.rekit.util.IntCompressor;

public class PacketOutputStream extends ByteArrayOutputStream {
	
	public PacketOutputStream(){ }
	
	public void writeInt(int i){
		try{
			this.write(IntCompressor.pack(i));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeString(String str){
		try{
			this.write(str.getBytes(StandardCharsets.UTF_8));
			this.write(0x00);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeBoolean(boolean bool){
		writeInt(bool ? 1 : 0);
	}
}
