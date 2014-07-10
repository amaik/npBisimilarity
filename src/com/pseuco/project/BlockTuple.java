package com.pseuco.project;

public class BlockTuple {
	
	private final Block blockOne;
	private final Block blockTwo;
	
	public BlockTuple(Block one, Block two){
		this.blockOne = one;
		this.blockTwo = two;
	}
	
	public boolean equals(BlockTuple bT){
		return (this.blockOne.equals(bT.getBlockOne())
				&& this.blockTwo.equals(bT.getBlockTwo()));
	}
	
	public Block getBlockOne() {
		return blockOne;
	}

	public Block getBlockTwo() {
		return blockTwo;
	}
}
