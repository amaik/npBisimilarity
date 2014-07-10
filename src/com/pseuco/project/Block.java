package com.pseuco.project;

import java.util.HashSet;

public class Block {
	
	private final HashSet<State> states;
	
	public HashSet<State> getStates() {
		return states;
	}

	public Block (HashSet<State> states){
		this.states = states;
	}
	

	public boolean equals(Block b){
		return this.states.equals(b.getStates());
	}
}
