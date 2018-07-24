package de.marcely.rekit.util;

public class MathUtil {
	
	public static int saturateAdd(int min, int max, int current, int modifier){
		if(modifier < 0){
			if(current < min)
				return current;
			
			current += modifier;
			
			if(current < min)
				current = min;
			
			return current;
		}
		
		if(current > max)
			return current;
		
		current += modifier;
		
		if(current > max)
			current = max;
		
		return current;
	}
	
	public static float saturateAdd(float min, float max, float current, float modifier){
		if(modifier < 0){
			if(current < min)
				return current;
			
			current += modifier;
			
			if(current < min)
				current = min;
			
			return current;
		}
		
		if(current > max)
			return current;
		
		current += modifier;
		
		if(current > max)
			current = max;
		
		return current;
	}
}