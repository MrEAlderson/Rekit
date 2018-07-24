package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;

public class SnapshotObjectGameInfo extends SnapshotObject implements Cloneable  {
	
	public byte gameFlags;
	public byte gameStateFlags;
	public int roundStartTick;
	public int warmupTimer;
	public int scoreLimit;
	public int timeLimit;
	public int roundAmount;
	public int currentRound;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_GAME_INFO;
	}

	@Override
	public int serializeLength(){
		return 8;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.gameFlags = (byte) data[offset++];
		this.gameStateFlags = (byte) data[offset++];
		this.roundStartTick = data[offset++];
		this.warmupTimer = data[offset++];
		this.scoreLimit = data[offset++];
		this.timeLimit = data[offset++];
		this.roundAmount = data[offset++];
		this.currentRound = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		data[offset++] = this.gameFlags;
		data[offset++] = this.gameStateFlags;
		data[offset++] = this.roundStartTick;
		data[offset++] = this.warmupTimer;
		data[offset++] = this.scoreLimit;
		data[offset++] = this.timeLimit;
		data[offset++] = this.roundAmount;
		data[offset++] = this.currentRound;
	}
	
	@Override
	public SnapshotObjectGameInfo clone(){
		return (SnapshotObjectGameInfo) super.clone();
	}
}
