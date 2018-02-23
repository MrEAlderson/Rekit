package de.marcely.rekit.network.server;

import java.net.InetAddress;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.network.packet.Packet;

public interface PacketReceiver {
	
	public void onReceive(InetAddress address, int port, Packet packet);
	
	public void onConnect(Client client);
	
	public void onDisconnect(Client client);
	
	public @Nullable KickReason canJoin(InetAddress address, int port);
	
	
	public static class AbstractPacketReceiver implements PacketReceiver {

		@Override
		public void onReceive(InetAddress address, int port, Packet packet){ }

		@Override
		public void onConnect(Client client){ }

		@Override
		public void onDisconnect(Client client){ }
		
		@Override
		public @Nullable KickReason canJoin(InetAddress address, int port){ return null; }
	}
}