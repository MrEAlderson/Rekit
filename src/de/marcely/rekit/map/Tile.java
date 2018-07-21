package de.marcely.rekit.map;

public class Tile {
	
	public final TWMap map;
	public final int x, y, index;
	
	public Tile(TWMap map, int x, int y, int index){
		this.map = map;
		this.x = x;
		this.y = y;
		this.index = index;
	}
}
