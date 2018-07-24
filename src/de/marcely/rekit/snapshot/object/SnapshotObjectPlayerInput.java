package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Vector2;

public class SnapshotObjectPlayerInput extends SnapshotObject implements Cloneable  {
	
	public int direction;
	public Vector2 target;
	public boolean jump;
	public int fire;
	public boolean hook;
	public byte playerFlags = 0;
	public int wantedWeapon;
	public int nextWeapon;
	public int prevWeapon;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_PLAYER_INPUT;
	}

	@Override
	public int serializeLength(){
		return 10;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.direction = data[offset++];
		this.target = new Vector2(data[offset++], data[offset++]);
		this.jump = data[offset++] != 0;
		this.fire = data[offset++];
		this.hook = data[offset++] != 0;
		this.playerFlags = (byte) data[offset++];
		this.wantedWeapon = data[offset++];
		this.nextWeapon = data[offset++];
		this.prevWeapon = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = this.direction;
		data[offset++] = (int) this.target.getX();
		data[offset++] = (int) this.target.getY();
		data[offset++] = this.jump ? 1 : 0;
		data[offset++] = this.fire;
		data[offset++] = this.hook ? 1 : 0;
		data[offset++] = this.playerFlags;
		data[offset++] = this.wantedWeapon;
		data[offset++] = this.nextWeapon;
		data[offset++] = this.prevWeapon;
	}
	
	@Override
	public SnapshotObjectPlayerInput clone(){
		final SnapshotObjectPlayerInput instance = (SnapshotObjectPlayerInput) super.clone();
		
		instance.target = this.target.clone();
		
		return instance;
	}
}
