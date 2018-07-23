package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Pickup;

public class EntityPickup extends TWEntity implements Pickup {

	public EntityPickup(TWWorld world, int id){
		super(world, id);
	}

	@Override
	public EntityType getType(){
		return null;
	}

	@Override
	public float getProximityRadius(){
		return 0;
	}

	@Override
	public void tick(){ }

	@Override
	public void doSnapshot(Client client){
		
	}
}
