package com.pseuco.project;

import java.util.HashSet;

import com.pseuco.project.Transition;

public class State {

	private final String name;

	public State(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/*
	 * // public void accept (LTS lts, HashSet<State> alreadyVisited) {
	 * alreadyVisited.add(this); for (Transition trans :
	 * lts.getOutgoingTransitions(this)) //all outgoing relations (strong==weak
	 * here) { if (!alreadyVisited.contains(trans.getTarState())) //if the
	 * target of the transition hasnt been visited {
	 * trans.getTarState().accept(lts, alreadyVisited); //visit the target
	 * 
	 * } }
	 * 
	 * for (Transition trans : lts.getOutgoingWeakTransitions(this)) { if
	 * (trans.getTransAction().getName()=="i") { State tar =
	 * trans.getTarState(); HashSet<Transition> outgoingFromTar
	 * =lts.getOutgoingWeakTransitions(tar); for (Transition tran :
	 * outgoingFromTar) { Transition newtran= new Transition(this,
	 * tran.getTarState(), tran.getTransAction()); lts.addToWeakTrans(newtran);
	 * } } } }
	 */

	public void accept(LTS lts, State start, Boolean UsedStrong,
			Action StrongAction) {

		if (lts == null) {
			throw new NullPointerException("lts == null");
		}
		if (start == null) {
			throw new NullPointerException("start == null");
		}

		for (Transition i : lts.getOutgoingTransitions(this)) { //iteriere über ausgehende transitionen
			State transTarget = i.getTarState(); //ziel der aktuellen transition
			if (transTarget != this) { //keine transitionen zu mir selbst 

				if (i.isWeak()) { // man kommt zu dem Folgezustand über eine
									// schwache Aktion
					if (UsedStrong) { // falls schon starke genutzt

						Transition newTrans = new Transition(start,
								transTarget, StrongAction);
						lts.addToWeakTrans(newTrans);
						transTarget
								.accept(lts, start, UsedStrong, StrongAction);
					} else if (!UsedStrong) { // falls noch keien starke genutzt
						// erzeuge neue transition vom start zustand zum
						// folgenden
						Transition newTrans = new Transition(start,
								transTarget, i.getTransAction());
						lts.addToWeakTrans(newTrans);
						transTarget
								.accept(lts, start, UsedStrong, StrongAction);
					}
				} else { // starke Transition
					if (!UsedStrong) { // nur falls noch keine starke genutzt
										// wurde
						// erzeuge neue transition vom start zustand zum
						// folgenden
						Transition newTrans = new Transition(start,
								transTarget, i.getTransAction());
						lts.addToWeakTrans(newTrans);
						transTarget
								.accept(lts, start, true, i.getTransAction());
					}
				}
			}
		}

	}
}
