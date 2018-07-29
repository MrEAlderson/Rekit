package de.marcely.rekit.network.server;

import java.util.Map.Entry;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.entity.EntityPlayer;
import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.TWMap;
import de.marcely.rekit.network.master.MasterServerCommunication;
import de.marcely.rekit.network.packet.PacketSendFlag;
import de.marcely.rekit.plugin.RekitServer;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.plugin.entity.Player;
import de.marcely.rekit.plugin.map.Map;
import de.marcely.rekit.snapshot.Snapshot;
import de.marcely.rekit.snapshot.SnapshotBuilder;
import de.marcely.rekit.snapshot.SnapshotDelta;
import de.marcely.rekit.snapshot.SnapshotItem;
import de.marcely.rekit.snapshot.SnapshotRate;
import de.marcely.rekit.util.BufferedWriteStream;
import de.marcely.rekit.util.IntCompressor;
import lombok.Getter;

public class Server implements RekitServer {
	
	private static final int TICK_SPEED = 50;
	
	public final Logger logger;
	public final ProtocolHandler protocol;
	public final MasterServerCommunication masterserver;
	private final GameLoop loop;
	private final GameController controller;
	private final Events events;
	
	@Getter private boolean running = false;
	private long startTime;
	private int tick, tickSpeed = TICK_SPEED;
	
	private int maxPlayers = 8;
	private int maxSameIPsAmount = 2;
	private String serverBrowseName = "A new Rekit Server";
	private String serverBrowseType = "Rekit";
	private TWMap map;
	private String password = null;
	private TWWorld world;
	private int timeLimit = 10, scoreLimit = 30;
	
	public SnapshotBuilder snapBuilder = new SnapshotBuilder();
	
	public Server(int port, TWMap map){
		this.logger = new Logger("Server");
		this.protocol = new ProtocolHandler(port, this);
		this.masterserver = new MasterServerCommunication(this);
		this.loop = new GameLoop(this);
		this.map = map;
		this.world = new TWWorld(this);
		this.controller = new GameController(this);
		this.events = new Events(this);
	}
	
	public int getPort(){ return this.protocol.getSocket().port; }
	
	public boolean run(){
		if(running) return false;
		running = true;
		
		this.logger.info("Starting server with the port " + getPort() + "...");
		
		if(protocol.run()){
			this.startTime = System.currentTimeMillis();
			
			this.logger.info("Started the server.");
			
			// this.masterserver.run();
			this.loop.run();
			
			return true;
		}else{
			this.logger.fatal("Failed to start the server. Maybe port is already in use?");
			
			return false;
		}
	}
	
	public boolean shutdown(){
		masterserver.shutdown();
		protocol.shutdown();
		
		return true;
	}
	
	public long tickStartTime(int tick){
		return this.startTime + (System.currentTimeMillis() * tick) / this.tickSpeed;
	}
	
	@Override
	public void setMaxPlayers(int amount){
		if(amount > ProtocolHandler.VANILLA_MAX_CLIENTS){
			new UnsupportedOperationException("Setting the max players amount to higher than 16 is not allowed by virtue of the rules by Teeworlds").printStackTrace();
			return;
		}else if(amount < 0){
			new UnsupportedOperationException("Setting max players amount to less than 0").printStackTrace();
			return;
		}
		
		this.maxPlayers = amount;
	}
	
