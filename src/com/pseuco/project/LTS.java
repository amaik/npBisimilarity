package com.pseuco.project;

import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class LTS {

	private final State startState;

	private final HashSet<State> states = new HashSet<State>();

	private final HashSet<Action> actions;

	private final HashSet<Transition> transitionRelation;

	private final HashSet<Transition> weakTransitionRelation = new HashSet<Transition>();

	/*
	 * Konstruktor
	 */

	public LTS(State startState, Set<State> states, Set<Action> actions,
			Set<Transition> transitions) {
		// Add startState and states
		this.startState = startState;
		this.states.add(startState);
		this.states.addAll(states);

		// Add actions i possible in every LTS
		Action τ = new Action("τ");
		this.actions = new HashSet<Action>();
		this.actions.add(τ);
		this.actions.addAll(actions);

		// Add transitions
		this.transitionRelation = new HashSet<Transition>();
		this.transitionRelation.addAll(transitions);

		generateWeakTransitionRelation(); // warum nicht im konstruktor
											// aufrufen? Kann man machen

	}

	public LTS(State newStart, HashSet<State> reachedStates,
			HashSet<Transition> newTransitions) {
		this.startState = newStart;
		this.states.add(newStart);
		this.states.addAll(reachedStates);

		// Add actions i possible in every LTS
		Action τ = new Action("τ");
		this.actions = new HashSet<Action>();
		this.actions.add(τ);
		this.actions.addAll(getActionsFromTransitions(newTransitions));

		// Add transitions
		this.transitionRelation = new HashSet<Transition>();
		this.transitionRelation.addAll(newTransitions);
		//no need to generate Weak Transitions
	}
	
	public HashSet<Action> getActionsFromTransitions(HashSet<Transition> Transitions) {
		HashSet<Action> res=new HashSet<Action>();
		for (Transition trans : Transitions) {
			res.add(trans.getTransAction());
		}
		return res;
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

	public void addToWeakTrans(Transition trans) {
		this.weakTransitionRelation.add(trans);
	}

	/*
	 * Method to compute the weak TransitionRelation for the given LTS
	 */

	public void generateWeakTransitionRelation() {
		// Füge Transitionen zu sich selbst in Transitionsrelation ein

		Action τ = new Action("τ");

		for (State i : this.states) {
			this.weakTransitionRelation.add(new Transition(i, i, τ));
			HashSet<State> alreadyVisited = new HashSet<State>();
			this.generateWeakTransitionForState(i, i, false, null,
					alreadyVisited);
		}

	}
	
	public State getStateWithName(String name) {
		for (State i : this.states) {
			if (i.getName().equals(name))
				return i;
		}
		throw new NullPointerException("There's no state with the given name");
	}

	/*
	 * Returns all transitions that start at the given state
	 */
	public HashSet<Transition> getOutgoingTransitions(State state) {
		HashSet<Transition> res = new HashSet<Transition>();
		for (Transition tran : this.transitionRelation) {
			if (tran.getSrcState().equals(state)) // Start state of the
													// transitions equals the
													// given state
			{
				res.add(tran);
			}
		}
		return res;
	}
	
	/*
	 * Returns all transitions that start at the given state
	 */
	public HashSet<Transition> getOutgoingWeakTransitions(State state) {
		HashSet<Transition> res = new HashSet<Transition>();
		for (Transition tran : this.weakTransitionRelation) {
			if (tran.getSrcState().equals(state)) // Start state of the
													// transitions equals the
													// given state
			{
				res.add(tran);
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public HashSet<Transition> getWeakTransitionRelation() {

		return (HashSet<Transition>) this.weakTransitionRelation.clone();
	}

	// Start is the state the Transitions are computed for
	// current is the current State in the recursive descent
	public void generateWeakTransitionForState(State start, State current,
			Boolean UsedStrong, Action StrongAction,
			HashSet<State> alreadyVisited) {

		if (start == null) {
			throw new NullPointerException("start == null");
		}

		alreadyVisited.add(current);

		for (Transition i : this.getOutgoingTransitions(current)) { // iteriere
																	// über
																	// ausgehende
																	// transitionen
			State transTarget = i.getTarState(); // ziel der aktuellen
													// transition

			/* Jeder zustand kann sich selbst aber schwach erreichen! */

			if (i.isIntern()) { // man kommt zu dem Folgezustand über eine
								// schwache Aktion (Transition? Es gibt keine
								// schwachen Aktionen)
				if (UsedStrong) { // falls schon starke genutzt

					Transition newTrans = new Transition(start, transTarget,
							StrongAction);
					this.addToWeakTrans(newTrans);
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited); // neues HashSet falls Backtracking
					if (!alreadyVisited.contains(transTarget)) { // gegen zykel
						this.generateWeakTransitionForState(start, transTarget,
								UsedStrong, StrongAction, newAlreadyVisited);
					}
				} else if (!UsedStrong) { // falls noch keien starke genutzt
					// erzeuge neue transition vom start zustand zum
					// folgenden
					Transition newTrans = new Transition(start, transTarget,
							i.getTransAction());
					this.addToWeakTrans(newTrans);
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited);
					if (!alreadyVisited.contains(transTarget)) {
						this.generateWeakTransitionForState(start, transTarget,
								UsedStrong, StrongAction, newAlreadyVisited);
					}
				}
			} else { // starke Transition
				if (!UsedStrong) { // nur falls noch keine starke genutzt
									// wurde
					// erzeuge neue transition vom start zustand zum
					// folgenden
					Transition newTrans = new Transition(start, transTarget,
							i.getTransAction());
					this.addToWeakTrans(newTrans);
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited);
					if (!alreadyVisited.contains(transTarget)) {
						this.generateWeakTransitionForState(start, transTarget,
								true, i.getTransAction(), newAlreadyVisited);
					}
				}

			}
		}

	}


	// Start is the state the Transitions are computed for
	// current is the current State in the recursive descent
	public void generateTheREALWeakTransitions(State start, State current,
			Boolean UsedStrong, Action StrongAction,
			HashSet<State> alreadyVisited, int ActionsUsed) {

		if (start == null) {
			throw new NullPointerException("start == null");
		}

		alreadyVisited.add(current);

		for (Transition i : this.getOutgoingTransitions(current)) { // iteriere
																	// über
																	// ausgehende
																	// transitionen
			State transTarget = i.getTarState(); // ziel der aktuellen
													// transition

			/* Jeder zustand kann sich selbst aber schwach erreichen! */

			if (i.isIntern()) { // man kommt zu dem Folgezustand über eine
								// schwache Aktion (Transition? Es gibt keine
								// schwachen Aktionen)
				if (UsedStrong) { // falls schon starke genutzt

					if (ActionsUsed > 0) {
					Transition newTrans = new Transition(start, transTarget,
							StrongAction);
					this.addToWeakTrans(newTrans);}
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited); // neues HashSet falls Backtracking
					if (!alreadyVisited.contains(transTarget)) { // gegen zykel
						this.generateTheREALWeakTransitions(start, transTarget,
								UsedStrong, StrongAction, newAlreadyVisited, ActionsUsed+1);
					}
				} else if (!UsedStrong) { // falls noch keien starke genutzt
					// erzeuge neue transition vom start zustand zum
					// folgenden
					if (ActionsUsed > 0) {
					Transition newTrans = new Transition(start, transTarget,
							i.getTransAction());
					this.addToWeakTrans(newTrans); }
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited);
					if (!alreadyVisited.contains(transTarget)) {
						this.generateTheREALWeakTransitions(start, transTarget,
								UsedStrong, StrongAction, newAlreadyVisited,ActionsUsed+1);
					}
				}
			} else { // starke Transition
				if (!UsedStrong) { // nur falls noch keine starke genutzt
									// wurde
					// erzeuge neue transition vom start zustand zum
					// folgenden
					if (ActionsUsed > 0) {
					Transition newTrans = new Transition(start, transTarget,
							i.getTransAction()); 
					this.addToWeakTrans(newTrans); }
					HashSet<State> newAlreadyVisited = new HashSet<State>(
							alreadyVisited);
					if (!alreadyVisited.contains(transTarget)) {
						this.generateTheREALWeakTransitions(start, transTarget,
								true, i.getTransAction(), newAlreadyVisited, ActionsUsed+1);
					}
				}

			}
		}

	}
	
	
	public JsonObject generateJSONLtsForm(){
		String result = new String();
		
		JsonObjectBuilder statesObjectBuilder = Json.createObjectBuilder();
		for(State s : this.states){
				JsonArrayBuilder transitions = Json.createArrayBuilder();
				for(Transition t : this.transitionRelation){
					if(t.getSrcState().equals(s))
					transitions.add(Json.createObjectBuilder().add("label",t.getTransAction().toString()).add("detailsLabel",false).add("target", t.getTarState().toString()).build());
				}
				statesObjectBuilder.add(s.toString(), Json.createObjectBuilder().add("transitions",transitions.build()).build());	
		}
		JsonObject statesObject = statesObjectBuilder.build();
		
		JsonObject ltsObject = Json.createObjectBuilder()
				.add("initialState", this.startState.toString())
				.add("states", statesObject)
				.build();
		return ltsObject;
	}
	
public void minimizeTransitions() {
	
	this.weakTransitionRelation.clear();
	for (State i : this.states) {

		HashSet<State> alreadyVisited = new HashSet<State>();
		this.generateTheREALWeakTransitions(i, i, false, null,alreadyVisited, 0);
				
	}
	
	HashSet<Transition> toDelete = new HashSet<Transition>();
		for (Transition trans : this.transitionRelation){
			for (Transition weak : this.getWeakTransitionRelation()){
				if(trans.equals(weak)) {
					toDelete.add(trans);
				}
			}
		}
		
		this.transitionRelation.removeAll(toDelete);
	}
	
	public String generateJSONLtsString(){
		return this.generateJSONLtsForm().toString();
	}
}
