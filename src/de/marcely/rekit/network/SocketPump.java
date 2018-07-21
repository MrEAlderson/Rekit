package de.marcely.rekit.network;

import java.net.InetAddress;

public interface SocketPump {
	
	public void receive(InetAddress address, int port, byte[] buffer) throws Exception;
}
