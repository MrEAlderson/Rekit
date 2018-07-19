package de.marcely.rekit.network.packet;

public abstract class DataPacket {
	
	public abstract PacketType getType();
	
	public abstract void write(PacketOutputStream stream);
	
	public abstract void read(PacketInputStream stream);
}
