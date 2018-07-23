package de.marcely.rekit;

import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.entity.*;
import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.plugin.entity.*;
import de.marcely.rekit.util.Vector2;

public class TWWorld implements World {

	private final Server server;
	
	private final List<Entity> entities = new ArrayList<>();
	public final List<Player> players = new ArrayList<>();
	
	public TWWorld(Server server){
		this.server = server;
	}
	
	@Override
	public List<Entity> getEntities(){
		return new ArrayList<>(this.entities);
	}

	@Override
	public List<Player> getPlayers(){
		return new ArrayList<>(this.players);
	}

	@Override
	public void removeEntity(Entity entity){
		this.entities.remove(entity);
		
		if(entity.getType() == EntityType.PLAYER)
			this.players.remove((Player) entity);
	}
	
	@Override
	public boolean exists(Entity entity){
		return this.entities.contains(entity);
	}
	
	@Override
	public Entity spawn(EntityType type, Vector2 pos){
		return spawn(type, pos, SpawnCause.PLUGIN);
	}
	
	@Override
	public Entity spawn(EntityType type, Vector2 pos, SpawnCause cause){
		Entity entity = null;
		
		switch(type){
		case PLAYER:
			entity = new EntityPlayer(this, getNextAvailableEntityId());
			break;
		
		case LASER:
			entity = new EntityLaser(this, getNextAvailableEntityId());
			break;
			
		case PICKUP:
			entity = new EntityPickup(this, getNextAvailableEntityId());
			break;
			
		case PROJECTILE:
			entity = new EntityProjectile(this, getNextAvailableEntityId());
			break;
		}
		
		this.entities.add(entity);
		
		return entity;
	}
	
	@Override
	public short getNextAvailableEntityId(){
		return 0;
	}

	@Override
	public Server getServer(){
		return this.server;
	}
}