package de.marcely.rekit;

import lombok.Getter;

public class KickReason {
	
	@Getter private final KickReasonType type;
	@Getter private final String message;
	
	public KickReason(String msg){
		this(KickReasonType.CUSTOM, msg);
	}
	
	public KickReason(KickReasonType type){
		this(type, type.getMessage());
	}
	
	public KickReason(KickReasonType type, String msg){
		if(msg == null) msg = "";
		
		this.type = type;
		this.message = msg;
	}
	
	
	
	public static enum KickReasonType {
		TIMEOUT("Timeout (Network lag?)"),
		KICK("You have been kicked of the server."),
		BANNED("You are not permitted to join the server for %1 minutes."),
		ERROR("An error occured."),
		SERVER_CLOSE("Server closed."),
		CUSTOM("No reason specified.");
		
		@Getter private final String message;
		
		private KickReasonType(String msg){
			this.message = msg;
		}
	}
}