package de.marcely.rekit.network;

import java.net.InetAddress;

import de.marcely.rekit.network.packet.Packet;

public interface PacketReceiver {
	
	public void onReceive(InetAddress address, int port, Packet packet);
}