package de.marcely.rekit.map;

import java.io.File;

import de.marcely.rekit.plugin.map.Map;

public class TWMap implements Map {
	
	public final File file;
	public final String name;
	public final long checksum;
	public final Tile[][] tiles;
	public final int width, height;
	public final int size;
	
	public TWMap(File file, String name, long checksum, int width, int height, int size){
		this.file = file;
		this.name = name;
		this.checksum = checksum;
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		this.size = size;
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
	public long getChecksum(){
		return this.checksum;
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

	@Override
	public int getSize(){
		return this.size;
	}
}
