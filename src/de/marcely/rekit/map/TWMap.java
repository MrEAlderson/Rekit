package de.marcely.rekit.map;

import java.io.File;

import de.marcely.rekit.plugin.map.Map;

public class TWMap implements Map {
	
	public final File file;
	public final String name, checksum;
	public final Tile[][] tiles;
	public final int width, height;
	
	public TWMap(File file, String name, String checksum, int width, int height){
		this.file = file;
		this.name = name;
		this.checksum = checksum;
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
	}

	@Override
	public File getFile(){
		return this.file;
	}

	@Override
	public String getName(){
		return this.name;
	}

	@Override
	public byte[] getChecksum(){
		return this.checksum.getBytes();
	}

	@Override
	public int getWidth(){
		return this.width;
	}

	@Override
	public int getHeight(){
		return this.height;
	}

	@Override
	public byte getGameIndexAt(int x, int y){
		return (byte) this.tiles[x][y].index;
	}
}
