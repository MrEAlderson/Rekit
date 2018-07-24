package de.marcely.rekit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class IntCompressor {
	
	public static byte[] pack(int input){
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte cByte = (byte)((input >> 25) & 64);
        
        input = input ^ (input >> 31);
        cByte = (byte)(cByte | (input & 63));
        input >>= 6; // discard 6 bits
        
        if(input != 0){
        	cByte = (byte)(cByte | 128);
        	
            do{
            	stream.write(cByte);
            	
                cByte = (byte)(input & 127);
                input >>= 7; // discard 7 bits
                cByte = (byte)(cByte | (byte)(input != 0 ? 128 : 0));
            }while(input != 0);
        }
        
        stream.write(cByte);
        
        try{
			stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
        
        return stream.toByteArray();
	}
	
	public static Entry<Integer, Integer> /* New offset, Value */ unpack(byte[] buffer, int offset){
        final int sign = (buffer[offset] >> 6) & 1;
        int value = buffer[offset] & 63;
        
        do{
            if((buffer[offset] & 128) == 0) break;
            if(offset+1 >= buffer.length) return null;
            offset++;
            value |= (buffer[offset] & 127) << (6);

            if((buffer[offset] & 128) == 0) break;
            if(offset+1 >= buffer.length) return null;
            offset++;
            value |= (buffer[offset] & 127) << (6 + 7);

            if((buffer[offset] & 128) == 0) break;
            if(offset+1 >= buffer.length) return null;
            offset++;
            value |= (buffer[offset] & 127) << (6 + 7 + 7);

            if((buffer[offset] & 128) == 0) break;
            if(offset+1 >= buffer.length) return null;
            offset++;
            value |= (buffer[offset] & 127) << (6 + 7 + 7 + 7);

        }while(false);

        offset++;
        value ^= -sign;
        
        return new SimpleEntry<>(offset, value);
	}
	
	public static int compress(int[] inputData, int inputOffset, int inputSize, byte[] outputData, int outputOffset){
		final int startOutputOffset = outputOffset;
		
		for(int i=0; i<inputSize; i++){
			final byte[] data = pack(inputData[inputOffset++]);
			
			System.arraycopy(data, 0, outputData, outputOffset, data.length);
			
			outputOffset += data.length;
		}
		
		return outputOffset - startOutputOffset;
	}
	
	final static int decompress(byte[] inputData, int inputOffset, int inputSize, int outputData[], int outputOffset){
		final int startOutputOffset = outputOffset;
		final int end = inputOffset + inputSize;
		
		while(inputOffset < end){
			final Entry<Integer, Integer> data = unpack(inputData, inputOffset);
			
			inputOffset = data.getKey();
			outputData[outputOffset++] = data.getValue();
		}
		
		return outputOffset - startOutputOffset;
	}
}
