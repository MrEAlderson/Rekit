package de.marcely.rekit.snapshot;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

public class SnapshotBuilder {
	
	public static final int SNAPSHOT_MAX_SIZE = 65536;
	public static final int SNAPSHOT_MAX_ITEMS = 1024;
	
	private List<SnapshotItem> items = new ArrayList<>(SNAPSHOT_MAX_ITEMS);
	private int totalSize;
	
	public SnapshotBuilder(){ }
	
	public void startBuild(){
		this.totalSize = 0;
		this.items.clear();
	}
	
	public Snapshot endBuild(){
		return new Snapshot(this.items.stream().toArray(SnapshotItem[]::new));
	}
	
	public SnapshotItem findItem(int key){
		for(SnapshotItem item:this.items){
			if(item.key == key)
				return item;
		}
		
		return null;
	}
	
	public <T extends SnapshotObject> boolean addItem(T obj, int id){
		if(obj == null){
			new NullPointerException("Object is null").printStackTrace();
			return false;
		}
		
		if(this.items.size() + 1 >= SNAPSHOT_MAX_ITEMS){
			new UnsupportedOperationException("Container is full").printStackTrace();
			return false;
		}
		
		if(obj.getType() == null){
			new NullPointerException("Type of object is null").printStackTrace();
			return false;
		}
		
		final int itemSize = obj.serializeLength()*4;
		
		if(this.totalSize + itemSize >= SNAPSHOT_MAX_SIZE){
			new UnsupportedOperationException("Buffer is full").printStackTrace();
			return false;
		}
		
		this.totalSize += itemSize;
		this.items.add(new SnapshotItem(obj.getType().getID() << 16 | id, obj));
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public @Nullable <T extends SnapshotObject> T newObject(int id, SnapshotObjectType type){
		final T obj = (T) type.newInstance();
		
		if(obj == null){
			new NullPointerException("Failed to create an instance of the object").printStackTrace();
			return null;
		}
		
		return addItem(obj, id) ? obj : null;
	}
}