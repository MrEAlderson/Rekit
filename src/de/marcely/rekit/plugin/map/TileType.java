package de.marcely.rekit.plugin.map;

import lombok.Getter;

public enum TileType {
	
	WALL_HOOKABLE((short) 1),
	WALL_NOT_HOOKABLE((short) 3),
	
	DEATH((short) 2),
	
	PLAYER_TEAMLESS((short) 192),
	PLAYER_RED((short) 193),
	PLAYER_BLUE((short) 194),
	
	FLAG_RED((short) 195),
	FLAG_BLUE((short) 196),
	
	PICKUP_AMMO((short) 197),
	PICKUP_HEALTH((short) 198),
	PICKUP_SHOTGUN((short) 199),
	PICKUP_GRENADE((short) 200),
	PICKUP_NINJA((short) 201),
	PICKUP_RIFLE((short) 202),
	
	NONE((short) -1);
	
	@Getter private final short index;
	
	private TileType(short index){
		this.index = index;
	}
	
	public static TileType ofIndex(short index){
		for(TileType type:values()){
			if(type.index == index)
				return type;
		}
		
		return TileType.NONE;
	}
}
