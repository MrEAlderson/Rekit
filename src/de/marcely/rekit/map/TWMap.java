package de.marcely.rekit.map;

import java.io.File;

import de.marcely.rekit.plugin.map.Map;
import de.marcely.rekit.plugin.map.Tile;

public class TWMap implements Map {
	
	private final File file;
	private final String name;
	private final long checksum;
	public final TWTile[][] tiles;
	private final int width, height;
	private final int size;
	
	private final TWTile[] tileTypes;
	
	public TWMap(File file, String name, long checksum, int width, int height, int size){
		this.file = file;
		this.name = name;
		this.checksum = checksum;
		this.width = width;
		this.height = height;
		this.tiles = new TWTile[width][height];
		this.size = size;
		
		// set tile types & fill tiles
		this.tileTypes = new TWTile[256];
		
		for(short i=0; i<256; i++)
			this.tileTypes[i] = new TWTile(i);
		
		for(int ix=0; ix<width; ix++){
			for(int iy=0; iy<height; iy++){
				this.tiles[ix][iy] = tileTypes[0];
			}
		}
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
	public Tile getTileAt(int x, int y){
		return (Tile) this.tiles[x][y];
	}

	@Override
	public int getSize(){
		return this.size;
	}

	@Override
	public Tile getTileByIndex(short index){
		return this.tileTypes[index];
	}

	@Override
	public short getMaxTileIndex(){
		return 255;
	}
}
