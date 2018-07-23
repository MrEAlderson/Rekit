package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotEvent;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotEventDamageInd extends SnapshotEvent {

	public int angle;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.EVENT_DAMAGE_IND;
	}

	@Override
	public int serializeLength(){
		return 3;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.pos = new Vector2(data[offset++], data[offset++]);
		this.angle = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = (int) this.pos.getX();
		data[offset++] = (int) this.pos.getY();
		data[offset++] = this.angle;
	}
}
