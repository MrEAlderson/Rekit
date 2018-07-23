package de.marcely.rekit.plugin.player;

import com.sun.istack.internal.Nullable;

public enum HookState {
	
	RETRACTED(-1),
	IDLE(0),
	RETRACT_START(1),
	RETRACT_PROCESS(2),
	RETRACT_END(3),
	FLYING(4),
	GRABBED(5);
	
	private final int id;
	
	private HookState(int id){
		this.id = id;
	}
	
	public int getID(){
		return this.id;
	}
	
	public static @Nullable HookState ofID(int id){
		for(HookState hs:values()){
			if(hs.id == id)
				return hs;
		}
		
		return null;
	}
}
