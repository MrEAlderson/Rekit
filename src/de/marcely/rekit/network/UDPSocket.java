package de.marcely.rekit.network;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import lombok.Getter;

public class UDPSocket {
	
	public final int port;
	
	@Getter private boolean running = false;
	
	private DatagramSocket socket;
	
	public UDPSocket(int port){
		this.port = port;
	}
	
	public boolean run(SocketPump pump){
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
							
							pump.receive(dp.getAddress(), dp.getPort(), Arrays.copyOf(buffer, dp.getLength()));
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