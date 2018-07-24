package de.marcely.rekit.snapshot;

public class SnapshotItem {
	
	public final int key;
	public final SnapshotObject obj;
	
	public SnapshotItem(int key, SnapshotObject obj){
		this.key = key;
		this.obj = obj;
	}
	
	public int getID(){
		return this.key & 0xFFFF;
	}
	
	public SnapshotObjectType getType(){
		return SnapshotObjectType.ofID(this.key >> 16);
	}
}
