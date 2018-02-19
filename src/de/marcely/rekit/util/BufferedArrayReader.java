package de.marcely.rekit.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferedArrayReader extends ByteArrayInputStream {
	
	public BufferedArrayReader(byte[] data){
		super(data);
	}
	
	@Override
	public int read(byte[] array){
		try{
			return super.read(array);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public byte[] read(int length){
		final byte[] a = new byte[length];
		
		this.read(a);
		
		return a;
	}
	
	public byte readByte(){
		return (byte) read();
	}
	
	public int readSignedInt(){
		return ByteBuffer.wrap(read(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
	
	public short readSignedShort(){
		return ByteBuffer.wrap(read(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	public float readSignedFloat(){
		return ByteBuffer.wrap(read(4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	public double readSignedDouble(){
		return ByteBuffer.wrap(read(8)).order(ByteOrder.LITTLE_ENDIAN).getDouble();
	}
	
	public long readSignedLong(){
		return ByteBuffer.wrap(read(8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}
	
	public int readUnsignedByte(){
		return Byte.toUnsignedInt(readByte());
	}
	
	public long readUnsignedInt(){
		return Integer.toUnsignedLong(readSignedInt());
	}
	
	public int readUnsignedShort(){
		return Short.toUnsignedInt(readSignedShort());
	}
	
	public int getOffset(){
		return this.pos;
	}
	
	public void setOffset(int off){
		this.pos = off;
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