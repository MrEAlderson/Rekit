package de.marcely.rekit.network.server;

import java.net.InetSocketAddress;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.util.Util;
import lombok.Getter;
import lombok.Setter;

public class Client {
	
	public static final long TIMEOUT = 8000;
	
	@Getter private final Server server;
	@Getter private final InetSocketAddress address;
	@Getter private final short id;
	
	@Setter private ClientState state = ClientState.DISCONNECTED;
	@Getter @Setter private long lastReceivedPacket = System.currentTimeMillis();
	
	public Client(Server server, InetSocketAddress address, short id){
		this.server = server;
		this.address = address;
		this.id = id;
	}
	
	public String getIdentifier(){
		return Util.getIdentifier(address.getAddress(), address.getPort());
	}
	
	public boolean isConnected(){
		return server.protocol.isConnected(address.getAddress(), address.getPort());
	}
	
	public ClientState getState(){
		if(!isConnected())
			this.state = ClientState.DISCONNECTED;
		
		return this.state;
	}
	
	public void sendPacket(Packet packet){
		this.server.sendPacket(address.getAddress(), address.getPort(), packet);
	}
	
	public boolean kick(KickReason reason){
		if(!close())
			return false;
		
		this.server.protocol.sendPacketControl(address.getAddress(), address.getPort(), PacketType.CONTROL_CLOSE, reason.getMessage());
		
		return true;
	}
	
	public boolean close(){
		if(!isConnected())
			return false;
		
		setState(ClientState.DISCONNECTED);
		
		this.server.protocol.clients.remove(this.id);
		this.server.protocol.clients2.remove(getIdentifier());
		
		for(PacketReceiver receiver:this.server.protocol.receivers)
			receiver.onDisconnect(this);
		
		System.out.println("Disconnected");
		
		return true;
	}
	
	public void ping(){
		if(state == ClientState.CONNECTED)
			this.server.protocol.sendPacketControl(address.getAddress(), address.getPort(), PacketType.CONTROL_KEEPALIVE, null);
		else if(state == ClientState.PENDING)
			this.server.protocol.sendPacketControl(address.getAddress(), address.getPort(), PacketType.CONTROL_CONNECT_ACCEPT, null);
	}
}