package com.pseuco.project;

public class BlockTuple {
	
	private final Block blockL;
	private final Block stateR;
	
	public BlockTuple(Block stateL, Block stateR){
		this.blockL = stateL;
		this.stateR = stateR;
	}
	
	public Block getBlockL() {
		return blockL;
	}

	public Block getBlockR() {
		return stateR;
	}

	public boolean equals(BlockTuple tuple){
		return (this.blockL.equals(tuple.getBlockL()) && this.stateR.equals(tuple.getBlockR()));
	}

}
