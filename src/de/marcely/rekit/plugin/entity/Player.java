package de.marcely.rekit.plugin.entity;

import de.marcely.rekit.plugin.player.Emote;

public interface Player {
	
	public Emote getEmote();
	
	public Weapon getWeapon();
	
	public int getHealth();
	
	public int getArmor();
	
	public int getAmmo();
	
	public boolean isAlive();
}
