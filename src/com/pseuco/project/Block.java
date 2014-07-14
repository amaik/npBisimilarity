package com.pseuco.project;

import java.util.HashSet;

public class Block {
	private final HashSet<State> states;
	
	
	public HashSet<State> getStates() {
		return states;
	}

	public Block(HashSet<State> states){
		this.states = states;
	}
	
	public boolean equals(Block b){
		return this.states.equals(b.getStates());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((states == null) ? 0 : states.hashCode());
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
		Block other = (Block) obj;
		if (states == null) {
			if (other.states != null)
				return false;
		} else if (!states.equals(other.states))
			return false;
		return true;
	}
}
