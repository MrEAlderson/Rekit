package de.marcely.rekit.map;

public class Tile {
	
	public final Map map;
	public final int x, y, index;
	
	public Tile(Map map, int x, int y, int index){
		this.map = map;
		this.x = x;
		this.y = y;
		this.index = index;
	}
}
