package de.marcely.rekit.plugin;

import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.plugin.entity.Entity;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Player;
import de.marcely.rekit.plugin.entity.SpawnCause;
import de.marcely.rekit.plugin.player.Team;
import de.marcely.rekit.util.Vector2;

public interface World {
	
	public List<Entity> getEntities();
	
	public List<Player> getPlayers();
	
	public void removeEntity(Entity entity);
	
	public boolean exists(Entity entity);
	
	public <T extends Entity> T spawn(EntityType type, Vector2 pos);
	
	public <T extends Entity> T spawn(EntityType type, Vector2 pos, SpawnCause cause);
	
	public int getNextAvailableEntityId();
	
	public float getTuningParameterValue(TuningParameter param);
	
	public void setTuningParameterValue(TuningParameter param, float value);
	
	public float[] getTuningParameterValues();
	
	public boolean isPaused();
	
	public List<Vector2> getSpawnPositions(Team team);
	
	public void addSpawnPosition(Team team, Vector2 pos);
	
	public void removeSpawnPosition(Team team, Vector2 pos);
	
	public @Nullable Vector2 getRandomSpawnPosition(Team team);
	
	public Server getServer();
}
