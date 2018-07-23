package de.marcely.rekit.network.packet.game;

import de.marcely.rekit.network.packet.DataPacket;
import de.marcely.rekit.network.packet.PacketInputStream;
import de.marcely.rekit.network.packet.PacketOutputStream;
import de.marcely.rekit.network.packet.PacketType;
import de.marcely.rekit.plugin.TuningParameter;

public class PacketGameServerTuneParams extends DataPacket {
	
	public float[] values;
	
	@Override
	public PacketType getType(){ return PacketType.GAME_SV_TUNE_PARAMS; }

	@Override
	public void write(PacketOutputStream stream){
		for(int i=0; i<this.values.length; i++)
			stream.writeInt((int) ((float) this.values[i]*100F));
	}

	@Override
	public void read(PacketInputStream stream){
		this.values = new float[TuningParameter.values().length];
		
		for(int i=0; i<this.values.length; i++)
			this.values[i] = stream.readInt()/100F;
	}
}
