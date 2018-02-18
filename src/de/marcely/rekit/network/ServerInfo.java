package de.marcely.rekit.network;

import java.util.List;

import de.marcely.rekit.map.Map;

public class ServerInfo {
	
	public final Server server;
	
	public String name = "A new rekit server";
	public Map map;
	public String mod = "Rekit";
	public String version = "0.6.4";
	public int maxClients = 1;
	public List<Map> maps;
	
	public ServerInfo(Server server, List<Map> maps){
		this.server = server;
		this.maps = maps;
		this.map = maps.get(0);
	}
}
