package de.marcely.rekit.network;

import java.net.InetAddress;
import java.util.List;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.Map;
import de.marcely.rekit.network.master.MasterServerCommunication;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class Server {
	
	public final Logger logger;
	public final UDPSocket protocol;
	public final MasterServerCommunication masterserver;
	public final ServerHandler handler;
	public final ServerInfo info;
	
	@Getter private boolean running = false;
	
	public Server(int port, List<Map> maps){
		this.logger = new Logger("Server");
		this.protocol = new UDPSocket(port);
		this.masterserver = new MasterServerCommunication(this);
		this.handler = new ServerHandler(this);
		this.info = new ServerInfo(this, maps);
	}
	
	public int getPort(){ return this.protocol.port; }
	
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
	
	public boolean sendPacket(InetAddress address, int port, Packet packet){
		return protocol.sendRawPacket(address, port,
				Util.concat(Packet.MAGIC, Util.concat(packet.type.id, packet.getRawData())));
	}
}
