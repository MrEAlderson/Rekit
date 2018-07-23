package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Projectile;

public class EntityProjectile extends TWEntity implements Projectile {

	public EntityProjectile(TWWorld world, int id){
		super(world, id);
	}

	@Override
	public EntityType getType(){
		return EntityType.PROJECTILE;
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
