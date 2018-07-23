package de.marcely.rekit.plugin.entity;

import com.sun.istack.internal.Nullable;

public enum Weapon {
	
	GAME,
	SELF,
	WORLD,
	
	HAMMER,
	GUN,
	SHOTGUN,
	GRENADE,
	RIFLE,
	NINJA;
	
	public int getID(){
		return this.ordinal()-3;
	}
	
	public static @Nullable Weapon ofID(int id){
		for(Weapon wep:values()){
			if(wep.getID() == id)
				return wep;
		}
		
		return null;
	}
}
