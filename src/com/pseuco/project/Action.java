package com.pseuco.project;

public class Action {
	
	private final String name;


	public Action(String name){
		this.name = name;
	}

	
	
	public String getName() {
		return name;
	}
	/*
	public boolean equals(Action act){
		return (this.name.equals(act.getName()));
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
		Action other = (Action) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
