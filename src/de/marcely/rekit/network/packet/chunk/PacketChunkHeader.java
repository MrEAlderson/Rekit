package de.marcely.rekit.network.packet.chunk;

import java.io.IOException;
import java.io.OutputStream;

public class PacketChunkHeader {
	
	public final PacketChunkFlag[] flags;
	public int size;
	public final int sequence;
	
	public int newOffset;
	
	public PacketChunkHeader(PacketChunkFlag[] flags, int size, int sequence){
		this.flags = flags;
		this.size = size;
		this.sequence = sequence;
	}
	
	public void write(OutputStream stream) throws IOException {
		final byte[] buffer = new byte[PacketChunkFlag.has(flags, PacketChunkFlag.VITAL) ? 3 : 2];
		
        buffer[0] = (byte) ((((int) PacketChunkFlag.toBitMask(this.flags) & 3) << 6) |
        		((this.size >> 4) & 0x3F));
        buffer[1] = (byte) (this.size & 0xF);
        
        if(buffer.length == 3){
            buffer[1] |= (byte) ((this.sequence >> 2) & 0xF0);
            buffer[2] = (byte) (this.sequence & 0xFF);
        }
        
        stream.write(buffer);
	}
	
	public static PacketChunkHeader read(byte[] buffer, int offset){
		final byte header1 = buffer[offset++];
		final byte header2 = buffer[offset++];
		final PacketChunkFlag[] flags = PacketChunkFlag.ofBitMask((byte) ((header1 >> 6) & 3));
		final int size = ((header1 & 0b111111) << 4) | (header2 & 0b1111);
		int sequence = -1;
		
		if(PacketChunkFlag.has(flags, PacketChunkFlag.VITAL))
			sequence = ((header2 & 0b1111_0000) << 2) | (buffer[offset++]);
		
		final PacketChunkHeader header = new PacketChunkHeader(flags, size, sequence);
		
		header.newOffset = offset;
		
		return header;
	}
}
