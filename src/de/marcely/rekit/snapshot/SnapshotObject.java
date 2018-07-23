package de.marcely.rekit.snapshot;

public abstract class SnapshotObject {
	
	public abstract SnapshotObjectType getType();
	
	public abstract int serializeLength();
	
	public abstract void deserialize(int[] data, int offset);
	
	public abstract void serialize(int[] data, int offset);
}
