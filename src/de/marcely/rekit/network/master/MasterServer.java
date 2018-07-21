package de.marcely.rekit.network.master;

import java.net.InetAddress;

import de.marcely.rekit.util.Util;

public enum MasterServer {
	
	MASTER1(Util.getInetAddress("master1.teeworlds.com")),
	MASTER2(Util.getInetAddress("master2.teeworlds.com")),
	MASTER3(Util.getInetAddress("master3.teeworlds.com")),
	MASTER4(Util.getInetAddress("master4.teeworlds.com"));
	
	public final InetAddress address;
	
	private MasterServer(InetAddress address){
		this.address = address;
	}
	
	public static MasterServer byAddress(InetAddress address){
		for(MasterServer ms:values()){
			if(ms.address.equals(address))
				return ms;
		}
		
		return null;
	}
}
