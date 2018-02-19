package de.marcely.rekit.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.Map;
import de.marcely.rekit.network.master.MasterServerCommunication;
import de.marcely.rekit.network.packet.Packet;
import lombok.Getter;

public class Server {
	
	public final Logger logger;
	public final ProtocolHandler protocol;
	public final MasterServerCommunication masterserver;
	public final ServerHandler handler;
	public final ServerInfo info;
	public final java.util.Map<Integer, Client> clients = new HashMap<>();
	public final java.util.Map<String, Client> clients2 = new HashMap<>();
	
	@Getter private boolean running = false;
	
	public Server(int port, List<Map> maps){
		this.logger = new Logger("Server");
		this.protocol = new ProtocolHandler(port);
		this.masterserver = new MasterServerCommunication(this);
		this.handler = new ServerHandler(this);
		this.info = new ServerInfo(this, maps);
	}
	
	public int getPort(){ return this.protocol.getSocket().port; }
	
	public boolean run(){
		if(running) return false;
		running = true;
		
		this.logger.info("Starting server with the port " + getPort() + "...");
		
		if(protocol.run()){
			this.masterserver.run();
			this.handler.run();
			
			return true;
		}else{
			this.logger.fatal("Failed to start the server. Maybe port is already in use?");
			
			return false;
		}
	}
	
	public boolean shutdown(){
		masterserver.shutdown();
		handler.shutdown();
		protocol.shutdown();
		
		return true;
	}
	
	public void sendPacket(InetAddress address, int port, Packet packet, TransferType type){
		protocol.sendPacket(address, port, packet, type);
	}
}