	public void doSnapshot(){
		for(Client client:this.protocol.clients.values()){
			if(client.serverState != ServerClientState.IN_GAME)
				continue;
			
			if(client.snapRate == SnapshotRate.RECOVER && this.tick % this.tickSpeed != 0)
				continue;
			
			if(client.snapRate == SnapshotRate.INIT && tick % 10 != 0)
				continue;
			
			this.snapBuilder.startBuild();
			
			this.world.doSnapshot(client);
			this.controller.doSnapshot(client);
			this.events.doSnapshot(client);
			
			for(Player player:this.world.getPlayers())
				((EntityPlayer) player).doSnapshotPlayer(client);
			
			final long now = System.currentTimeMillis();
			final Snapshot snap = this.snapBuilder.endBuild();
			final int crc = snap.crc();
			
			client.snapStorage.purgeUntil(tick-tickSpeed*3);
			client.snapStorage.add(tick, now, snap);
			
			int deltaTick = -1;
			final Entry<Long, Snapshot> cSnap = client.snapStorage.get(client.lastAckedSnapshot);
			Snapshot deltaSnap = null;
			
			if(cSnap != null){
				deltaTick = client.lastAckedSnapshot;
				deltaSnap = cSnap.getValue();
			
			}else{
				deltaSnap = new Snapshot(new SnapshotItem[0]);
				
				if(client.snapRate == SnapshotRate.FULL)
					 client.snapRate = SnapshotRate.RECOVER;
			}
			
			final int[] deltaData = new int[SnapshotBuilder.SNAPSHOT_MAX_SIZE / 4];
			final int deltaSize = SnapshotDelta.createDelta(deltaSnap, snap, deltaData);
			
			if(deltaSize == 0){
				final BufferedWriteStream stream = new BufferedWriteStream();
				
				stream.writeTWInt(PacketHandler.MSG_SV_SNAP_EMPTY);
				stream.writeTWInt(this.tick);
				stream.writeTWInt(this.tick - deltaTick);
				
				client.sendMsgEx(stream, true, PacketSendFlag.FLUSH);
				continue;
			}
			
			final byte[] snapData = new byte[SnapshotBuilder.SNAPSHOT_MAX_SIZE];
			final int snapSize = IntCompressor.compress(deltaData, 0, deltaSize, snapData, 0);
			final int packetsAmount = (snapSize + Snapshot.SNAPSHOT_MAX_PACK_SIZE - 1) / Snapshot.SNAPSHOT_MAX_PACK_SIZE;
			
			for(int n=0, left = snapSize; left != 0; n++){
				final int chunk = left < Snapshot.SNAPSHOT_MAX_PACK_SIZE ? left : Snapshot.SNAPSHOT_MAX_PACK_SIZE;
				left -= chunk;
				
				if(packetsAmount == 1){
					final BufferedWriteStream stream = new BufferedWriteStream();
					
					stream.writeTWInt(PacketHandler.MSG_SV_SNAP_SINGLE);
					stream.writeTWInt(this.tick);
					stream.writeTWInt(this.tick - deltaTick);
					stream.writeTWInt(crc);
					stream.writeTWInt(chunk);
					stream.write(snapData, n * Snapshot.SNAPSHOT_MAX_PACK_SIZE, chunk);
					
					client.sendMsgEx(stream, true, PacketSendFlag.FLUSH);
					
				}else{
					final BufferedWriteStream stream = new BufferedWriteStream();
					
					stream.writeTWInt(PacketHandler.MSG_SV_SNAP);
					stream.writeTWInt(this.tick);
					stream.writeTWInt(this.tick - deltaTick);
					stream.writeTWInt(packetsAmount);
					stream.writeTWInt(n);
					stream.writeTWInt(crc);
					stream.writeTWInt(chunk);
					stream.write(snapData, n * Snapshot.SNAPSHOT_MAX_PACK_SIZE, chunk);
					
					client.sendMsgEx(stream, true, PacketSendFlag.FLUSH);
				}
			}
		}
	}
	
	public void tick() throws Exception {
		this.tick++;
		
		if(this.tick % 2 == 0)
			doSnapshot();
		
		this.controller.tick();
		this.protocol.tick();
	}
	
	public int maskOne(int clientID){
		return 1 << clientID;
	}
	
	public boolean maskIsSet(int mask, int clientID){
		return (mask & maskOne(clientID)) != 0;
	}

	@Override
	public int getMaxPlayers(){
		return this.maxPlayers;
	}

	@Override
	public void setMaxSameIPsAmount(int amount){
		if(amount < 0) amount = 0;
		
		this.maxSameIPsAmount = amount;
	}

	@Override
	public int getMaxSameIPsAmount(){
		return this.maxSameIPsAmount;
	}

	@Override
	public String getGameVersion(){
		return "0.6";
	}

	@Override
	public String getServerBrowseName(){
		return this.serverBrowseName;
	}

	@Override
	public void setServerBrowseName(String val){
		if(val == null){
			new NullPointerException().printStackTrace();
			return;
		}
		if(val.length() > 256)
			val = val.substring(0, 256);
		
		this.serverBrowseName = val;
	}

	@Override
	public String getServerBrowseType(){
		return this.serverBrowseType;
	}
	
	public void setBrowserType(String val){
		if(val == null){
			new NullPointerException().printStackTrace();
			return;
		}
		if(val.length() > 16)
			val = val.substring(0, 16);
		
		this.serverBrowseType = val;
	}

	@Override
	public Map getMap(){
		return (Map) this.map;
	}

	@Override
	public boolean isPasswordEnabled(){
		return this.password != null;
	}

	@Override
	public String getPassword(){
		return this.password;
	}

	@Override
	public void setPassword(String password){
		this.password = password;
	}

	@Override
	public void disablePassword(){
		this.password = null;
	}

	@Override
	public String getNetworkVersion(){
		return "0.6";
	}

	@Override
	public String getSoftwareVersion(){
		return "0.6.0";
	}
	
	@Override
	public int getTicksPerSecond(){
		return this.loop.tps;
	}
	
	@Override
	public int getMaxTicksPerSecond(){
		return this.tickSpeed;
	}

	@Override
	public long getGameLoopExecutionTime(){
		return this.loop.execTime;
	}
	
	@Override
	public long getStartTime(){
		return this.startTime;
	}

	@Override
	public World getWorld(){
		return (World) this.world;
	}

	@Override
	public int getCurrentTick(){
		return this.tick;
	}

	@Override
	public int getTimeLimit(){
		return this.timeLimit;
	}

	@Override
	public int getScoreLimit(){
		return this.scoreLimit;
	}
}
