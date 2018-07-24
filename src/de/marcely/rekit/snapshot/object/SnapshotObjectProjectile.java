package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.plugin.entity.Weapon;
import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectProjectile extends SnapshotObject implements Cloneable  {
	
	public Vector2 pos;
	public Vector2 velo;
	public Weapon weapon;
	public int startTick;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_PROJECTILE;
	}

	@Override
	public int serializeLength(){
		return 6;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.velo = new Vector2(data[offset++], data[offset++]);
		this.weapon = Weapon.ofID(data[offset++]);
		this.startTick = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = (int) this.velo.getX();
		data[offset++] = (int) this.velo.getY();
		data[offset++] = this.weapon.getID();
		data[offset++] = this.startTick;
	}
	
	@Override
	public SnapshotObjectProjectile clone(){
		final SnapshotObjectProjectile instance = (SnapshotObjectProjectile) super.clone();
		
		instance.pos = this.pos.clone();
		instance.velo = this.velo.clone();
		
		return instance;
	}
}
