package de.marcely.rekit.network.packet.chunk;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.network.server.ProtocolHandler;
import de.marcely.rekit.util.Util;

public class PacketChunk {
	
	public final PacketSendFlag[] flags;
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
				if(header.sequence == (client.ack+1) % ProtocolHandler.PACKET_MAX_SEQUENCE)
					client.ack = (client.ack+1) % ProtocolHandler.PACKET_MAX_SEQUENCE;
				else{
					if(isSequenceInBackroom(header.sequence, client.ack))
						continue;
					
					client.signalResend();
				}
			}
			
			System.out.println(header.size + " " + offset);
			
			if(header.size+offset+1 >= buffer.length)
				break;
			
			list.add(new PacketChunk(new PacketSendFlag[0], Util.arraycopy(buffer, offset, header.size+offset)));
		}
		
		return list;
	}
	
    private static boolean isSequenceInBackroom(int seq, int ack){
        final int bottom = ack - ProtocolHandler.PACKET_MAX_SEQUENCE/2;
        
        if(bottom < 0){
            if(seq <= ack)
                return true;
            else if(seq >= (bottom + ProtocolHandler.PACKET_MAX_SEQUENCE))
                return true;
        }else if (seq <= ack && seq >= bottom)
            return true;

        return false;
    }
}
