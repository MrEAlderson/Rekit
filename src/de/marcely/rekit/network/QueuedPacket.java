package de.marcely.rekit.network;

import java.net.InetAddress;

import de.marcely.rekit.network.packet.PacketFlag;
import lombok.Getter;

public class QueuedPacket {
	
	@Getter private final InetAddress address;
	@Getter private final int port;
	@Getter private final byte ackID;
	@Getter private final PacketFlag[] flags;
	@Getter private final byte[] buffer;
	
	public QueuedPacket(InetAddress address, int port, byte ackID, PacketFlag[] flags, byte[] buffer){
		this.address = address;
		this.port = port;
		this.ackID = ackID;
		this.flags = flags;
		this.buffer = buffer;
	}
}
