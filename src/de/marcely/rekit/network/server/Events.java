package de.marcely.rekit.network.server;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.snapshot.SnapshotEvent;
import de.marcely.rekit.snapshot.SnapshotObjectType;

public class Events {
	
	private final Server server;
	
	private final List<EventInfo> events;
	private final int maxEvents;
	
	public Events(Server server){
		this.server = server;
		this.maxEvents = 128;
		this.events = new ArrayList<>(this.maxEvents);
	}
	
	@SuppressWarnings("unchecked")
	public @Nullable <T extends SnapshotEvent> T create(int mask, SnapshotObjectType type){
		if(this.events.size() == this.maxEvents)
			return null;
		
		final EventInfo info = new EventInfo();
		
		info.snap = (SnapshotEvent) type.newInstance();
		info.mask = mask;
		
		this.events.add(info);
		
		return (T) info.snap;
	}
	
	public void clear(){
		this.events.clear();
	}
	
	public void doSnapshot(@Nullable Client client){
		for(EventInfo info:this.events){
			if(client == null || server.maskIsSet(info.mask, client.player.getID())){
				if(client == null ||
				   client.player.viewPos.distance(info.snap.pos) < 1500F){
					this.server.snapBuilder.addItem(info.snap, client.player.getID());
				}
			}
		}
	}
	
	
	
	public static class EventInfo {
		
		public SnapshotEvent snap;
		public int mask;
	}
}