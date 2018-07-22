package de.marcely.rekit.map;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.Inflater;

import com.sun.istack.internal.Nullable;

import de.marcely.rekit.logger.Logger;
import de.marcely.rekit.util.BufferedReadStream;
import de.marcely.rekit.util.Util;

/**
 * 
 * @author Marcel S.
 * 
 * Thanks to:
 * https://github.com/prathje/tw-webgl/blob/master/twwebgl.js
 */
public class MapFile {
	
	private static final Logger LOGGER = new Logger("MapFile");
	
	public static final int ITEMTYPE_VERSION = 0;
	public static final int ITEMTYPE_INFO = 1;
	public static final int ITEMTYPE_IMAGE = 2;
	public static final int ITEMTYPE_ENVELOPE = 3;
	public static final int ITEMTYPE_GROUP = 4;
	public static final int ITEMTYPE_LAYER = 5;
	public static final int ITEMTYPE_ENVPOINTS = 6;
	
	public static final int LAYERTYPE_INVALID = 0;
	public static final int LAYERTYPE_GAME = 1;
	public static final int LAYERTYPE_TILES = 2;
	public static final int LAYERTYPE_QUADS = 3;
	
	public static final byte[] SIGNATURE1 = { 'D', 'A', 'T', 'A' };
	public static final byte[] SIGNATURE2 = { 'A', 'T', 'A', 'D' };
	public static final byte[] VERSION = { 0x4, 0x0, 0x0, 0x0 };
	
	public final File file;
	
	public MapFile(File file){
		this.file = file;
	}
	
