package com.pseuco.project;

import java.util.HashSet;
import java.util.Set;

public class LTS {
	
	private final State startState;
	
	private final HashSet<State> states;
	
	private final HashSet<Action> actions;
	
	private final HashSet<Transition> transitionRelation;
	
	private HashSet<Transition> weakTransitionRelation = new HashSet<Transition>();
	
	/*
	 * Konstruktor
	 */
	
	public LTS(State startState, Set<State> states, Set<Action> actions, Set<Transition> transitions){
		//Add startState and states
		this.startState = startState;
		this.states = new HashSet<State>();
		this.states.add(startState);
		this.states.addAll(states);
		
		//Add actions i possible in every LTS
		Action τ = new Action("τ");
		this.actions = new HashSet<Action>();
		this.actions.add(τ);
		this.actions.addAll(actions);
		
		//Add transitions
		this.transitionRelation = new HashSet<Transition>();
		this.transitionRelation.addAll(transitions);
		
	}

	/*
	 * Getters for final Fields
	 */
	public State getStartState() {
		return startState;
	}

	public HashSet<State> getStates() {
		return states;
	}

	public HashSet<Action> getActions() {
		return actions;
	}

	public HashSet<Transition> getTransitionRelation() {
		return transitionRelation;
	}

	
	public void addToWeakTrans(Transition trans){
		this.weakTransitionRelation.add(trans);
	}
	
	/*
	 * Method to compute the weak TransitionRelation for the given LTS
	 */
	
	public void generateWeakTransitionRelation(){
		//TODO Max deine Arbeit ;)
		
		for (State i :this.states) {
			i.accept(this, i, false, null);
		}
	}
	
	/*
	 * Returns all transitions that start at the given state
	 * */
	public HashSet<Transition> getOutgoingTransitions(State status) {
		HashSet<Transition> res = new HashSet<Transition>();
		for (Transition tran : this.transitionRelation)
		{
			if(tran.getSrcState().equals(status)) //Start state of the transitions equals the given state
					{
						res.add(tran);
					}
		}
		return res;
	}
	
	public HashSet<Transition> getWeakTransitionRelation(){
		if (this.weakTransitionRelation.isEmpty())
			generateWeakTransitionRelation();  //warum nicht im konstruktor aufrufen?
		return (HashSet<Transition>) this.weakTransitionRelation.clone();
	}
	

}
