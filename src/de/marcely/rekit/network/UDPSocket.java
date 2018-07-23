package de.marcely.rekit.network;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import de.marcely.rekit.util.Util;
import lombok.Getter;

public class UDPSocket {
	
	public final int port;
	private final int bufferSize;
	
	@Getter private boolean running = false;
	
	private SocketPump pump;
	private DatagramChannel channel;
	private ByteBuffer buffer;
	
	public UDPSocket(int port, int bufferSize){
		this.port = port;
		this.bufferSize = bufferSize;
		this.buffer = ByteBuffer.allocate(this.bufferSize);
	}
	
	public boolean update() throws Exception {
		if(!running) return false;
		
		while(true){
			final InetSocketAddress address = (InetSocketAddress) channel.receive(buffer);
			
			if(address == null) return true;
			
			pump.receive(address.getAddress(), address.getPort(), Util.arraycopy(buffer.array(), 0, buffer.position()));
			buffer.clear();
		}
	}
	
	public boolean run(SocketPump pump){
		if(running) return false;
		running = true;
		
		this.pump = pump;
		
		try{
			// open channel
			this.channel = DatagramChannel.open();
			
			this.channel.configureBlocking(false);
			this.channel.bind(new InetSocketAddress(this.port));
			
		}catch(BindException e){
			return false;
		}catch(IOException e){
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean shutdown(){
		if(!running) return false;
		running = false;
		
		try{
			this.channel.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	public boolean sendRawPacket(InetAddress address, int port, byte[] data){
		try{
			this.channel.send(ByteBuffer.wrap(data), new InetSocketAddress(address, port));
		}catch(IOException e){
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
}