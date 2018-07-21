package de.marcely.rekit.network.server;

public enum ClientState {
	
	PENDING,
	CONNECT,
	ONLINE,
	ONLINE_AUTH,
	ONLINE_CONNECTING,
	DISCONNECTED;
	
	public boolean isOnline(){
		switch(this){
		case ONLINE:
		case ONLINE_AUTH:
			return true;
		default:
			return false;
		}
	}
}
