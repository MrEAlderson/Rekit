package de.marcely.rekit.map.index;

import com.sun.istack.internal.Nullable;

import lombok.Getter;

public enum DefaultIndexType {
	
	AIR(0),
	WALL_HOOKABLE(1),
	WALL_UNHOOKABLE(3),
	
	DEATH(2),
	
	SPAWN_GENERIC(192),
	SPAWN_RED(193),
	SPAWN_BLUE(194),
	
	FLAG_RED(195),
	FLAG_BLUE(196),
	
	PROT_ARMOR(197),
	PROT_HEALTH(198),
	
	WEAPON_SHOTGUN(199),
	WEAPON_GRENADE(200),
	WEAPON_NINJA(201),
	WEAPON_RIFLE(202);
	
	@Getter private final int index;
	
	private DefaultIndexType(int index){
		this.index = index;
	}
	
	public static @Nullable DefaultIndexType byIndex(int i){
		for(DefaultIndexType type:values()){
			if(type.index == i)
				return type;
		}
		
		return null;
	}
}
