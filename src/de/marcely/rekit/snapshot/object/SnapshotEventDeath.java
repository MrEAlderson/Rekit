package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotEvent;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotEventDeath extends SnapshotEvent implements Cloneable  {
	
	public int clientID;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.EVENT_DEATH;
	}

	@Override
	public int serializeLength(){
		return 3;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.clientID = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = this.clientID;
	}
	
	@Override
	public SnapshotEventDeath clone(){
		return (SnapshotEventDeath) super.clone();
	}
}
