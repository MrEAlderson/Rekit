package de.marcely.rekit.snapshot;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.snapshot.object.*;

public enum SnapshotObjectType {
	
	OBJECT_PLAYER_INPUT,
	OBJECT_PROJECTILE,
	OBJECT_LASER,
	OBJECT_PICKUP,
	OBJECT_FLAG,
	OBJECT_GAME_INFO,
	OBJECT_GAME_DATA,
	OBJECT_CHARACTER_SCORE,
	OBJECT_CHARACTER,
	OBJECT_PLAYER_INFO,
	OBJECT_CLIENT_INFO,
	OBJECT_SPECTATOR_INFO,
	
	EVENT_COMMON,
	EVENT_EXPLOSION,
	EVENT_SPAWN,
	EVENT_HAMMER_HIT,
	EVENT_DEATH,
	EVENT_SOUND_GLOBAL,
	EVENT_SOUND_WORLD,
	EVENT_DAMAGE_IND;
	
	public int getID(){
		return this.ordinal()+1;
	}
	
	public @Nullable SnapshotObject newInstance(){
		switch(this){
		case OBJECT_PLAYER_INPUT:
			return new SnapshotObjectPlayerInput();
		case OBJECT_PROJECTILE:
			return new SnapshotObjectProjectile();
		case OBJECT_LASER:
			return new SnapshotObjectLaser();
		case OBJECT_PICKUP:
			return new SnapshotObjectPickup();
		case OBJECT_FLAG:
			return null;
		case OBJECT_GAME_INFO:
			return new SnapshotObjectGameInfo();
		case OBJECT_GAME_DATA:
			return null;
		case OBJECT_CHARACTER_SCORE:
			return null;
		case OBJECT_CHARACTER:
			return new SnapshotObjectCharacter();
		case OBJECT_PLAYER_INFO:
			return new SnapshotObjectPlayerInfo();
		case OBJECT_CLIENT_INFO:
			return new SnapshotObjectClientInfo();
		case OBJECT_SPECTATOR_INFO:
			return new SnapshotObjectSpectatorInfo();
		case EVENT_COMMON:
			return null;
		case EVENT_EXPLOSION:
			return new SnapshotEventExplosion();
		case EVENT_SPAWN:
			return new SnapshotEventSpawn();
		case EVENT_HAMMER_HIT:
			return new SnapshotEventHammerHit();
		case EVENT_DEATH:
			return new SnapshotEventDeath();
		case EVENT_SOUND_GLOBAL:
			return new SnapshotEventSoundGlobal();
		case EVENT_SOUND_WORLD:
			return new SnapshotEventSoundWorld();
		case EVENT_DAMAGE_IND:
			return new SnapshotEventDamageInd();
		default:
			return null;
		}
	}
}
