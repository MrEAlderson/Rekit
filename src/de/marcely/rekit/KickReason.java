package de.marcely.rekit;

import lombok.Getter;

public class KickReason {
	
	@Getter private final KickReasonType type;
	@Getter private final String message;
	
	public KickReason(String msg){
		this(KickReasonType.CUSTOM, msg);
	}
	
	public KickReason(KickReasonType type){
		this(type, "");
	}
	
	public KickReason(KickReasonType type, String msg){
		if(msg == null) msg = "";
		
		this.type = type;
		this.message = msg;
	}
	
	
	
	public static enum KickReasonType {
		TIMEOUT,
		KICK_VOTING,
		KICK_ADMIN,
		ERROR,
		CUSTOM;
	}
}