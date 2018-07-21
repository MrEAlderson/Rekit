package de.marcely.rekit.network.server;

import java.net.InetSocketAddress;

import de.marcely.rekit.KickReason;
import de.marcely.rekit.KickReason.KickReasonType;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.util.Util;
import lombok.Getter;
import lombok.Setter;

public class Client {
	
	private static final long TIMEOUT = 8000;
	private static final long KEEP_ALIVE_TIME = 1000;
	
	@Getter private final Server server;
	@Getter private final InetSocketAddress address;
	@Getter private final short id;
	
	@Setter private ClientState state = ClientState.DISCONNECTED;
	@Getter @Setter private long lastReceivedPacket = System.currentTimeMillis();
	@Getter private final long loginDate = System.currentTimeMillis();
	private long lastKeepAlive = System.currentTimeMillis();
	
	public int ack = 0;
	
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
		
		server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CLOSE, reason.getMessage());
		
		return true;
	}
	
	public boolean close(){
		if(!isConnected())
			return false;
		
		setState(ClientState.DISCONNECTED);
		
		this.server.protocol.clients.remove(this.id);
		this.server.protocol.clients2.remove(getIdentifier());
		this.server.protocol.bufferedChunks.remove(getIdentifier());
		
		for(PacketReceiver receiver:this.server.protocol.receivers)
			receiver.onDisconnect(this);
		
		System.out.println("Disconnected");
		
		return true;
	}
	
	public void tick(){
		if(System.currentTimeMillis() - lastReceivedPacket >= TIMEOUT){
			kick(new KickReason(KickReasonType.TIMEOUT));
			return;
		}
		
		if(System.currentTimeMillis() - lastKeepAlive >= KEEP_ALIVE_TIME){
			switch(state){
			case ONLINE:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_KEEP_ALIVE, "");
				break;
				
			case CONNECT:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CONNECT, "");
				break;
				
			case PENDING:
				server.protocol.sendControlPacket(this.address.getAddress(), this.address.getPort(), 0, ProtocolHandler.PACKET_TYPE_CONNECT_ACCEPT, "");
				break;
				
			default:
				break;
			}
			
			lastKeepAlive = System.currentTimeMillis();
		}
	}
	
	public void ping(){
		// if(state == ClientState.CONNECTED)
		//	this.server.protocol.sendPacketControl(address.getAddress(), address.getPort(), PacketType.CONTROL_KEEPALIVE, null);
		//else if(state == ClientState.PENDING)
		//	this.server.protocol.sendPacketControl(address.getAddress(), address.getPort(), PacketType.CONTROL_CONNECT_ACCEPT, null);
	}
	
	public void signalResend(){
		
	}
}