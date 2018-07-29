package de.marcely.rekit.plugin.entity;

import java.awt.Color;
import java.net.InetAddress;

import de.marcely.rekit.plugin.player.Emote;

public interface Player {
	
	public Emote getEmote();
	
	public Weapon getWeapon();
	
	public int getHealth();
	
	public int getArmor();
	
	public int getAmmo();
	
	public boolean isAlive();
	
	public String getName();
	
	public String getClan();
	
	public boolean hasBodyColor();
	
	public boolean hasFeetColor();
	
	public Color getBodyColor();
	
	public Color getFeetColor();
	
	public String getSkinName();
	
	public boolean isRealPlayer();
	
	public InetAddress getAddress();
	
	public int getPort();
	
	public long getLoginTime();
}
