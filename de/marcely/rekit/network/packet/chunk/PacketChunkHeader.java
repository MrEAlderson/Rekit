package de.marcely.rekit.network.packet.chunk;

import de.marcely.rekit.util.BufferedReadStream;
import de.marcely.rekit.util.BufferedWriteStream;

public class PacketChunkHeader {
	
	public final PacketChunkFlag[] flags;
	public final int size;
	public final int sequence;
	
	public PacketChunkHeader(PacketChunkFlag[] flags, int size, int sequence){
		this.flags = flags;
		this.size = size;
		this.sequence = sequence;
	}
	
	public void write(BufferedWriteStream stream){
		final byte[] buffer = new byte[PacketChunkFlag.has(flags, PacketChunkFlag.VITAL) ? 3 : 2];
		
        buffer[0] = (byte) ((((int) PacketChunkFlag.toBitMask(this.flags) & 0b11) << 6) |
        		((this.size >> 4) & 0b111111));
        buffer[1] = (byte) (this.size & 0xF);

        if(buffer.length == 3){
            buffer[1] |= (byte) ((this.sequence >> 2) & 0b1111_0000);
            buffer[2] = (byte) (this.sequence & 0b1111_1111);
        }
        
        stream.write(buffer);
	}
	
	public static PacketChunkHeader read(BufferedReadStream stream){
		final byte header1 = stream.readByte();
		final byte header2 = stream.readByte();
		final PacketChunkFlag[] flags = PacketChunkFlag.ofBitMask((byte) ((header1 >> 6) & 0b11));
		final int size = ((header1 & 0b111111) << 4) | (header2 & 0b1111);
		int sequence = -1;
		
		if(PacketChunkFlag.has(flags, PacketChunkFlag.VITAL))
			sequence = ((header2 & 0b1111_0000) << 2) | (stream.readByte());
		
		return new PacketChunkHeader(flags, size, sequence);
	}
}
