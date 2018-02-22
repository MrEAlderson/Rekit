package de.marcely.rekit.network.packet;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.util.BufferedWriteStream;
import lombok.Getter;

public class PacketChunk {
	
	@Getter private final InetAddress address;
	@Getter private final int port;
	@Getter private final PacketFlag[] flags;
	@Getter private final byte ackID, chunksAmount;
	@Getter private final byte[] buffer;
	
	public byte[] _preBuffer = null;
	
	public PacketChunk(InetAddress address, int port, PacketFlag[] flags, byte ack, byte chunksAmount, byte[] buffer){
		this.address = address;
		this.port = port;
		this.flags = flags;
		this.ackID = ack;
		this.chunksAmount = chunksAmount;
		this.buffer = buffer;
	}
	
	public boolean hasAck(){
		return ackID != -1;
	}
	
	public byte[] toData(){
		final BufferedWriteStream stream = new BufferedWriteStream();
		
		if(hasFlag(PacketFlag.CONNLESS)){
			for(int i=0; i<10; i++)
				stream.write((byte) 0xFF);
		
		}else{
			byte flagID = (byte) 0x00;
			
			for(PacketFlag flag:flags)
				flagID |= flag.getId();
			
			stream.writeByte((byte) (((flagID << 4) & 0xF0) | ((ackID >> 8) & 0xF)));
			stream.writeByte((byte) (ackID & 0xFF));
			stream.writeByte((byte) (chunksAmount-1));
		}
		
		if(_preBuffer != null) stream.write(_preBuffer);
		stream.write(buffer);
		
		final byte[] data = stream.toByteArray();
		
		stream.close();
		
		return data;
	}
	
	public boolean hasFlag(PacketFlag flag){
		for(PacketFlag f:flags){
			if(f == flag)
				return true;
		}
		
		return false;
	}
	
	public static @Nullable PacketChunk ofData(InetAddress address, int port, byte[] data){
		final byte flagID = (byte) (data[0] >> 4);
		final byte ack = (byte) (((data[0] & 0xf) << 8) | data[1]);
		final byte numChunks = data[2] == -1 ? 1 : (byte) (data[2]+1);
		final List<PacketFlag> flags = new ArrayList<>(PacketFlag.values().length);
		
		for(PacketFlag flag:PacketFlag.values()){
			if((byte) (flagID & flag.getId()) == flag.getId())
				flags.add(flag);
		}
		
		if(flags.contains(PacketFlag.CONNLESS)){
			if(data.length >= 10)
				return new PacketChunk(address, port, flags.toArray(new PacketFlag[flags.size()]), ack, numChunks, Arrays.copyOfRange(data, 10, data.length));
			else
				return null;
		}else
			return new PacketChunk(address, port, flags.toArray(new PacketFlag[flags.size()]), ack, numChunks, Arrays.copyOfRange(data, 3, data.length));
	}
}
