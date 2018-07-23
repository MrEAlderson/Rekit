package de.marcely.rekit.plugin.player;

import com.sun.istack.internal.Nullable;

public enum Emote {
	
	NORMAL,
	PAIN,
	HAPPY,
	SURPRISE,
	ANGRY,
	BLINK;
	
	public int getID(){
		return this.ordinal();
	}
	
	public static @Nullable Emote ofID(int id){
		for(Emote e:values()){
			if(e.getID() == id)
				return e;
		}
		
		return null;
	}
}
