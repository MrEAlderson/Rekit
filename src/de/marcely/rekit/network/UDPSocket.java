package de.marcely.rekit.network;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.util.Util;
import lombok.Getter;

public class UDPSocket {
	
	private final Logger logger;
	public final int port;
	
	public final List<PacketReceiver> receivers = new ArrayList<PacketReceiver>();
	@Getter private boolean running = false;
	
	private DatagramSocket socket;
	
	public UDPSocket(int port){
		this.logger = new Logger("Protocol");
		this.port = port;
	}
	
	public boolean run(){
		if(running) return false;
		running = true;
		
		try{
			socket = new DatagramSocket(port);
			
			new Thread(){
				public void run(){
					while(running){
						final byte[] buffer = new byte[1024];
						final DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
						
						try{
							socket.receive(dp);
							
							final byte[] data = Arrays.copyOfRange(buffer, Packet.MAGIC.length, dp.getLength());
							final PacketType type = PacketType.byData(data);
							
							if(type != null){
								final Packet packet = type.clazz.newInstance();
								packet.readRawData(Arrays.copyOfRange(data, 8, data.length));
								
								for(PacketReceiver receiver:receivers)
									receiver.onReceive(dp.getAddress(), dp.getPort(), packet);
							}else
								logger.debug("Received unkown packet (" + Util.bytesToHex(Arrays.copyOfRange(data, 0, 4)) + ", " + dp.getAddress().getHostAddress() + ") [" + new String(data) + "]");
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}.start();
			
		}catch(BindException e){
			return false;
		}catch(SocketException e){
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean shutdown(){
		if(!running) return false;
		running = false;
		
		socket.close();
		
		return true;
	}
	
	public boolean sendRawPacket(InetAddress address, int port, byte[] data){
		final DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
		
		try{
			socket.send(sendPacket);
		}catch(IOException e){
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
}