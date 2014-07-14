package com.pseuco.project;

public class State {

	private final String name;
	private Block containingBlock;

	public State(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	/*
	public boolean equals(State state){
		return (this.name.equals(state.getName()));
	}
	*/
	
	public String toString() {
		return this.name;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		State other = (State) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Block getContainingBlock() {
		return containingBlock;
	}

	public void setContainingBlock(Block containingBlock) {
		this.containingBlock = containingBlock;
	}
	
	
}
