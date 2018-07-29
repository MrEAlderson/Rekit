package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.plugin.entity.Entity;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.TeleportCause;
import de.marcely.rekit.util.Vector2;

public abstract class TWEntity implements Entity {
	
	public final TWWorld world;
	public final int id;
	public Vector2 pos = new Vector2(0, 0);
	
	public TWEntity(TWWorld world, int id){
		this.world = world;
		this.id = id;
	}
	
	public abstract EntityType getType();
	
	public abstract float getProximityRadius();
	
	public abstract void tick();
	
	public abstract void doSnapshot(Client client);
	
	@Override
	public Vector2 getPosition(){
		return this.pos;
	}
	
	@Override
	public void teleport(Vector2 pos){
		teleport(pos, TeleportCause.PLUGIN);
	}
	
	@Override
	public void teleport(Vector2 pos, TeleportCause cause){
		
	}
	
	@Override
	public void remove(){
		this.world.removeEntity(this);
	}
	
	@Override
	public boolean exists(){
		return this.world.exists(this);
	}
	
	@Override
	public int getID(){
		return this.id;
	}
	
	@Override
	public World getWorld(){
		return this.world;
	}
	
	@Override
	public Server getServer(){
		return this.world.getServer();
	}
	
	public void destroySnapshot(){
		
	}
}
