package de.marcely.rekit.map;

import de.marcely.rekit.plugin.map.Tile;
import de.marcely.rekit.plugin.map.TileType;

public class TWTile implements Tile {
	
	private final TileType type;
	private final short index;
	
	public TWTile(short index){
		this.type = TileType.ofIndex(index);
		this.index = index;
	}

	@Override
	public TileType getType(){
		return this.type;
	}

	@Override
	public short getIndex(){
		return this.index;
	}
}
