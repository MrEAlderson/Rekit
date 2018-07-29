package de.marcely.rekit.network.server;

import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.plugin.GameStateFlag;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.snapshot.object.SnapshotObjectGameInfo;
import de.marcely.rekit.util.Vector2;

public class GameController {
	
	public static final int SPAWN_POS_DEFAULT = 0;
	public static final int SPAWN_POS_RED = 1;
	public static final int SPAWN_POS_BLUE = 2;
	
	private final Server server;
	
	private List<Vector2>[] spawnPos;
	private byte gameFlags;
	private int roundStartTick;
	private int gameOverTick;
	private int suddenDeath;
	private int warmup;
	private int roundNumber;
	private int unbalancedTick;
	private boolean forceBalanced;
	private int[] teamScores;
	private float[] scoresStartTick;
	
	@SuppressWarnings("unchecked")
	public GameController(Server server){
		this.server = server;
		
		this.spawnPos = new List[]{
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		};
		
		this.teamScores = new int[2];
		this.gameOverTick = -1;
		this.suddenDeath = 0;
		this.roundStartTick = server.getCurrentTick();
		this.roundNumber = 0;
		this.gameFlags = 0x00;
		this.unbalancedTick = -1;
		this.forceBalanced = false;
	}
	
	public void tick(){
		
	}
	
	public void doSnapshot(Client client){
		final SnapshotObjectGameInfo snap = this.server.snapBuilder.newObject(0, SnapshotObjectType.OBJECT_GAME_INFO);
		
		if(snap == null)
			return;
		
		snap.gameFlags = this.gameFlags;
		snap.gameStateFlags = 0;
		
		if(this.gameOverTick != -1)
			snap.gameStateFlags |= GameStateFlag.GAME_OVER;
		if(this.suddenDeath != 0)
			snap.gameStateFlags |= GameStateFlag.SUDDEN_DATH;
		if(this.server.getWorld().isPaused())
			snap.gameStateFlags |= GameStateFlag.PAUSED;
		
		snap.roundStartTick = this.roundStartTick;
		snap.warmupTimer = this.warmup;
		
		snap.scoreLimit = this.server.getScoreLimit();
		snap.timeLimit = this.server.getTimeLimit();
		
		snap.roundAmount = 0;
		snap.currentRound = this.roundNumber + 1;
	}
}