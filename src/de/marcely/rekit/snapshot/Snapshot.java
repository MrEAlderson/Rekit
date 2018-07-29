package de.marcely.rekit.snapshot;

import com.sun.istack.internal.Nullable;

public class Snapshot {
	
	public static final int SNAPSHOT_MAX_PACK_SIZE = 900;
	
	private SnapshotItem[] items;
	
	public Snapshot(SnapshotItem[] items){
		this.items = items;
	}
	
	public void clear(){
		this.items = new SnapshotItem[0];
	}
	
	public @Nullable SnapshotItem findItem(int id, SnapshotObjectType type){
		return findItem(type.getID() << 16 | id);
	}
	
	public @Nullable SnapshotItem findItem(int key){
		for(SnapshotItem item:this.items){
			if(item.key == key)
				return item;
		}
		
		return null;
	}
	
	public int crc(){
		int crc = 0;
		
		for(SnapshotItem item:this.items){
			final int[] data = new int[item.obj.serializeLength()];
			
			item.obj.serialize(data, 0);
			
			for(int field:data)
				crc += field;
		}
		
		return crc;
	}
	
	public int getItemsAmount(){
		return this.items.length;
	}
	
	public SnapshotItem getItemOfIndex(int i){
		return this.items[i];
	}
}