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
	/*
	public boolean equals(Transition trans){
		return(this.srcState.equals(trans.getSrcState())
				  && this.tarState.equals(trans.getTarState())
				    && this.transAction.equals(trans.getTransAction()));
	}
	*/
	public String toString() {
		
		return srcState.toString() + " -" + transAction.toString() + "-> " + tarState.toString();
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((srcState == null) ? 0 : srcState.hashCode());
		result = prime * result
				+ ((tarState == null) ? 0 : tarState.hashCode());
		result = prime * result
				+ ((transAction == null) ? 0 : transAction.hashCode());
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
		Transition other = (Transition) obj;
		if (srcState == null) {
			if (other.srcState != null)
				return false;
		} else if (!srcState.equals(other.srcState))
			return false;
		if (tarState == null) {
			if (other.tarState != null)
				return false;
		} else if (!tarState.equals(other.tarState))
			return false;
		if (transAction == null) {
			if (other.transAction != null)
				return false;
		} else if (!transAction.equals(other.transAction))
			return false;
		return true;
	}
	

}
