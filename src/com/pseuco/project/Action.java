package com.pseuco.project;

public class Action {
	
	private final String name;


	public Action(String name){
		this.name = name;
	}

	
	
	public String getName() {
		return name;
	}
	
	public boolean equals(Action act){
		return (this.name.equals(act.getName()));
	}
}
