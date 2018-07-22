package de.marcely.rekit.network.packet.chunk;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.network.server.ProtocolHandler;
import de.marcely.rekit.util.Util;

public class PacketChunk {
	
	public PacketSendFlag[] flags;
	public byte[] buffer;
	
	public PacketChunk(PacketSendFlag[] flags, byte[] buffer){
		this.flags = flags;
		this.buffer = buffer;
	}
	
	public static List<PacketChunk> read(byte[] buffer, int chunksAmount, @Nullable Client client){
		final List<PacketChunk> list = new ArrayList<>();
		
		int offset = 0;
		
		while(true){
			if(offset+3 >= buffer.length)
				break;
			
			final PacketChunkHeader header = PacketChunkHeader.read(buffer, offset);
			
			offset = header.newOffset;
			
			if(client != null && PacketChunkFlag.has(header.flags, PacketChunkFlag.VITAL)){
				System.out.println("asd" + (client.ack+1) % ProtocolHandler.PACKET_MAX_SEQUENCE);
				
				if(header.sequence == (client.ack+1) % ProtocolHandler.PACKET_MAX_SEQUENCE)
					client.ack = (client.ack+1) % ProtocolHandler.PACKET_MAX_SEQUENCE;
				else{
					if(ProtocolHandler.isSequenceInBackroom(header.sequence, client.ack)){
						offset += header.size;
						continue;
					}
					
					client.signalResend();
				}
			}
			
			System.out.println(offset + "-" + (header.size+offset) + "/" + buffer.length);
			
			if(header.size+offset > buffer.length)
				break;
			
			list.add(new PacketChunk(new PacketSendFlag[0], Util.arraycopy(buffer, offset, header.size+offset)));
			
			offset += header.size;
		}
		
		return list;
	}
}
