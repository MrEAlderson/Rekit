package de.marcely.rekit.plugin.map;

import java.io.File;

public interface Map {
	
	public File getFile();
	
	public String getName();
	
	public long getChecksum();
	
	public int getWidth();
	
	public int getHeight();
	
	public Tile getTileAt(int x, int y);
	
	public Tile getTileByIndex(short index);
	
	public short getMaxTileIndex();
	
	public int getSize();
}
