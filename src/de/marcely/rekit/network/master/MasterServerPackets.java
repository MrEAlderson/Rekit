package de.marcely.rekit.network.master;

import de.marcely.rekit.util.Util;

public class MasterServerPackets {
	
	public static final byte[] SERVERBROWSE_HEARTBEAT = init(new byte[]{ 'b', 'e', 'a', '2' });
	
	public static final byte[] SERVERBROWSE_GETLIST = init(new byte[]{ 'r', 'e', 'q', '2' });
	public static final byte[] SERVERBROWSE_LIST = init(new byte[]{ 'l', 'i', 's', '2' });
	
	public static final byte[] SERVERBROWSE_GETCOUNT = init(new byte[]{ 'c', 'o', 'u', '2' });
	public static final byte[] SERVERBROWSE_COUNT = init(new byte[]{ 's', 'i', 'z', '2' });
	
	public static final byte[] SERVERBROWSE_GETINFO = init(new byte[]{ 'g', 'i', 'e', '3' });
	public static final byte[] SERVERBROWSE_INFO = init(new byte[]{ 'i', 'n', 'f', '3' });
	
	public static final byte[] SERVERBROWSE_GETINFO_64_LEGACY = init(new byte[]{ 'f', 's', 't', 'd' });
	public static final byte[] SERVERBROWSE_INFO_64_LEGACY = init(new byte[]{ 'd', 't', 's', 'f' });
	
	public static final byte[] SERVERBROWSE_FWCHECK = init(new byte[]{ 'f', 'w', '?', '?' });
	public static final byte[] SERVERBROWSE_FWRESPONSE = init(new byte[]{ 'f', 'w', '!', '!' });
	public static final byte[] SERVERBROWSE_FWOK = init(new byte[]{ 'f', 'w', 'o', 'k' });
	public static final byte[] SERVERBROWSE_FWERROR = init(new byte[]{ 'f', 'w', 'e', 'r' });
	
	private static byte[] init(byte[] bytes){
		return Util.concat(new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, bytes);
	}
}
