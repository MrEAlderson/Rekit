package de.marcely.rekit.snapshot;

import com.sun.istack.internal.Nullable;

public class SnapshotDelta {
	
	private static final int HASH_LIST_SIZE = 256;
	
	public static @Nullable Snapshot unpackDelta(Snapshot from, int[] inputData, int inputOffset, int inputSize){
		final SnapshotBuilder builder = new SnapshotBuilder();
		final int endIndex = inputOffset + inputSize;
		final int deletedItemsAmount = inputData[inputOffset++];
		final int updatedItemsAmount = inputData[inputOffset++];
		inputOffset++; // temp items amount
		final int deletedOffset = inputOffset;
		
		inputOffset += deletedItemsAmount;
		
		if(inputOffset > endIndex)
			return null;
		
		builder.startBuild();
		
		for(int i=0; i<from.getItemsAmount(); i++){
			final SnapshotItem item = from.getItemOfIndex(i);
			boolean keep = true;
			
			for(int i1=0; i1<deletedItemsAmount; i1++){
				if(inputData[deletedOffset + i1] == item.key){
					keep = false;
					break;
				}
			}
			
			if(keep)
				builder.addItem(item.obj.clone(), item.getID());
		}
		
		for(int i=0; i<updatedItemsAmount; i++){
			if(inputOffset + 2 > endIndex)
				return null;
			
			final SnapshotObjectType type = SnapshotObjectType.ofID(inputData[inputOffset++]);
			final int id = inputData[inputOffset++];
			int itemSize;
			
			if(type.size > 0)
				itemSize = type.size;
			else{
				if(inputOffset + 1 > endIndex)
					return null;
				
				itemSize = inputData[inputOffset++] * 4;
			}
			
			if(itemSize < 0 || !rangeCheck(endIndex, inputOffset, itemSize / 4))
				return null;
			
			final int key = ((int) type.getID() << 16) | id;
			final SnapshotItem c = builder.findItem(key);
			SnapshotObject newItem = c != null ? c.obj : null;
			
			if(newItem == null){
				final SnapshotObject item = type.newInstance();
				
				if(builder.addItem(item, id));
					newItem = item;
			}
			
			if(newItem == null)
				return null;
			
			final SnapshotItem fromItem = from.findItem(key);
			
			if(fromItem != null)
				undiffItem(fromItem.obj, inputData, inputOffset, newItem);
			else
				newItem.deserialize(inputData, inputOffset);
			
			inputOffset += itemSize / 4;
		}
		
		return builder.endBuild();
	}
	
	public static boolean rangeCheck(int endIndex, int currentIndex, int size){
		return currentIndex + size <= endIndex;
	}
	
