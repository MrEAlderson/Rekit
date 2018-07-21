package de.marcely.rekit.plugin;

import de.marcely.rekit.Main;

public interface RekitServer {
	
	public void setMaxPlayers(int amount);
	
	public int getMaxPlayers();
	
	public void setMaxSameIPsAmount(int amount);
	
	public int getMaxSameIPsAmount();
	
	
	
	public static RekitServer getServer(){
		return Main.SERVER;
	}
}
