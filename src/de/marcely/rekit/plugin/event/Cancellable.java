package de.marcely.rekit.plugin.event;

public interface Cancellable {

	public void setCancelled(boolean cancel);
	
	public boolean isCancelled();
}
