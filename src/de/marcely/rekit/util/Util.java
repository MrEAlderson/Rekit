package de.marcely.rekit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.util.huffman.AdaptiveHuffmanCompress;
import de.marcely.rekit.util.huffman.AdaptiveHuffmanDecompress;
import de.marcely.rekit.util.huffman.BitInputStream;
import de.marcely.rekit.util.huffman.BitOutputStream;

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
    
    public static byte[] arraycopy(byte[] data, int start, int end){
    	final byte[] result = new byte[end-start];
    	
    	if(result.length == 0) return result;
    	
    	System.arraycopy(data, start, result, 0, result.length);
    	
    	return result;
    }
    
    public static byte[] huffmanDecompress(byte[] data) throws IOException {
    	final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	
    	AdaptiveHuffmanDecompress.decompress(new BitInputStream(new ByteArrayInputStream(data)), stream);
    	
    	return stream.toByteArray();
    }
    
    public static byte[] huffmanCompress(byte[] data) throws IOException {
    	final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	
    	AdaptiveHuffmanCompress.compress(new ByteArrayInputStream(data), new BitOutputStream(stream));
    	
    	return stream.toByteArray();
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
