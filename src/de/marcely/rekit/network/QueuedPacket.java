package de.marcely.rekit.network;

import java.net.InetAddress;

import lombok.Getter;

public class QueuedPacket {
	
	@Getter private final InetAddress address;
	@Getter private final int port;
	@Getter private final byte[] buffer;
	
	public QueuedPacket(InetAddress address, int port, byte[] buffer){
		this.address = address;
		this.port = port;
		this.buffer = buffer;
	}
}
