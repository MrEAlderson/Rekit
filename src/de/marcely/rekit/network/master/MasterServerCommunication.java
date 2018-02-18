package de.marcely.rekit.network.master;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.network.PacketReceiver;
import de.marcely.rekit.network.PacketType;
import de.marcely.rekit.network.Server;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketMasterOutHandshake;
import de.marcely.rekit.network.packet.PacketMasterOutHeartbeat;

public class MasterServerCommunication {
	
	public static final int PORT = 8300;
	public MasterServer selectedServer;
	
	public final Server server;
	private final Logger logger;
	
	private PacketReceiver receiver = null;
	private Timer heartbeatTimer = null;
	
	public MasterServerCommunication(Server server){
		this.server = server;
		this.logger = new Logger("MasterServer");
	}
	
	public boolean isRunning(){
		return receiver != null;
	}
	
	public boolean run(){
		if(isRunning()) return false;
		
		selectedServer = null;
		
		receiver = new PacketReceiver(){
			public void onReceive(InetAddress address, int port, Packet packet){
				if(packet.type == PacketType.MASTER_IN_HANDSHAKE){
					if(selectedServer == null){
						selectedServer = MasterServer.byAddress(address);
						
						logger.info("Chose '" + selectedServer.address.getHostName() + "' as master, sending heartbeats");
						
						heartbeatTimer = new Timer();
						heartbeatTimer.schedule(new TimerTask(){
							public void run(){
								for(int i=0; i<2; i++)
									sendPacket(selectedServer, new PacketMasterOutHeartbeat());
							}
						}, 1000, 1000*10);
					
					}
				
				}
			}
		};
		server.protocol.receivers.add(receiver);
		
		logger.info("Fetching server counts");
		
		for(MasterServer ms:MasterServer.values())
			sendPacket(ms, new PacketMasterOutHandshake());
		
		return true;
	}
	
	public boolean shutdown(){
		return true;
	}
	
	public void sendPacket(MasterServer server, Packet packet){
		this.server.sendPacket(server.address, PORT, packet);
	}
}