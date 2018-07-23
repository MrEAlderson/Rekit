package de.marcely.rekit.network.server;

import java.net.InetAddress;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.TWMap;
import de.marcely.rekit.network.master.MasterServerCommunication;
import de.marcely.rekit.network.packet.Packet;
import de.marcely.rekit.plugin.RekitServer;
import de.marcely.rekit.plugin.TuningParameter;
import de.marcely.rekit.plugin.World;
import de.marcely.rekit.plugin.map.Map;
import lombok.Getter;

public class Server implements RekitServer {
	
	public final Logger logger;
	public final ProtocolHandler protocol;
	public final MasterServerCommunication masterserver;
	public final ServerHandler handler;
	
	@Getter private boolean running = false;
	
	public Server(int port, TWMap map){
		this.logger = new Logger("Server");
		this.protocol = new ProtocolHandler(port, this);
		this.masterserver = new MasterServerCommunication(this);
		this.handler = new ServerHandler(this);
		this.map = map;
		
		// init tuning
		this.tuningParams = new float[TuningParameter.values().length];
		
		for(int i=0; i<this.tuningParams.length; i++)
			this.tuningParams[i] = TuningParameter.values()[i].getDefaultValue();
	}
	
	public int getPort(){ return this.protocol.getSocket().port; }
	
	public boolean run(){
		if(running) return false;
		running = true;
		
		this.logger.info("Starting server with the port " + getPort() + "...");
		
		if(protocol.run()){
			// this.masterserver.run();
			this.handler.run();
			
			return true;
		}else{
			this.logger.fatal("Failed to start the server. Maybe port is already in use?");
			
			return false;
		}
	}
	
	public boolean shutdown(){
		masterserver.shutdown();
		handler.shutdown();
		protocol.shutdown();
		
		return true;
	}
	
	public void sendPacket(InetAddress address, int port, Packet packet){
		// protocol.sendPacket(address, port, packet);
	}

	private int maxPlayers = 8;
	private int maxSameIPsAmount = 2;
	private String serverBrowseName = "A new Rekit Server";
	private String serverBrowseType = "Rekit";
	private TWMap map;
	private String password = null;
	private float[] tuningParams;
	private TWWorld world;
	
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
	public float getTuningParameterValue(TuningParameter param){
		return this.tuningParams[param.ordinal()];
	}

	@Override
	public void setTuningParameterValue(TuningParameter param, float value){
		this.tuningParams[param.ordinal()] = value;
	}

	@Override
	public float[] getTuningParameterValues(){
		return this.tuningParams;
	}

	@Override
	public World getWorld(){
		return (World) this.world;
	}
}
