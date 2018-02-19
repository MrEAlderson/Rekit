package de.marcely.rekit.network;

import java.net.InetSocketAddress;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.network.packet.Packet;
import lombok.Getter;
import lombok.Setter;

public class Client {
	
	public static final long TIMEOUT = 8000;
	
	@Getter private final Server server;
	@Getter private final InetSocketAddress address;
	@Getter private final int id;
	
	@Setter private ClientState state = ClientState.DISCONNECTED;
	@Getter @Setter private long lastReceivedPacket = System.currentTimeMillis();
	
	public Client(Server server, InetSocketAddress address, int id){
		this.server = server;
		this.address = address;
		this.id = id;
	}
	
	public String getIdentifier(){
		return this.address.getHostString() + ":" + this.address.getPort();
	}
	
	public boolean isConnected(){
		return server.clients.containsValue(this);
	}
	
	public ClientState getState(){
		if(!isConnected())
			this.state = ClientState.DISCONNECTED;
		
		return this.state;
	}
	
	public void sendPacket(Packet packet, TransferType type){
		this.server.sendPacket(address.getAddress(), address.getPort(), packet, type);
	}
	
	public boolean kick(KickReason reason){
		if(!isConnected())
			return false;
		
		this.server.clients.remove(this.id);
		this.server.clients2.remove(getIdentifier());
		
		// TODO Send kick packet
		
		return true;
	}
}