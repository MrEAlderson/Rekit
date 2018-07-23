package de.marcely.rekit.plugin;

import com.sun.istack.internal.Nullable;

public enum Sound {
	
	GUN_FIRE,
	SHOTGUN_FIRE,
	GRENADE_FIRE,
	HAMMER_FIRE,
	HAMMER_HIT,
	NINJA_FIRE,
	GRENADE_EXPLODE,
	NINJA_HIT,
	RIFLE_FIRE,
	RIFLE_BOUNCE,
	WEAPON_SWITCH,
	PLAYER_PAIN_SHORT,
	PLAYER_PAIN_LONG,
	BODY_LAND,
	PLAYER_AIR_JUMP,
	PLAYER_JUMP,
	PLAYER_DIE,
	PLAYER_SPAWN,
	PLAYER_SKID,
	TEE_CRY,
	HOOK_LOOP,
	HOOK_ATTACH_GROUND,
	HOOK_ATTACH_PLAYER,
	HOOK_NO_ATTACH,
	PICKUP_HEALTH,
	PICKUP_ARMOR,
	PICKUP_GRENADE,
	PICKUP_SHOTGUN,
	PICKUP_NINJA,
	WEAPON_SPAWN,
	WEAPON_NO_AMMO,
	HIT,
	CHAT_SERVER,
	CHAT_CLIENT,
	CHAT_HIGHLIGHT,
	CTF_DROP,
	CTF_RETURN,
	CTF_GRAB_PL,
	CTF_GRAB_EN,
	CTF_CAPTURE,
	MENU;
	
	public int getID(){
		return this.ordinal();
	}
	
	public static @Nullable Sound ofID(int id){
		for(Sound sound:values()){
			if(sound.getID() == id)
				return sound;
		}
		
		return null;
	}
}
