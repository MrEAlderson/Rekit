package de.marcely.rekit.network.master;

public class MasterServerPackets {
	
	public static final byte LENGTH = 4;
	
	public static final byte[] SERVERBROWSE_HEARTBEAT = { 'b', 'e', 'a', '2' };
	
	public static final byte[] SERVERBROWSE_GETLIST = { 'r', 'e', 'q', '2' };
	public static final byte[] SERVERBROWSE_LIST = { 'l', 'i', 's', '2' };
	
	public static final byte[] SERVERBROWSE_GETCOUNT = { 'c', 'o', 'u', '2' };
	public static final byte[] SERVERBROWSE_COUNT = { 's', 'i', 'z', '2' };
	
	public static final byte[] SERVERBROWSE_GETINFO = { 'g', 'i', 'e', '3' };
	public static final byte[] SERVERBROWSE_INFO = { 'i', 'n', 'f', '3' };
	
	public static final byte[] SERVERBROWSE_GETINFO_64_LEGACY = { 'f', 's', 't', 'd' };
	public static final byte[] SERVERBROWSE_INFO_64_LEGACY = { 'd', 't', 's', 'f' };
	
	public static final byte[] SERVERBROWSE_FWCHECK = { 'f', 'w', '?', '?' };
	public static final byte[] SERVERBROWSE_FWRESPONSE = { 'f', 'w', '!', '!' };
	public static final byte[] SERVERBROWSE_FWOK = { 'f', 'w', 'o', 'k' };
	public static final byte[] SERVERBROWSE_FWERROR = { 'f', 'w', 'e', 'r' };
	
}
