package de.marcely.rekit.network.packet;

import java.net.InetSocketAddress;
import java.util.Arrays;

import de.marcely.rekit.network.TransferType;
import de.marcely.rekit.util.BufferedWriteStream;
import lombok.Getter;

public class PacketChunk {
	
	@Getter private final InetSocketAddress address;
	@Getter private final PacketFlag flag;
	@Getter private final byte ackID, chunksAmount;
	@Getter private final byte[] buffer;
	
	public byte[] _preBuffer = null;
	
	public PacketChunk(InetSocketAddress address, PacketFlag flag, byte ack, byte chunksAmount, byte[] buffer){
		this.address = address;
		this.flag = flag;
		this.ackID = ack;
		this.chunksAmount = chunksAmount;
		this.buffer = buffer;
	}
	
	public boolean hasAck(){
		return ackID != -1;
	}
	
	public byte[] toData(TransferType type){
		final BufferedWriteStream stream = new BufferedWriteStream();
		
		stream.writeByte(type == TransferType.SIMPLE ? (byte) 0xFF : (byte) (((flag.getId() << 4) & 0xF0) | ((ackID >> 8) & 0xF)));
		stream.writeByte((byte) (ackID & 0xFF));
		stream.writeByte(type == TransferType.SIMPLE ? (byte) 0xFF : chunksAmount);
		stream.write(new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
		if(_preBuffer != null) stream.write(_preBuffer);
		stream.write(buffer);
		
		final byte[] data = stream.toByteArray();
		
		stream.close();
		
		return data;
	}
	
	public static PacketChunk ofData(InetSocketAddress address, byte[] data){
		final PacketFlag flag = PacketFlag.ofID((byte) (data[0] >> 4));
		final byte ack = (byte) (((data[0] & 0xf) << 8) | data[1]);
		final byte numChunks = data[2] == -1 ? 1 : data[2];
		
		return new PacketChunk(address, flag, ack, numChunks, Arrays.copyOfRange(data, 3, data.length));
	}
}
