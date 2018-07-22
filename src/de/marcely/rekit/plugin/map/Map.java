package de.marcely.rekit.plugin.map;

import java.io.File;

public interface Map {
	
	public File getFile();
	
	public String getName();
	
	public long getChecksum();
	
	public int getWidth();
	
	public int getHeight();
	
	public byte getGameIndexAt(int x, int y);
	
	public int getSize();
}
