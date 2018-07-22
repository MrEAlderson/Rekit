package de.marcely.rekit;

public enum Message {
	
	KICK_WEAK_CONNECTION_TIMEOUT("Too weak connection: Timeout"),
	KICK_WEAK_CONNECTION_ACK("Too weak connection: Not acked for too long"),
	KICK_WEAK_CONNECTION_OUT_OF_BUFFER("Too weak connection: Out of buffer"),
	KICK_KICK("You have been kicked out of the server"),
	KICK_BANNED("You are not permitted to join the server for %1 minutes"),
	KICK_ERROR("An error occured"),
	KICK_WRONG_PASSWORD("Wrong password"),
	KICK_SERVER_CLOSE("Server has been closed"),
	KICK_WRONG_VERSION("Wrong version. Server is running %1 and client %2");
	
	public final String msg;
	
	private Message(String msg){
		this.msg = msg;
	}
}
