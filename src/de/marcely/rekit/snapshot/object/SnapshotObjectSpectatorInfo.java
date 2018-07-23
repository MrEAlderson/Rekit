package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectSpectatorInfo extends SnapshotObject {
	
	public int spectatorID;
	public Vector2 viewPos;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_SPECTATOR_INFO;
	}

	@Override
	public int serializeLength(){
		return 3;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.spectatorID = data[offset++];
		this.viewPos = new Vector2(data[offset++], data[offset++]);
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = this.spectatorID;
		data[offset++] = (int) this.viewPos.getX();
		data[offset++] = (int) this.viewPos.getY();
	}
}
