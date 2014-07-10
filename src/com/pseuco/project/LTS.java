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
		//Füge Transitionen zu sich selbst in Transitionsrelation ein
		
		Action τ = new Action("τ");
		
		for (State i :this.states) {
			this.weakTransitionRelation.add(new Transition(i,i,τ));
			HashSet<State> alreadyVisited= new  HashSet<State>();
			this.generateWeakTransitionForState(i,i, false, null, alreadyVisited);
		}
	
	}
	
	
	/*
	 * Returns all transitions that start at the given state
	 * */
	public HashSet<Transition> getOutgoingTransitions(State state) {
		HashSet<Transition> res = new HashSet<Transition>();
		for (Transition tran : this.transitionRelation)
		{
			if(tran.getSrcState().equals(state)) //Start state of the transitions equals the given state
					{
						res.add(tran);
					}
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<Transition> getWeakTransitionRelation(){
		if (this.weakTransitionRelation.isEmpty())
			generateWeakTransitionRelation();  //warum nicht im konstruktor aufrufen? Kann man machen
		return (HashSet<Transition>) this.weakTransitionRelation.clone();
	}
	
	//Start is the state the Transitions are computed for
	//current is the current State in the recursive descent
	public void generateWeakTransitionForState(State start, State current, Boolean UsedStrong,
			Action StrongAction, HashSet<State> alreadyVisited) {

		if (start == null) {
			throw new NullPointerException("start == null");
		}
		
		alreadyVisited.add(current);

		for (Transition i : this.getOutgoingTransitions(current)) { //iteriere über ausgehende transitionen
			State transTarget = i.getTarState(); //ziel der aktuellen transition
			if (!alreadyVisited.contains(transTarget)) { //keine transitionen zu schon besuchten States
				
										/*Jeder zustand kann sich selbst aber schwach erreichen!*/
				
				if (i.isIntern()) { // man kommt zu dem Folgezustand über eine
									// schwache  Aktion (Transition? Es gibt keine schwachen Aktionen)
					if (UsedStrong) { // falls schon starke genutzt

						Transition newTrans = new Transition(start,
								transTarget, StrongAction);
						this.addToWeakTrans(newTrans);
						HashSet <State> newAlreadyVisited= new HashSet<State>(alreadyVisited); //neues HashSet falls Backtracking
						this.generateWeakTransitionForState( start,transTarget, UsedStrong, StrongAction, newAlreadyVisited);
					} else if (!UsedStrong) { // falls noch keien starke genutzt
						// erzeuge neue transition vom start zustand zum
						// folgenden
						Transition newTrans = new Transition(start,
								transTarget, i.getTransAction());
						this.addToWeakTrans(newTrans);
						HashSet <State> newAlreadyVisited= new HashSet<State>(alreadyVisited);
						this.generateWeakTransitionForState(start, transTarget, UsedStrong, StrongAction, newAlreadyVisited);
					}
				} else { // starke Transition
					if (!UsedStrong) { // nur falls noch keine starke genutzt
										// wurde
						// erzeuge neue transition vom start zustand zum
						// folgenden
						Transition newTrans = new Transition(start,
								transTarget, i.getTransAction());
						this.addToWeakTrans(newTrans);
						HashSet <State> newAlreadyVisited= new HashSet<State>(alreadyVisited);
						this.generateWeakTransitionForState(start,transTarget, true, i.getTransAction(), newAlreadyVisited);
					}
				}
			}
		}

	}

}
