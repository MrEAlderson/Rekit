package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Player;

public class EntityPlayer extends TWEntity implements Player {

	public EntityPlayer(TWWorld world, int id){
		super(world, id);
	}

	@Override
	public EntityType getType(){
		return EntityType.PLAYER;
	}

	@Override
	public float getProximityRadius(){
		return 0;
	}

	@Override
	public void tick(){
		
	}

	@Override
	public void doSnapshot(){
		
	}
}
