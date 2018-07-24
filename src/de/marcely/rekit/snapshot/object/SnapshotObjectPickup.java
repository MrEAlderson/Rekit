package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.plugin.entity.Powerup;
import de.marcely.rekit.plugin.entity.Weapon;
import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectPickup extends SnapshotObject implements Cloneable  {
	
	public Vector2 pos;
	public Powerup powerup = Powerup.WEAPON;
	public Weapon weapon = Weapon.HAMMER;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_PICKUP;
	}

	@Override
	public int serializeLength(){
		return 4;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.powerup = Powerup.ofID(data[offset++]);
		this.weapon = Weapon.ofID(data[offset++]);
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = this.powerup.getID();
		data[offset++] = this.weapon.getID();
	}
	
	@Override
	public SnapshotObjectPickup clone(){
		final SnapshotObjectPickup instance = (SnapshotObjectPickup) super.clone();
		
		instance.pos = this.pos.clone();
		
		return instance;
	}
}
