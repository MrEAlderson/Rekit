package de.marcely.rekit.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.util.IntCompressor;

public class BufferedPacketWriter extends ByteArrayOutputStream {
	public void write(byte[] data){
		for(byte b:data)
			write(b);
		
		write(Packet.SEPERATOR);
	}
	
	public void writeByte(byte b){
		write(new byte[]{ b });
	}
	
	public void writeString(String str){
		write(str.getBytes());
	}
	
	public void writePackedInt(int i){
		write(IntCompressor.pack(i));
	}
	
	public void writeSignedInt(int i){
		write(ByteBuffer.allocate(4).putInt(i).array());
	}
	
	public void writeUnsignedByte(int b){
		write(b & 0xFF);
		write(Packet.SEPERATOR);
	}
	
	public void writeUnsignedInt(long u){
		writeSignedInt((int) u);
	}
	
	public void writeToken(int token){
		final String[] parts = Integer.toString(token).split("");
		final byte[] result = new byte[parts.length];
		
		for(int i=0; i<parts.length; i++)
			result[i] = (byte) (Byte.parseByte(parts[i]) + 0x30);
		
		write(result);
	}
	
	@Override
	public void close(){
		try{
			super.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
