package de.marcely.rekit.plugin;

import de.marcely.rekit.Main;
import de.marcely.rekit.plugin.map.Map;

public interface RekitServer {
	
	public void setMaxPlayers(int amount);
	
	public int getMaxPlayers();
	
	public void setMaxSameIPsAmount(int amount);
	
	public int getMaxSameIPsAmount();
	
	public String getGameVersion();
	
	public String getNetworkVersion();
	
	public String getSoftwareVersion();
	
	public String getServerBrowseName();
	
	public void setServerBrowseName(String browseName);
	
	public String getServerBrowseType();
	
	public Map getMap();
	
	public boolean isPasswordEnabled();
	
	public String getPassword();
	
	public void setPassword(String password);
	
	public void disablePassword();
	
	public float getTuningParameterValue(TuningParameter param);
	
	public void setTuningParameterValue(TuningParameter param, float value);
	
	public float[] getTuningParameterValues();
	
	public World getWorld();
	
	public static RekitServer getServer(){
		return Main.SERVER;
	}
}
