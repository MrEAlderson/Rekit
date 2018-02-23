package de.marcely.rekit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.istack.internal.Nullable;

public class Util {
	
	public static @Nullable InetAddress getInetAddress(String address){
		try{
			return InetAddress.getByName(address);
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
    public static byte[] concat(byte[] a1, byte[] a2){
    	final byte[] c = new byte[a1.length+a2.length];
    	
		System.arraycopy(a1, 0, c, 0, a1.length);
		System.arraycopy(a2, 0, c, a1.length, a2.length);
		
		return c;
    }
    
    private final static char[] HEXMAP = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte... bytes){
        String str = "";
        
        for(int j=0; j<bytes.length; j++){
            
            str += "0x";
            str += byteToHex(bytes[j]);
            
            if(j < bytes.length-1) str += " ";
        }
        
        return str;
    }
    
    public static String byteToHex(byte b){
    	final char[] c = new char[2];
    	final int v = b & 0xFF;
    	
        c[0] = HEXMAP[v >>> 4];
        c[1] = HEXMAP[v & 0x0F];
        
        return new String(c);
    }
    
    public static byte[] encodeMD5(byte[] input){
		try{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			
	    	md.update(input);
	    	return md.digest();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		
		return null;
    }
    
    public static String getIdentifier(InetAddress address, int port){
    	return address.getHostAddress() + ":" + port;
    }
}
