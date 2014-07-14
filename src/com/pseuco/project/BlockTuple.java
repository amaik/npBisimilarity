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
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blockOne == null) ? 0 : blockOne.hashCode());
		result = prime * result
				+ ((blockTwo == null) ? 0 : blockTwo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockTuple other = (BlockTuple) obj;
		if (blockOne == null) {
			if (other.blockOne != null)
				return false;
		} else if (!blockOne.equals(other.blockOne))
			return false;
		if (blockTwo == null) {
			if (other.blockTwo != null)
				return false;
		} else if (!blockTwo.equals(other.blockTwo))
			return false;
		return true;
	}

}
