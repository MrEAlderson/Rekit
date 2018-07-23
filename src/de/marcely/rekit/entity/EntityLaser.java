package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Laser;

public class EntityLaser extends TWEntity implements Laser {

	public EntityLaser(TWWorld world, int id){
		super(world, id);
	}

	@Override
	public EntityType getType(){
		return EntityType.LASER;
	}

	@Override
	public float getProximityRadius(){
		return 0;
	}

	@Override
	public void tick(){
		
	}

	@Override
	public void doSnapshot(Client client){
		
	}
}
