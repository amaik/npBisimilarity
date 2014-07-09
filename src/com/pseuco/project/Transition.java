package com.pseuco.project;

public class Transition {
	
	/*
	 * Transition from srcState to tarState via transAction
	 */
	private final State srcState;
	
	private final State tarState;
	
	private final Action transAction;
	
	public Transition (State src, State tar, Action act){
		this.srcState = src;
		this.tarState = tar;
		this.transAction = act;
	}

	public State getSrcState() {
		return srcState;
	}

	public State getTarState() {
		return tarState;
	}

	public Action getTransAction() {
		return transAction;
	}
	
	public Boolean isWeak() {
		if (this.transAction.getName()=="i")
			return true;
		return false;
	}
	

}
