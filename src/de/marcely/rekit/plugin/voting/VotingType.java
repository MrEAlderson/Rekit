package de.marcely.rekit.plugin.voting;

import com.sun.istack.internal.Nullable;

import lombok.Getter;

public enum VotingType {
	
	OPTION("option"),
	SPECTATE("spectate"),
	KICK("kick");
	
	@Getter private final String id;
	
	private VotingType(String id){
		this.id = id;
	}
	
	public static @Nullable VotingType ofID(String id){
		for(VotingType type:values()){
			if(type.id.equals(id))
				return type;
		}
		
		return null;
	}
}