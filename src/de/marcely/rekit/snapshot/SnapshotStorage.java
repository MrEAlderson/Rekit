package de.marcely.rekit.snapshot;

import java.util.Map.Entry;

import com.sun.istack.internal.Nullable;

import java.util.AbstractMap.SimpleEntry;

public class SnapshotStorage {
	
	public SnapshotInfo first, last;
	
	public SnapshotStorage(){ }
	
	public void purgeUntil(int tick){
		SnapshotInfo holder = this.first;
		
		while(holder != null){
			final SnapshotInfo next = holder.next;
			
			if(holder.tick >= tick)
				return;
			
			holder.next = null;
			holder.previus = null;
			
			if(next == null)
				break;
			
			first = next;
			next.previus = null;
			holder = next;
		}
		
		this.first = null;
		this.last = null;
	}
	
	public void add(int tick, long tagTime, Snapshot snapshot){
		final SnapshotInfo holder = new SnapshotInfo();
		
		holder.tick = tick;
		holder.tagTime = tagTime;
		holder.snapshot = snapshot;
		holder.previus = this.last;
		
		if(this.last != null)
			this.last.next = holder;
		else
			this.first = holder;
		
		this.last = holder;
	}
	
	public @Nullable Entry<Long, Snapshot> get(int tick){
		SnapshotInfo holder = this.first;
		
		while(holder != null){
			if(holder.tick == tick)
				return new SimpleEntry<Long, Snapshot>(holder.tagTime, holder.snapshot);
			
			holder = holder.next;
		}
		
		return null;
	}
	
	public void purgeAll(){
		SnapshotInfo holder = this.first;
		
		while(holder != null){
			final SnapshotInfo next = holder.next;
			
			holder.previus = null;
			holder.next = null;
			holder = next;
		}
		
		this.first = null;
		this.last = null;
	}
	
	
	
	public static class SnapshotInfo {
		public long tagTime;
		public int tick;
		public Snapshot snapshot;
		public SnapshotInfo previus, next;
	}
}
