package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Player;
import de.marcely.rekit.plugin.entity.Weapon;
import de.marcely.rekit.plugin.player.Emote;

public class EntityPlayer extends TWEntity implements Player {

	public Emote emote;
	public Weapon weapon;
	public int health, armor, ammo;
	public boolean isAlive;
	
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
	public void doSnapshot(Client client){
		
	}

	@Override
	public Emote getEmote(){
		return this.emote;
	}

	@Override
	public Weapon getWeapon(){
		return this.weapon;
	}

	@Override
	public int getHealth(){
		return this.health;
	}

	@Override
	public int getArmor(){
		return this.armor;
	}

	@Override
	public int getAmmo(){
		return this.ammo;
	}

	@Override
	public boolean isAlive(){
		return this.isAlive;
	}
}
