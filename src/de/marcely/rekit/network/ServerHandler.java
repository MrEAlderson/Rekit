package de.marcely.rekit.network;

import java.net.InetAddress;

import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.network.packet.PacketServerbrowseInGetInfo;
import de.marcely.rekit.network.packet.PacketServerbrowseOutInfo;
import de.marcely.rekit.network.packet.PacketServerbrowseOutResponse;
import de.marcely.rekit.util.Util;

public class ServerHandler {
	
	public final Server server;
	
	private PacketReceiver receiver = null;
	
	public ServerHandler(Server server){
		this.server = server;
	}
	
	public boolean isRunning(){
		return receiver != null;
	}
	
	public boolean run(){
		if(isRunning()) return false;
		
		receiver = new PacketReceiver(){
			public void onReceive(InetAddress address, int port, Packet rawPacket){
				if(rawPacket.type == PacketType.SERVERBROWSE_IN_CHECK)
					server.sendPacket(address, port, new PacketServerbrowseOutResponse());
				
				else if(rawPacket.type == PacketType.SERVERBROWSE_IN_GETINFO){
					final PacketServerbrowseInGetInfo packet = (PacketServerbrowseInGetInfo) rawPacket;
					if(packet.token == null) return;
					
					final PacketServerbrowseOutInfo np = new PacketServerbrowseOutInfo();
					np.token = packet.token;
					np.serverInfo = server.info;
					
					System.out.println(Util.bytesToHex(np.getRawData()));
					
					server.sendPacket(address, port, np);
				
				}else if(rawPacket.type == PacketType.SERVERBROWSE_IN_ERROR){
					server.logger.warn("The master server reports that clients can not connect to this server");
					server.logger.warn("Configure your Firewall/NAT to let through UDP on port " + server.getPort());
				}
			}
		};
		server.protocol.receivers.add(receiver);
		
		return true;
	}
	
	public boolean shutdown(){
		if(!isRunning()) return false;
		
		server.protocol.receivers.remove(receiver);
		
		return true;
	}
}
