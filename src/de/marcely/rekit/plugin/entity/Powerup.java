package de.marcely.rekit.plugin.entity;

import com.sun.istack.internal.Nullable;

public enum Powerup {
	
	HEALTH,
	ARMOR,
	WEAPON,
	NINJA;
	
	public int getID(){
		return this.ordinal();
	}
	
	public static @Nullable Powerup ofID(int id){
		for(Powerup pow:values()){
			if(pow.getID() == id)
				return pow;
		}
		
		return null;
	}
}
