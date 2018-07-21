package de.marcely.rekit.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BufferedWriteStream extends OutputStream {
	
	private OutputStream stream;
	private byte[] buffer;
	private int count = 0;
	
	public BufferedWriteStream(OutputStream stream){
		this.stream = stream;
	}
	
	public BufferedWriteStream(int size){
		if(size < 0)
			throw new IllegalArgumentException("Negative initial size: " + size);
		
		this.buffer = new byte[size];
	}
	
	public BufferedWriteStream(){
		this(32);
	}
	
	@Override
	public void write(int b){
		if(stream != null){
			try{
				stream.write(b);
			}catch(IOException e){
				e.printStackTrace();
			}
		
		}else{
			final int newCount = count+1;
			
			if(newCount > buffer.length)
				buffer = Arrays.copyOf(buffer, Math.max(buffer.length << 1, newCount));
			
			buffer[count] = (byte) b;
			count = newCount;
		}
	}
	
	@Override
	public void write(byte[] array){
		try{
			super.write(array);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeByte(byte b){
		final byte[] array = new byte[1];
		array[0] = b;
		
		write(array);
	}
	
	public void writeUnsignedByte(int b){
		writeByte((byte) (b & 0xFF));
	}
	
	public void writeByteArray(byte[] b){
		writeSignedInt(b.length);
		write(b);
	}
	
	public void writeSignedInt(int i){
		write(ByteBuffer.allocate(4).putInt(i).array());
	}
	
	public void writeUnsignedInt(long i){
		writeSignedInt((int) ((long) i & 0x7FFFFFFF));
	}
	
	public void writeSignedShort(short s){
		write(ByteBuffer.allocate(2).putShort(s).array());
	}
	
	public void writeUnsignedShort(int s){
		writeSignedShort((short) (s & 0x00FF));
	}
	
	public void writeFloat(float f){
		write(ByteBuffer.allocate(4).putFloat(f).array());
	}
	
	public void writeDouble(double d){
		write(ByteBuffer.allocate(8).putDouble(d).array());
	}
	
	public void writeSignedLong(long l){
		write(ByteBuffer.allocate(8).putLong(l).array());
	}
	
	public void writeString(String s){
		writeByteArray(s.getBytes());
	}
	
	public void writeBoolean(boolean b){
		writeByte(b == true ? (byte) 1 : (byte) 0);
	}
	
	public void writeTWInt(int val){
		write(IntCompressor.pack(val));
	}
	
	public void writeTWString(String val){
		write(val.getBytes(StandardCharsets.UTF_8));
		write(0x00);
	}
	
	public void writeTWBoolean(boolean val){
		writeTWInt(val ? 1 : 0);
	}
	
	public byte[] toByteArray(){
		if(stream != null){
			new NullPointerException("Streaming is enabled").printStackTrace();
			return null;
		}
		
		return Arrays.copyOf(buffer, count);
	}
	
	@Override
	public void close(){
		try{
			if(stream != null)
				stream.close();
			
			super.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
