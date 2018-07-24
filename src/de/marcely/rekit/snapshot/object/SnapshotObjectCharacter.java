package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.plugin.entity.Weapon;
import de.marcely.rekit.plugin.player.Emote;
import de.marcely.rekit.plugin.player.HookState;
import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectCharacter extends SnapshotObject implements Cloneable  {
	
	public int tick;
	public Vector2 pos;
	public Vector2 velo;
	public int angle;
	public int direction;
	public int jumped;
	public int hookedPlayer;
	public HookState hookState;
	public int hookTick;
	public Vector2 hookPos;
	public int hookDirX;
	public int hookDirY;
	public byte playerFlags;
	public int health;
	public int armor;
	public int ammoCount;
	public Weapon weapon = Weapon.HAMMER;
	public Emote emote = Emote.NORMAL;
	public int attackTick;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_CHARACTER;
	}

	@Override
	public int serializeLength(){
		return 22;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.tick = data[offset++];
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.velo = new Vector2(data[offset++], data[offset++]);
		this.angle = data[offset++];
		this.direction = data[offset++];
		this.jumped = data[offset++];
		this.hookedPlayer = data[offset++];
		this.hookState = HookState.ofID(data[offset++]);
		this.hookTick = data[offset++];
		this.hookPos = new Vector2(data[offset++], data[offset++]);
		this.hookDirX = data[offset++];
		this.hookDirY = data[offset++];
		this.playerFlags = (byte) data[offset++];
		this.health = data[offset++];
		this.armor = data[offset++];
		this.ammoCount = data[offset++];
		this.weapon = Weapon.ofID(data[offset++]);
		this.emote = Emote.ofID(data[offset++]);
		this.attackTick = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = this.tick;
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = (int) this.velo.getX();
		data[offset++] = (int) this.velo.getY();
		data[offset++] = this.angle;
		data[offset++] = this.direction;
		data[offset++] = this.jumped;
		data[offset++] = this.hookedPlayer;
		data[offset++] = this.hookState.getID();
		data[offset++] = this.hookTick;
		data[offset++] = (int) ((float) this.hookPos.getX()*100F);
		data[offset++] = (int) ((float) this.hookPos.getY()*100F);
		data[offset++] = this.hookDirX;
		data[offset++] = this.hookDirY;
		data[offset++] = this.playerFlags;
		data[offset++] = this.health;
		data[offset++] = this.armor;
		data[offset++] = this.ammoCount;
		data[offset++] = this.weapon.getID();
		data[offset++] = this.emote.getID();
		data[offset++] = this.attackTick;
	}
	
	@Override
	public SnapshotObjectCharacter clone(){
		final SnapshotObjectCharacter instance = (SnapshotObjectCharacter) super.clone();
		
		instance.pos = this.pos.clone();
		instance.velo = this.velo.clone();
		instance.hookPos = this.hookPos.clone();
		
		return instance;
	}
}
