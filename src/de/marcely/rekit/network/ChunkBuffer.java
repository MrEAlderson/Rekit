package de.marcely.rekit.network;

import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.network.packet.PacketChunk;
import lombok.Getter;
import lombok.Setter;

public class ChunkBuffer {
	
	@Getter private final String identifier;
	
	@Getter @Setter private long lastUpdate = System.currentTimeMillis();
	@Getter private final List<PacketChunk> chunks = new ArrayList<PacketChunk>();
	
	public ChunkBuffer(String identifier){
		this.identifier = identifier;
	}
}
