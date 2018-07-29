package de.marcely.rekit.plugin.event.player;

import java.net.InetAddress;

import de.marcely.rekit.plugin.event.Event;
import de.marcely.rekit.plugin.event.HandlerContainer;
import lombok.Getter;

public class PlayerPreLoginEvent extends Event {
	
	private static final HandlerContainer CONTAINER = new HandlerContainer();
	
	@Getter private final InetAddress address;
	@Getter private final int port;
	
	public PlayerPreLoginEvent(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	@Override
	public HandlerContainer getHandlerContainer(){
		return CONTAINER;
	}
	
	public static HandlerContainer getHandlerContainerStatic(){
		return CONTAINER;
	}
}