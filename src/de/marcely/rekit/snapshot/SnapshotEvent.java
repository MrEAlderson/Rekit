package de.marcely.rekit.snapshot;

import de.marcely.rekit.util.Vector2;

public abstract class SnapshotEvent extends SnapshotObject implements Cloneable {
	
	public Vector2 pos;
	
	@Override
	public SnapshotEvent clone(){
		final SnapshotEvent instance = (SnapshotEvent) super.clone();
		
		instance.pos = this.pos.clone();
		
		return instance;
	}
}
