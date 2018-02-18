package de.marcely.rekit.logger;

public class Logger {
	
	public final String identifier;
	
	public Logger(String identifier){
		this.identifier = identifier;
	}
	
	public void info(String msg){
		print("INFO", msg);
	}
	
	public void warn(String msg){
		print("WARN", msg);
	}
	
	public void error(String msg){
		print("ERROR", msg);
	}
	
	public void fatal(String msg){
		print("FATAL", msg);
	}
	
	public void debug(String msg){
		print("DEBUG", msg);
	}
	
	private void print(String type, String msg){
		System.out.println(identifier + " - " + type + " - " + msg);
	}
}
