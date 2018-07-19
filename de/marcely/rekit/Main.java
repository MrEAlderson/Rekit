package de.marcely.rekit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.Map;
import de.marcely.rekit.map.MapFile;
import de.marcely.rekit.network.server.Server;

public class Main {
	
	private static final String VERSION = "1.0dev";
	private static final Logger LOGGER = new Logger("Rekit");
	
	public static Server SERVER;
	
	public static void main(String[] args){
		LOGGER.info(" REKIT v." + VERSION);
		LOGGER.info(" Created by MrEAlderson");
		LOGGER.info("============================");
		LOGGER.info("");
		
		final List<Map> loadedMaps = new ArrayList<Map>();
		
		final MapFile file = new MapFile(new File("F:/Program Files (x86)/Steam/steamapps/common/Teeworlds/tw/data/maps/ctf1.map"));
		loadedMaps.add(file.load());
		
		SERVER = new Server(8303, loadedMaps);
		SERVER.run();
	}
}
