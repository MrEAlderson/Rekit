package de.marcely.rekit.snapshot;

public abstract class SnapshotObject implements Cloneable {
	
	public abstract SnapshotObjectType getType();
	
	public abstract int serializeLength();
	
	public abstract void deserialize(int[] data, int offset);
	
	public abstract void serialize(int[] data, int offset);
	
	@Override
	public SnapshotObject clone(){
		try{
			return (SnapshotObject) super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return null;
	}
}
