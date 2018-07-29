package de.marcely.rekit.plugin.event.player;

import de.marcely.rekit.plugin.entity.Player;
import de.marcely.rekit.plugin.event.Event;
import de.marcely.rekit.plugin.event.HandlerContainer;
import lombok.Getter;

public class PlayerLoginEvent extends Event {
	
	private static final HandlerContainer CONTAINER = new HandlerContainer();
	
	@Getter private final Player player;
	
	@Getter private String message;
	
	public PlayerLoginEvent(Player player){
		this(player, player.getName() + " entered and joined the game");
	}
	
	public PlayerLoginEvent(Player player, String message){
		this.player = player;
		this.message = message;
	}
	
	public void setMessage(String message){
		if(message == null)
			message = "";
		
		this.message = message;
	}
	
	@Override
	public HandlerContainer getHandlerContainer(){
		return CONTAINER;
	}
	
	public static HandlerContainer getHandlerContainerStatic(){
		return CONTAINER;
	}
}