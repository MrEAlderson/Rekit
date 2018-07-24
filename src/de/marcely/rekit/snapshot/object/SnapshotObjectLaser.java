package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectLaser extends SnapshotObject implements Cloneable  {

	public Vector2 pos;
	public Vector2 from;
	public int startTick;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_LASER;
	}

	@Override
	public int serializeLength(){
		return 5;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.from = new Vector2(data[offset++], data[offset++]);
		this.startTick = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = (int) this.from.getX();
		data[offset++] = (int) this.from.getY();
		data[offset++] = this.startTick;
	}
	
	@Override
	public SnapshotObjectLaser clone(){
		final SnapshotObjectLaser instance = (SnapshotObjectLaser) super.clone();
		
		instance.pos = this.pos.clone();
		instance.from = this.from.clone();
		
		return (SnapshotObjectLaser) super.clone();
	}
}
