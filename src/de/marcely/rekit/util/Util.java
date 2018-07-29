package de.marcely.rekit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.CRC32;

import com.sun.istack.internal.Nullable;

public class Util {
	
    private static final int[] HUFFMAN_FREQ_TABLE = {
            1<<30, 4545, 2657, 431, 1950, 919, 444, 482, 2244, 617, 838, 542, 715, 1814, 304, 240, 754, 212, 647, 186,
            283, 131, 146, 166, 543, 164, 167, 136, 179, 859, 363, 113, 157, 154, 204, 108, 137, 180, 202, 176,
            872, 404, 168, 134, 151, 111, 113, 109, 120, 126, 129, 100, 41, 20, 16, 22, 18, 18, 17, 19,
            16, 37, 13, 21, 362, 166, 99, 78, 95, 88, 81, 70, 83, 284, 91, 187, 77, 68, 52, 68,
            59, 66, 61, 638, 71, 157, 50, 46, 69, 43, 11, 24, 13, 19, 10, 12, 12, 20, 14, 9,
            20, 20, 10, 10, 15, 15, 12, 12, 7, 19, 15, 14, 13, 18, 35, 19, 17, 14, 8, 5,
            15, 17, 9, 15, 14, 18, 8, 10, 2173, 134, 157, 68, 188, 60, 170, 60, 194, 62, 175, 71,
            148, 67, 167, 78, 211, 67, 156, 69, 1674, 90, 174, 53, 147, 89, 181, 51, 174, 63, 163, 80,
            167, 94, 128, 122, 223, 153, 218, 77, 200, 110, 190, 73, 174, 69, 145, 66, 277, 143, 141, 60,
            136, 53, 180, 57, 142, 57, 158, 61, 166, 112, 152, 92, 26, 22, 21, 28, 20, 26, 30, 21,
            32, 27, 20, 17, 23, 21, 30, 22, 22, 21, 27, 25, 17, 27, 23, 18, 39, 26, 15, 21,
            12, 18, 18, 27, 20, 18, 15, 19, 11, 17, 33, 12, 18, 15, 19, 18, 16, 26, 17, 18,
            9, 10, 25, 22, 22, 17, 20, 16, 6, 16, 15, 20, 14, 18, 24, 335, 1517
     };
	
    public static final Random RAND = new Random();
	public static final Huffman HUFFMAN = new Huffman(HUFFMAN_FREQ_TABLE);
	
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
    
    public static byte[] arraycopy(byte[] data, int start, int end){
    	final byte[] result = new byte[end-start];
    	
    	if(result.length == 0) return result;
    	
    	System.arraycopy(data, start, result, 0, result.length);
    	
    	return result;
    }
    
    public static boolean compare(byte[] a1, byte[] a2){
    	return compare(a1, a2, false);
    }
    
    public static boolean compare(byte[] a1, byte[] a2, boolean ignoreLength){
    	if(!ignoreLength && a1.length != a2.length) return false;
    	
    	for(int i=0; i<a1.length; i++){
    		if(a1[i] != a2[i])
    			return false;
    	}
    	
    	return true;
    }
    
	public static @Nullable Long calcCRC32(byte[] data){
		final CRC32 crc = new CRC32();
		
		crc.update(data);
		
		return crc.getValue();
	}
	
	public static String intsToString(int[] ints){
		final byte[] bytes = new byte[ints.length*4];
		int size = 0;
		
        for(int i=0; i<ints.length; i++){
            bytes[i * 4 + 0] = (byte) (((ints[i] >> 24) & 0b1111_1111) - 128);
            if(bytes[i * 4 + 0] < 32) return getString(bytes, size, StandardCharsets.UTF_8);
            size++;

            bytes[i * 4 + 1] = (byte) (((ints[i] >> 16) & 0b1111_1111) - 128);
            if (bytes[i * 4 + 1] < 32) return getString(bytes, size, StandardCharsets.UTF_8);
            size++;

            bytes[i * 4 + 2] = (byte) (((ints[i] >> 8) & 0b1111_1111) - 128);
            if (bytes[i * 4 + 2] < 32) return getString(bytes, size, StandardCharsets.UTF_8);
            size++;

            bytes[i * 4 + 3] = (byte) ((ints[i] & 0b1111_1111) - 128);
            if (bytes[i * 4 + 3] < 32) return getString(bytes, size, StandardCharsets.UTF_8);
            size++;
        }
        
        return getString(bytes, size, StandardCharsets.UTF_8);
	}
	
	public static String getString(byte[] bytes, int size, Charset charset){
		String str = new String(bytes, charset);
		
		if(str.length() > size)
			str = str.substring(size);
		
		return str;
	}
	
	public static int[] stringToInts(String input, int size){
		final int[] ints = new int[size];
		byte[] bytes = new byte[0];
		int index = 0;
		
		if(input != null && !input.isEmpty())
			bytes = input.getBytes(StandardCharsets.UTF_8);
		
		for(int i=0; i<size; i++){
			final int[] buffer = new int[4];
			
			for(int c=0; c<buffer.length && index < bytes.length; c++, index++){
				buffer[c] = bytes[index] >= 128 ?
						bytes[index] - 256 :
						bytes[index];
			}
			
            ints[i] = ((buffer[0] + 128) << 24) | 
                    ((buffer[1] + 128) << 16) | 
                    ((buffer[2] + 128) << 0x08) | 
                    ((buffer[3] + 128) << 00);
		}
		
		ints[ints.length - 1] = (int) (ints[ints.length - 1] & 0xFFFFFF00);
		
		return ints;
	}
}
