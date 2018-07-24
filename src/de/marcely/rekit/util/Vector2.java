package de.marcely.rekit.util;

public class Vector2 implements Cloneable {
	
	private float x, y;
	
	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}

	/**
	 * 
	 * Gets the X component
	 * @return The X component
	 */
	public float getX(){
		return this.x;
	}
	
	/**
	 * 
	 * Gets the Y component
	 * @return The Y component
	 */
	public float getY(){
		return this.y;
	}
	
	/**
	 * 
	 * Sets the X component
	 * @param x The new X component
	 * @return Returns himself
	 */
	public Vector2 setX(float x){
		this.x = x;
		
		return this;
	}
	
	/**
	 * 
	 * Sets the Y component
	 * @param y The new Y component
	 * @return Returns himself
	 */
	public Vector2 setY(float y){
		this.y = y;
		
		return this;
	}
	
	/**
	 * 
	 * Add the components
	 * @param x The X component
	 * @param y The Y Component
	 * @return Returns himself
	 */
	public Vector2 add(float x, float y){
		this.x += x;
		this.y += y;
		
		return this;
	}
	
	/**
	 * 
	 * Does the same as add(float, float)
	 * @param vec The other vector to add with
	 * @return Returns himself
	 */
	public Vector2 add(Vector2 vec){
		add(vec.x, vec.y);
		
		return this;
	}
	
	/**
	 * 
	 * Subtract the components
	 * @param x The X component
	 * @param y The Y Component
	 * @return Returns himself
	 */
	public Vector2 subtract(float x, float y){
		this.x -= x;
		this.y -= y;
		
		return this;
	}
	
	/**
	 * Does the same as subtract(float, float)
	 * @param vec The other vector to add with
	 * @return Returns himself
	 */
	public Vector2 subtract(Vector2 vec){
		this.x -= vec.x;
		this.y -= vec.y;
		
		return this;
	}
	
	/**
	 * 
	 * Multiply the components
	 * @param x The X component
	 * @param y The Y Component
	 * @return Returns himself
	 */
	public Vector2 multiply(float x, float y){
		this.x *= x;
		this.y *= y;
		
		return this;
	}
	
	/**
	 * 
	 * Divide the components
	 * @param x The X component
	 * @param y The Y Component
	 * @return Returns himself
	 */
	public Vector2 divide(float x, float y){
		this.x /= x;
		this.y /= y;
		
		return this;
	}
	
	/**
	 * 
	 * Add the X component
	 * @param x The X component
	 * @return Returns himself
	 */
	public Vector2 addX(float x){
		this.x += x;
		
		return this;
	}
	
	/**
	 * 
	 * Subtract the X component
	 * @param x The X component
	 * @return Returns himself
	 */
	public Vector2 subtractX(float x){
		this.x -= x;
		
		return this;
	}
	
	/**
	 * 
	 * Multiply the X component
	 * @param x The X component
	 * @return Returns himself
	 */
	public Vector2 multiplyX(float x){
		this.x *= x;
		
		return this;
	}
	
	/**
	 * 
	 * Dive the X component
	 * @param x The X component
	 * @return Returns himself
	 */
	public Vector2 divideX(float x){
		this.x /= x;
		
		return this;
	}
	
	/**
	 * 
	 * Add the Y component
	 * @param y The Y component
	 * @return Returns himself
	 */
	public Vector2 addY(float y){
		this.y += y;
		
		return this;
	}
	
	/**
	 * 
	 * Subtract the Y component
	 * @param y The Y component
	 * @return Returns himself
	 */
	public Vector2 subtractY(float y){
		this.y -= y;
		
		return this;
	}
	
	/**
	 * 
	 * Multiply the Y component
	 * @param y The Y component
	 * @return Returns himself
	 */
	public Vector2 multiplyY(float y){
		this.y *= y;
		
		return this;
	}
	
	/**
	 * 
	 * Dive the Y component
	 * @param y The Y component
	 * @return Returns himself
	 */
	public Vector2 divideY(float y){
		this.y /= y;
		
		return this;
	}
	
	/**
	 * 
	 * Sets the components of this Vector to the Components of an other Vector
	 * @param vec The other vector
	 * @return Returns himself
	 */
	public Vector2 copy(Vector2 vec){
		this.x = vec.x;
		this.y = vec.y;
		
		return this;
	}
	
	/**
	 * 
	 * @param vec Returns the distance to an other Vector
	 * @return The other Vector
	 */
	public float distance(Vector2 vec){
		return (float) Math.hypot(x-vec.x, y-vec.y);
	}
	
	/**
	 * 
	 * Returns the angle of this Vector to an other Vector
	 * @param vec The other vector
	 * @return The angle in radian
	 */
	public float angleRadian(Vector2 vec){
		return (float) Math.atan2(vec.x - x, vec.y - y);
	}
	
	/**
	 * 
	 * Returns the angle of this Vector to an other Vector
	 * @param vec The other vector
	 * @return The angle in degrees
	 */
	public float angleDegrees(Vector2 vec){
		return (float) (angleRadian(vec)*180F/Math.PI);
	}
	
	/**
	 * 
	 * Sets all Components to 0
	 * @return Returns himself
	 */
	public Vector2 zero(){
		this.x = 0;
		this.y = 0;
		
		return this;
	}
	
	public Vector2 normalize(){
		final float l = 1F / length();
		
		return new Vector2(this.x*l, this.y*l);
	}
	
	public float length(){
		return (float) Math.sqrt(x*x + y*y);
	}
	
	@Override
	public Vector2 clone(){
		try{
			return (Vector2) super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		
		return null;
	}
}
