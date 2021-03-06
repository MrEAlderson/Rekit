package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotEvent;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotEventHammerHit extends SnapshotEvent implements Cloneable  {

	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.EVENT_HAMMER_HIT;
	}

	@Override
	public int serializeLength(){
		return 2;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
	}
	
	@Override
	public SnapshotEventHammerHit clone(){
		return (SnapshotEventHammerHit) super.clone();
	}
}
