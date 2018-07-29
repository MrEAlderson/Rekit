package de.marcely.rekit.util;

public class Huffman {
	
	public static final int SYMBOL_EOF = 256;
	public static final int SYMBOLS_MAX = SYMBOL_EOF + 1;
	public static final int NODES_MAX = SYMBOLS_MAX * 2 - 1;
	public static final int LUTBITS = 10;
	public static final int LUTSIZE = 1 << LUTBITS;
	public static final int LUTMASK = LUTSIZE - 1;
	
	private Node[] nodes;
	private Node[] decodeLut;
	private Node startNode;
	private int nodesAmount;
	
	public Huffman(int[] frequencies){
		try{
			this.nodes = new Node[NODES_MAX];
			
			for(int i=0; i<NODES_MAX; i++)
				this.nodes[i] = new Node();
			
			this.decodeLut = new Node[LUTSIZE];
			this.startNode = new Node();
			this.nodesAmount = 0;
			
			treeConstruct(frequencies);
			
			for(int i=0; i<LUTSIZE; i++){
				int bits = i;
				Node node = this.startNode;
				
				for(int k=0; k<LUTBITS; k++){
					node = this.nodes[node.leafs[bits&1]];
					bits >>= 1;
				
					if(node == null)
						break;
					
					if(node.bitsAmount != 0){
						this.decodeLut[i] = node;
						break;
					}
				}
				
				this.decodeLut[i] = node;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void treeConstruct(int[] frequencies){
		final ConstructNode[] nodesLeft = new ConstructNode[SYMBOLS_MAX];
		int nodesLeftAmount = SYMBOLS_MAX;
		
		for(int i=0; i<SYMBOLS_MAX; i++){
			this.nodes[i].bitsAmount = 0xFFFFFFFF;
			this.nodes[i].symbol = i;
			this.nodes[i].leafs[0] = 0xFFFF;
			this.nodes[i].leafs[1] = 0xFFFF;
			
			nodesLeft[i] = new ConstructNode();
			
			if(i == SYMBOL_EOF)
				nodesLeft[i].frequency = 1;
			else
				nodesLeft[i].frequency = frequencies[i];
			
			nodesLeft[i].nodeID = i;
		}
		
		this.nodesAmount = SYMBOLS_MAX;
		
		while(nodesLeftAmount > 1){
			bubbleSort(nodesLeft, nodesLeftAmount);
			
			this.nodes[this.nodesAmount].bitsAmount = 0;
			this.nodes[this.nodesAmount].leafs[0] = nodesLeft[nodesLeftAmount - 1].nodeID;
			this.nodes[this.nodesAmount].leafs[1] = nodesLeft[nodesLeftAmount - 2].nodeID;
			
			nodesLeft[nodesLeftAmount-2].nodeID = this.nodesAmount;
			nodesLeft[nodesLeftAmount-2].frequency =
					nodesLeft[nodesLeftAmount-1].frequency +
					nodesLeft[nodesLeftAmount-2].frequency;
			
			this.nodesAmount++;
			nodesLeftAmount--;
		}
		
		this.startNode = this.nodes[this.nodesAmount-1];
		setBitsRecursively(this.startNode, 0, 0);
	}
	
	private void setBitsRecursively(Node node, int bits, long depth){
		if(node.leafs[1] != 0xFFFF)
			setBitsRecursively(this.nodes[node.leafs[1]], bits | (1 << depth), depth + 1);
		if(node.leafs[0] != 0xFFFF)
			setBitsRecursively(this.nodes[node.leafs[0]], bits, depth + 1);
		
		if(node.bitsAmount != 0){
			node.bits = bits;
			node.bitsAmount = depth;
		}
	}
	
	public int compress(byte[] source, int sourceOffset, int sourceSize, byte[] output, int outputOffset, int maxOutputSize){
		int sourceIndex = sourceOffset;
		int sourceEnd = sourceIndex + sourceSize;
		int outputIndex = outputOffset;
		int bits = 0;
		int bitsAmount = 0;
		
		if(sourceSize != 0){
			int symbol = source[sourceIndex++]&0xFF;
			
			while(sourceIndex != sourceEnd){
				bits |= this.nodes[symbol].bits << bitsAmount;
				bitsAmount += this.nodes[symbol].bitsAmount;
				
				symbol = source[sourceIndex++]&0xFF;
				
				while(bitsAmount >= 8){
					output[outputIndex++] = (byte) (bits & 0xFF);
					
					if(outputIndex >= output.length ||
					   outputIndex >= outputOffset + maxOutputSize)
						return -1;
					
					bits >>= 8;
					bitsAmount -= 8;
				}
			}
			
			bits |= this.nodes[symbol].bits << bitsAmount;
			bitsAmount += this.nodes[symbol].bitsAmount;
			
			while(bitsAmount >= 8){
				output[outputIndex++] = (byte) (bits & 0xFF);
				
				if(outputIndex >= output.length ||
				   outputIndex >= outputOffset + maxOutputSize)
					return -1;
				
				bits >>= 8;
				bitsAmount -= 8;
			}
		}
		
		bits |= this.nodes[SYMBOL_EOF].bits << bitsAmount;
		bitsAmount += this.nodes[SYMBOL_EOF].bitsAmount;
		
		while(bitsAmount >= 8){
			output[outputIndex++] = (byte)(bits & 0xFF);
			
			if(outputIndex >= output.length ||
			   outputIndex >= outputOffset + maxOutputSize)
				return -1;
			
			bits >>= 8;
			bitsAmount -= 8;
		}
		
		output[outputIndex++] = (byte) bits;
		
		return outputIndex - outputOffset;
	}
	
	public int decompress(byte[] source, int sourceOffset, int sourceSize, byte[] output, int outputOffset, int maxOutputSize){
		int sourceIndex = sourceOffset;
		int sourceEnd = sourceIndex + sourceSize;
		int outputIndex = outputOffset;
		int bits = 0;
		int bitsAmount = 0;
		
		while(true){
			Node node = null;
			
			if(bitsAmount >= LUTBITS)
				node = this.decodeLut[bits & LUTMASK];
			
			while(bitsAmount < 24 && sourceIndex < sourceEnd){
				bits |= (source[sourceIndex++]&0xFF) << bitsAmount;
				bitsAmount += 8;
			}
			
			if(node == null){
				node = this.decodeLut[bits & LUTMASK];
				
				if(node == null)
					return -1;
			}
			
			if(node.bitsAmount != 0){
				bits >>= (int) node.bitsAmount;
				bitsAmount -= (int) node.bitsAmount;
			
			}else{
				bits >>= LUTBITS;
				bitsAmount -= LUTBITS;
				
				while(true){
					node = this.nodes[node.leafs[bits & 1]];
					bitsAmount--;
					bits >>= 1;
					
					if(node.bitsAmount != 0)
						break;
					
					if(bitsAmount == 0)
						return -1;
				}
			}
			
			if(node.bits == this.nodes[SYMBOL_EOF].bits)
				break;
			
			if(outputIndex >= output.length ||
			   outputIndex >= outputOffset + maxOutputSize)
				return -1;
			
			output[outputIndex++] = (byte) (node.symbol);
		}
		
		return outputIndex - outputOffset;
	}
	
	private static void bubbleSort(ConstructNode[] nodes, int size){
		boolean changed = true;
		
		while(changed){
			changed = false;
			
			for(int i=0; i<size-1; i++){
				if(nodes[i].frequency < nodes[i+1].frequency){
					final ConstructNode temp = nodes[i];
					
					nodes[i] = nodes[i+1];
					nodes[i+1] = temp;
					
					changed = true;
				}
			}
			
			size--;
		}
	}
	
	
	
	private static class ConstructNode {
		
		public int nodeID;
		public int frequency;
	}
	
	private static class Node {
		
		public int bits;
		public long bitsAmount;
		public int[] leafs = new int[2];
		public int symbol;
	}
}
