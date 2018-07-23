package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.plugin.player.Team;
import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;

public class SnapshotObjectPlayerInfo extends SnapshotObject {
	
	public boolean local;
	public int clientID;
	public Team team;
	public int score;
	public int latency;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_PLAYER_INFO;
	}

	@Override
	public int serializeLength(){
		return 5;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.local = data[offset++] != 0;
		this.clientID = data[offset++];
		this.team = Team.ofID(data[offset++]);
		this.score = data[offset++];
		this.latency = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = this.local ? 1 : 0;
		data[offset++] = this.clientID;
		data[offset++] = this.team.getID();
		data[offset++] = this.score;
		data[offset++] = this.latency;
	}
}
