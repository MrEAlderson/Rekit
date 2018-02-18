package de.marcely.rekit.map;

import java.io.File;

public class Map {
	
	public final File file;
	public final String name, checksum;
	public final Tile[][] tiles;
	public final int width, height;
	
	public Map(File file, String name, String checksum, int width, int height){
		this.file = file;
		this.name = name;
		this.checksum = checksum;
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
	}
}