	public @Nullable TWMap load(){
		LOGGER.info("Loading map '" + file.getName() + "'...");
		
		try{
			final byte[] data = read(file);
			
			final BufferedReadStream stream = new BufferedReadStream(data);
			
			// wrong signature
			final byte[] signature = stream.read(4);
			
			if(!Arrays.equals(signature, SIGNATURE1) && !Arrays.equals(signature, SIGNATURE2)){
				stream.close();
				return null;
			}
			
			// wrong version
			if(!Arrays.equals(stream.read(4), VERSION)){
				stream.close();
				return null;
			}
			
			final long checksum = Util.calcCRC32(data);
			/*final long size = */stream.readUnsignedInt();
			/*final long swapLen = */stream.readUnsignedInt();
			final long numItemTypes = stream.readUnsignedInt();
			final long numItems = stream.readUnsignedInt();
			final long numRawData = stream.readUnsignedInt();
			final long itemSize = stream.readUnsignedInt();
			final long dataSize = stream.readUnsignedInt();
			
			final long itemTypesStart = stream.getOffset();
			final long itemOffsetsStart = itemTypesStart+numItemTypes*12;
			final long dataOffsetsStart = itemOffsetsStart+numItems*4;
			final long dataSizesStart = dataOffsetsStart+numRawData*4;
			
			final long itemStart = dataSizesStart+numRawData*4;
			final long dataStart = itemStart+itemSize;
			
			HashMap<Integer, MapType> items = new HashMap<>();
			
			for(int i=0; i<numItemTypes; i++){
				final MapType item = new MapType();
				item.type = (int) stream.readUnsignedInt();
				item.start = (int) stream.readUnsignedInt();
				item.num = (int) stream.readUnsignedInt();
				
				items.put(item.type, item);
			}
			
			// read item offsets
			final Long itemOffsets[] = new Long[(int) numItems];
			
			for(int i=0; i<numItems; i++)
				itemOffsets[i] = stream.readUnsignedInt();
			
			
			// read data
			final MapData[] datas = new MapData[(int) numRawData];
			
			// offsets
			for(int i = 0; i<numRawData; i++){
				datas[i] = new MapData();
				datas[i].offset = stream.readUnsignedInt();
			}
			
			for(int i=0; i<numRawData; i++){
				// uncompressed size
				datas[i].size = stream.readUnsignedInt();
				
				if(i == numRawData-1)
					datas[i].compSize = dataSize-datas[i].offset;
				else
					datas[i].compSize = datas[i+1].offset-datas[i].offset;
			}
			
			// decompress data
			final byte[][] decData = new byte[(int) numRawData][];
			for(int i=0; i<numRawData; i++){
				long startOffs = datas[i].offset + dataStart;
				long endOffs = startOffs + datas[i].compSize;
				
				stream.setOffset((int) startOffs);
				
				final byte[] rawData = stream.read((int) ((long) endOffs-startOffs));
				
				final Inflater decompresser = new Inflater();
				final byte[] buffer = new byte[512000]; // 512 kb
				
				decompresser.setInput(rawData);
				decData[i] = Arrays.copyOfRange(buffer, 0, decompresser.inflate(buffer));
			}
			
			final MapType groupItem = items.get(ITEMTYPE_GROUP);
			final MapType layerItem = items.get(ITEMTYPE_LAYER);
			
			for(int i=0; i<groupItem.num; i++){
				stream.setOffset((int) ((long) itemStart+itemOffsets[groupItem.start+i]));
				
				/*final long gTypeAndId = */stream.readUnsignedInt();
				/*final int gType = (int) ((gTypeAndId>>16)&0xffff);
				final int gID = (int) (gTypeAndId&0xffff);*/
				final MapGroup group = new MapGroup(stream.read((int) stream.readUnsignedInt()));
				
				for(int l=0; l<group.numLayer; l++){
					stream.setOffset((int) (itemStart+itemOffsets[layerItem.start+group.startLayer+l]));
					
					stream.readUnsignedInt();
					final byte[] layerData = stream.read((int) stream.readUnsignedInt());
					final MapLayer layer = new MapLayer(layerData);
					
					if(layer.type == LAYERTYPE_TILES){
						final MapTileLayer layerTile = new MapTileLayer(layerData);
						final MapTile[] tiles = new MapTile[layerTile.width*layerTile.height];
						final BufferedReadStream reader = new BufferedReadStream(decData[layerTile.data]);
						
						for(int ti=0; ti<layerTile.width*layerTile.height; ti++)
							tiles[ti] = new MapTile(reader);
						
						if(layerTile.flags == 1){ // is game layer
							final TWMap map = new TWMap(file, file.getName().replace("_" + checksum, "").replace(".map", ""), checksum, layerTile.width, layerTile.height, data.length);
							
							for(int tx=0; tx<layerTile.width; tx++){
								for(int ty=0; ty<layerTile.height; ty++){
									map.tiles[tx][ty] = new Tile(map, tx, ty, tiles[ty*layerTile.width+tx].index);
								}
							}
							
							LOGGER.info("Success!");
							
							return map;
						}
					}
				}
			}
			
			stream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		LOGGER.error("Failed!!");
		
		return null;
	}
	
	private static @Nullable byte[] read(File file){
		try{
			final FileInputStream stream = new FileInputStream(file);
			final byte[] data = new byte[(int) stream.getChannel().size()];
			
			stream.read(data);
			
			stream.close();
			
			return data;
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static class MapTile {
		
		public final int index, flags;
		
		public MapTile(BufferedReadStream reader){
			this.index = reader.readUnsignedByte();
			this.flags = reader.readUnsignedByte();
			
			reader.setOffset(reader.getOffset()+2);
		}
	}
	
	public static class MapTileLayer {
		public final int version, width, height, colorEnv, colorEnvOffset, image, data;
		public final long flags;
		public final Color color;
		
		public MapTileLayer(byte[] data){
			final BufferedReadStream reader = new BufferedReadStream(data);
			reader.setOffset(4*3);
			
			this.version = (int) reader.readUnsignedInt();
			this.width = reader.readSignedInt();
			this.height = reader.readSignedInt();
			this.flags = reader.readUnsignedInt();
			this.color = new Color((int) (reader.readUnsignedInt()&0xFF), (int) (reader.readUnsignedInt()&0xFF), (int) (reader.readUnsignedInt()&0xFF), (int) (reader.readUnsignedInt()&0xFF));
			this.colorEnv = reader.readSignedInt();
			this.colorEnvOffset = reader.readSignedInt();
			this.image = reader.readSignedInt();
			this.data = reader.readSignedInt();
			
			reader.close();
		}
	}
	
	public static class MapLayer {
		public final int version, type;
		public final long flags;
		
		public MapLayer(byte[] data){
			final BufferedReadStream reader = new BufferedReadStream(data);
			
			this.version = (int) reader.readUnsignedInt();
			this.type = (int) reader.readUnsignedInt();
			this.flags = reader.readUnsignedInt();
			
			reader.close();
		}
	}
	
	public static class MapGroup {
		public final int version, offsX, offsY, parralaxX, parralaxY, startLayer,
						 numLayer, useClippig, clipX, clipY, clipW, clipH;
		
		public MapGroup(byte[] data){
			final BufferedReadStream reader = new BufferedReadStream(data);
			
			this.version = (int) reader.readUnsignedInt();
			this.offsX = reader.readSignedInt();
			this.offsY = reader.readSignedInt();
			this.parralaxX = reader.readSignedInt();
			this.parralaxY = reader.readSignedInt();
			this.startLayer = (int) reader.readUnsignedInt();
			this.numLayer = (int) reader.readUnsignedInt();
			this.useClippig = (int) reader.readUnsignedInt();
			this.clipX = reader.readSignedInt();
			this.clipY = reader.readSignedInt();
			this.clipW = reader.readSignedInt();
			this.clipH = reader.readSignedInt();
			
			reader.close();
		}
	}
	
	public static class MapType {
		public int type, start, num;	
	}
	
	public static class MapData {
		
		public long offset;
		public long size;
		public long compSize;
	}
}
