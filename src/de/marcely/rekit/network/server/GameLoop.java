package de.marcely.rekit.network.server;

public class GameLoop {
	
	private final Server server;

	public int tps;
	public long execTime;
	
	public GameLoop(Server server){
		this.server = server;
	}
	
	public void run(){
		long last = System.currentTimeMillis();
		final double nanoSec = 1000000000/server.getMaxTicksPerSecond();
		double delta = 0;
		int rTps = 0;
		long tpsTime = System.currentTimeMillis();
		
		while(server.isRunning()){
			final long now = System.nanoTime();
			
			delta += (now - last) / nanoSec;
			last = now;
			
			if(delta >= 1){
				final long startExec = System.currentTimeMillis();
				
				tick();
				
				rTps++;
				delta = 0;
				
				this.execTime = System.currentTimeMillis()-startExec;
			}
			
			if(tpsTime+1000 < System.currentTimeMillis()){
				tpsTime = System.currentTimeMillis();
				this.tps = rTps;
				rTps = 0;
			}
		}
	}
	
	private void tick(){
		try{
			server.protocol.tick();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}