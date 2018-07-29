package de.marcely.rekit;

import java.io.File;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.map.MapFile;
import de.marcely.rekit.network.server.Server;
import de.marcely.rekit.snapshot.SnapshotObjectType;

public class Main {
	
	private static final String VERSION = "1.0dev";
	private static final Logger LOGGER = new Logger("Rekit");
	
	public static Server SERVER;
	
	public static void main(String[] args){
		LOGGER.info(" REKIT v." + VERSION);
		LOGGER.info(" Created by MrEAlderson");
		LOGGER.info("============================");
		LOGGER.info("");
		
		SnapshotObjectType.init();
		
		final MapFile file = new MapFile(new File("F:/Program Files (x86)/Steam/steamapps/common/Teeworlds/tw/data/maps/ctf1.map"));
		
		SERVER = new Server(8303, file.load());
		SERVER.run();
	}
}
