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
	
	public boolean isIntern() {
		if (this.transAction.getName().equals("τ"))
			return true;
		return false;
	}
	
	public boolean equals(Transition trans){
		return(this.srcState.equals(trans.getSrcState())
				  && this.tarState.equals(trans.getTarState())
				    && this.transAction.equals(trans.getTransAction()));
	}
	

}
