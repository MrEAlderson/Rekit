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
	 * @return The new X component
	 */
	public void setX(float x){
		this.x = x;
	}
	
	/**
	 * 
	 * Sets the Y component
	 * @return The new Y component
	 */
	public void setY(float y){
		this.y = y;
	}
	
	/**
	 * 
	 * Add the components
	 * @param x The X component
	 * @param y The Y Component
	 */
	public void add(float x, float y){
		this.x += x;
		this.y += y;
	}
	
	/**
	 * 
	 * Subtract the components
	 * @param x The X component
	 * @param y The Y Component
	 */
	public void subtract(float x, float y){
		this.x -= x;
		this.y -= y;
	}
	
	/**
	 * 
	 * Multiply the components
	 * @param x The X component
	 * @param y The Y Component
	 */
	public void multiply(float x, float y){
		this.x *= x;
		this.y *= y;
	}
	
	/**
	 * 
	 * Divide the components
	 * @param x The X component
	 * @param y The Y Component
	 */
	public void divide(float x, float y){
		this.x /= x;
		this.y /= y;
	}
	
	/**
	 * 
	 * Add the X component
	 * @param x The X component
	 */
	public void addX(float x){
		this.x += x;
	}
	
	/**
	 * 
	 * Subtract the X component
	 * @param x The X component
	 */
	public void subtractX(float x){
		this.x -= x;
	}
	
	/**
	 * 
	 * Multiply the X component
	 * @param x The X component
	 */
	public void multiplyX(float x){
		this.x *= x;
	}
	
	/**
	 * 
	 * Dive the X component
	 * @param x The X component
	 */
	public void divideX(float x){
		this.x /= x;
	}
	
	/**
	 * 
	 * Add the Y component
	 * @param y The Y component
	 */
	public void addY(float y){
		this.y += y;
	}
	
	/**
	 * 
	 * Subtract the Y component
	 * @param y The Y component
	 */
	public void subtractY(float y){
		this.y -= y;
	}
	
	/**
	 * 
	 * Multiply the Y component
	 * @param y The Y component
	 */
	public void multiplyY(float y){
		this.y *= y;
	}
	
	/**
	 * 
	 * Dive the Y component
	 * @param y The Y component
	 */
	public void divideY(float y){
		this.y /= y;
	}
	
	/**
	 * 
	 * Sets the components of this Vector to the Components of an other Vector
	 * @param vec The other vector
	 */
	public void copy(Vector2 vec){
		this.x = vec.x;
		this.y = vec.y;
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
	 */
	public void zero(){
		this.x = 0;
		this.y = 0;
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
