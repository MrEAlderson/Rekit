package de.marcely.rekit.network.server;

import java.net.InetAddress;
import java.util.List;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.Map;
import de.marcely.rekit.network.master.MasterServerCommunication;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.plugin.RekitServer;
import lombok.Getter;

public class Server implements RekitServer {
	
	public final Logger logger;
	public final ProtocolHandler protocol;
	public final MasterServerCommunication masterserver;
	public final ServerHandler handler;
	public final ServerInfo info;
	
	@Getter private boolean running = false;
	
	public Server(int port, List<Map> maps){
		this.logger = new Logger("Server");
		this.protocol = new ProtocolHandler(port, this);
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
			// this.masterserver.run();
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
	
	public void sendPacket(InetAddress address, int port, Packet packet){
		// protocol.sendPacket(address, port, packet);
	}

	private int maxPlayers = 8;
	private int maxSameIPsAmount = 2;
	
	@Override
	public void setMaxPlayers(int amount){
		if(amount > 16){
			new UnsupportedOperationException("Setting the max players amount to higher than 16 is not allowed by virtue of the rules by Teeworlds").printStackTrace();
			return;
		}else if(amount < 0){
			new UnsupportedOperationException("Setting max players amount to less than 0").printStackTrace();
			return;
		}
		
		this.maxPlayers = amount;
	}

	@Override
	public int getMaxPlayers(){
		return this.maxPlayers;
	}

	@Override
	public void setMaxSameIPsAmount(int amount){
		if(amount < 0) amount = 0;
		
		this.maxSameIPsAmount = amount;
	}

	@Override
	public int getMaxSameIPsAmount(){
		return this.maxSameIPsAmount;
	}
}
