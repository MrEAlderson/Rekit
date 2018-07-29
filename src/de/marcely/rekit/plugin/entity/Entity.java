package de.marcely.rekit.plugin.entity;

import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.util.Vector2;

public interface Entity {
	
	public EntityType getType();
	
	public Vector2 getPosition();
	
	public void teleport(Vector2 pos);
	
	public void teleport(Vector2 pos, TeleportCause cause);
	
	public void remove();
	
	public boolean exists();
	
	public int getID();
	
	public World getWorld();
	
	public Server getServer();
}
