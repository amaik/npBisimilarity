package com.pseuco.project;

public class State {

	private final String name;

	public State(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean equals(State state){
		return (this.name.equals(state.getName()));
	}
	
	public String toString() {
		return this.name;
	}
}