	public static int createDelta(Snapshot from, Snapshot to, int[] outputData){
		int deletedItemsAmount = 0;
		int updatedItemsAmount = 0;
		int tempItemsAmount = 0;
		int outputOffset = 3;
		
		final HashItem[] hashItems = new HashItem[HASH_LIST_SIZE];
		
		for(int i=0; i<hashItems.length; i++)
			hashItems[i] = new HashItem();
		
		generateHash(hashItems, to);
		
		// pack deleted stuff
		for(int i=0; i<from.getItemsAmount(); i++){
			final SnapshotItem fromItem = from.getItemOfIndex(i);
			
			if(getItemIndexHashed(fromItem.key, hashItems) == -1){
				// deleted
				deletedItemsAmount++;
				outputData[outputOffset++] = fromItem.key;
			}
		}
		
		generateHash(hashItems, from);
		final int[] pastIndecies = new int[SnapshotBuilder.SNAPSHOT_MAX_ITEMS];
		
        // fetch previous indices
        // we do this as a separate pass because it helps the cache
		for(int i=0; i<to.getItemsAmount(); i++)
			pastIndecies[i] = getItemIndexHashed(to.getItemOfIndex(i).key, hashItems);
		
		for(int i=0; i<to.getItemsAmount(); i++){
			final SnapshotItem currentItem = to.getItemOfIndex(i);
			final int pastIndex = pastIndecies[i];
			
			if(pastIndex != -1){
				final SnapshotItem pastItem = from.getItemOfIndex(pastIndex);
				int offset = outputOffset + 3;
				
				if(currentItem.getType().size > 0)
					offset = outputOffset + 2;
				
				if(diffItem(pastItem.obj, currentItem.obj, outputData, offset) != 0){
					outputData[outputOffset++] = currentItem.getType().getID();
					outputData[outputOffset++] = currentItem.getID();
					
					if(currentItem.getType().size <= 0)
						outputData[outputOffset++] = currentItem.obj.serializeLength();
					
					outputOffset += currentItem.obj.serializeLength();
					updatedItemsAmount++;
				}
			
			}else{
				outputData[outputOffset++] = currentItem.getType().getID();
				outputData[outputOffset++] = currentItem.getID();
				
				if(currentItem.getType().size <= 0)
					outputData[outputOffset++] = currentItem.obj.serializeLength();
				
				final int[] data = new int[currentItem.obj.serializeLength()];
				
				currentItem.obj.serialize(data, 0);
				System.arraycopy(data, 0, outputData, outputOffset, data.length);
				
				outputOffset += data.length;
				updatedItemsAmount++;
			}
		}
		
		if(deletedItemsAmount == 0 && updatedItemsAmount == 0 && tempItemsAmount == 0)
			return 0;
		
		outputData[0] = deletedItemsAmount;
		outputData[1] = updatedItemsAmount;
		outputData[2] = tempItemsAmount;
		
		return outputOffset;
	}
	
	private static void undiffItem(SnapshotObject past, int[] inputData, int inputOffset, SnapshotObject newItem){
		final int[] pastData = new int[past.serializeLength()];
		final int[] newData = new int[past.serializeLength()];
		
		past.serialize(pastData, 0);
		
		for(int i=0; i<past.serializeLength(); i++)
			newData[i] = pastData[i] + inputData[inputOffset + i];
		
		newItem.deserialize(newData, 0);
	}
	
	private static int diffItem(SnapshotObject past, SnapshotObject current, int[] outputData, int outputOffset){
		int needed = 0;
		final int[] pastData = new int[past.serializeLength()];
		final int[] currentData = new int[current.serializeLength()];
		
		past.serialize(pastData, 0);
		current.serialize(currentData, 0);
		
		for(int i=0; i<current.serializeLength(); i++){
			final int out = currentData[i] - pastData[i];
			
			needed |= out;
			outputData[outputOffset++] = out;
		}
		
		return needed;
	}
	
	public static int getItemIndexHashed(int key, HashItem[] hashItems){
		final int hashId = ((key >> 12) & 0b1111_0000) | (key & 0b1111);
		
		for(int i=0; i<hashItems[hashId].amount; i++){
			if(hashItems[hashId].keys[i] == key)
				return hashItems[hashId].indexes[i];
		}
		
		return -1;
	}
	
	public static void generateHash(HashItem[] hashItems, Snapshot snap){
		for(int i=0; i<hashItems.length; i++)
			hashItems[i].amount = 0;
		
		for(int i=0; i<snap.getItemsAmount(); i++){
			final int key = snap.getItemOfIndex(i).key;
			final int hashId = ((key >> 12) & 0b1111_0000) | (key & 0b1111);
			
			if(hashItems[hashId].amount != 64){
				hashItems[hashId].indexes[hashItems[hashId].amount] = i;
				hashItems[hashId].keys[hashItems[hashId].amount] = key;
				hashItems[hashId].amount++;
			}
		}
	}
	
	public static class HashItem {
		
		public int amount;
		public final int[] keys;
		public final int[] indexes;
		
		public HashItem(){
			this.amount = 0;
			this.keys = new int[64];
			this.indexes = new int[64];
		}
	}
}