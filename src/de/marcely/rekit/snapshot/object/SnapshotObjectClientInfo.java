package de.marcely.rekit.snapshot.object;

import de.marcely.rekit.snapshot.SnapshotObject;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.util.Util;

public class SnapshotObjectClientInfo extends SnapshotObject implements Cloneable  {

	public String name;
	public String clan;
	public int country;
	public String skin;
	public boolean hasCustomColor;
	public int bodyColor;
	public int feetColor;
	
	@Override
	public SnapshotObjectType getType(){
		return SnapshotObjectType.OBJECT_CLIENT_INFO;
	}

	@Override
	public int serializeLength(){
		return 17;
	}

	@Override
	public void deserialize(int[] data, int offset){
		this.name = Util.intsToString(new int[]{
				data[offset++],	data[offset++],
				data[offset++], data[offset++]
		});
		this.clan = Util.intsToString(new int[]{
				data[offset++],	data[offset++],
				data[offset++]
		});
		this.country = data[offset++];
		this.skin = Util.intsToString(new int[]{
				data[offset++],	data[offset++],
				data[offset++], data[offset++],
				data[offset++], data[offset++]
		});
		this.hasCustomColor = data[offset++] != 0;
		this.bodyColor = data[offset++];
		this.feetColor = data[offset++];
	}

	@Override
	public void serialize(int[] data, int offset){
		final int[] name = Util.stringToInts(this.name, 4);
		final int[] clan = Util.stringToInts(this.clan, 3);
		final int[] skin = Util.stringToInts(this.skin, 6);
		
		data[offset++] = name[0];
		data[offset++] = name[1];
		data[offset++] = name[2];
		data[offset++] = name[3];
		data[offset++] = clan[0];
		data[offset++] = clan[1];
		data[offset++] = clan[2];
		data[offset++] = skin[0];
		data[offset++] = skin[1];
		data[offset++] = skin[2];
		data[offset++] = skin[3];
		data[offset++] = skin[4];
		data[offset++] = skin[5];
		data[offset++] = this.hasCustomColor ? 1 : 0;
		data[offset++] = this.bodyColor;
		data[offset++] = this.feetColor;
	}
	
	@Override
	public SnapshotObjectClientInfo clone(){
		return (SnapshotObjectClientInfo) super.clone();
	}
}
