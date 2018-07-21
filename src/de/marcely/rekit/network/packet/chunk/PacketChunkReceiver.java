package de.marcely.rekit.network.packet.chunk;

import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.network.packet.PacketFlag;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.util.BufferedReadStream;
import de.marcely.rekit.util.Util;

public class PacketChunkReceiver {
	
	private final Client client;
	
	private int currentChunk = 0;
	private boolean valid = true;
	
	public PacketFlag[] constructFlags;
	public int constructAck;
	public int constructChunksAmount;
	public byte[] constructData;
	
	public PacketChunkReceiver(Client client){
		this.client = client;
	}
}