package de.marcely.rekit.plugin.player;

import com.sun.istack.internal.Nullable;

public enum Team {
	
	SPECTATOR(-1),
	RED(0),
	BLUE(1);
	
	private final int id;
	
	private Team(int id){
		this.id = id;
	}
	
	public int getID(){
		return this.id;
	}
	
	public static @Nullable Team ofID(int id){
		for(Team team:values()){
			if(team.id == id)
				return team;
		}
		
		return null;
	}
}
