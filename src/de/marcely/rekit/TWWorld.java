package de.marcely.rekit;

import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.entity.*;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.plugin.TuningParameter;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.plugin.entity.*;
import de.marcely.rekit.plugin.player.Team;
import de.marcely.rekit.util.Util;
import de.marcely.rekit.util.Vector2;

public class TWWorld implements World {

	private final Server server;
	
	private final List<Entity> entities = new ArrayList<>();
	public final List<Player> players = new ArrayList<>();
	private final float[] tuningParams;
	private boolean isPaused;
	private List<Vector2>[] spawnPositions;
	
	private int nextID;
	
	@SuppressWarnings("unchecked")
	public TWWorld(Server server){
		this.server = server;
		this.spawnPositions = new List[2];
		
		// init tuning
		this.tuningParams = new float[TuningParameter.values().length];
		
		for(int i=0; i<this.tuningParams.length; i++)
			this.tuningParams[i] = TuningParameter.values()[i].getDefaultValue();
		
		// find spawn points
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
	public <T extends Entity> T spawn(EntityType type, Vector2 pos){
		return spawn(type, pos, SpawnCause.PLUGIN);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entity> T spawn(EntityType type, Vector2 pos, SpawnCause cause){
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
		this.nextID++;
		
		return (T) entity;
	}
	
	@Override
	public int getNextAvailableEntityId(){
		return this.nextID;
	}
	
	@Override
	public float getTuningParameterValue(TuningParameter param){
		return this.tuningParams[param.ordinal()];
	}

	@Override
	public void setTuningParameterValue(TuningParameter param, float value){
		this.tuningParams[param.ordinal()] = value;
	}

	@Override
	public float[] getTuningParameterValues(){
		return this.tuningParams;
	}

	@Override
	public Server getServer(){
		return this.server;
	}
	
	public void doSnapshot(Client client){
		for(Entity entity:this.entities)
			((TWEntity) entity).doSnapshot(client);
	}
	
	@Override
	public List<Vector2> getSpawnPositions(Team team){
		if(team.getID() < 0) return new ArrayList<>();
		
		return this.spawnPositions[team.getID()];
	}

	@Override
	public Vector2 getRandomSpawnPosition(Team team){
		final List<Vector2> list = getSpawnPositions(team);
		
		if(list.size() == 0)
			return null;
		
		return list.get(Util.RAND.nextInt(list.size()));
	}

	@Override
	public boolean isPaused(){
		return this.isPaused;
	}

	@Override
	public void addSpawnPosition(Team team, Vector2 pos){
		getSpawnPositions(team).add(pos);
	}

	@Override
	public void removeSpawnPosition(Team team, Vector2 pos){
		getSpawnPositions(team).remove(pos);
	}
}