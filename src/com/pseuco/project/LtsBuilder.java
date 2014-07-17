package com.pseuco.project;

import java.util.HashMap;
import java.util.HashSet;

public class LtsBuilder {
	
	private final HashSet<State> reachedStates; 
	private final HashSet<Transition> newTransitions; 
	private final HashSet<Transition> weakTransitions; 
	private final HashMap<State,Block> StateToBlock; 
	private final HashMap<Block, State> BlockToState;
	private final HashSet<Block> VisitedBlocks;
	
	
	public LtsBuilder(HashMap<State,Block> StateToBlock,	HashMap<Block, State> BlockToState, HashSet<Transition> weakTransitions) {
		this.reachedStates = new HashSet<State>(); 
		this.newTransitions= new HashSet<Transition>();
		this.weakTransitions = weakTransitions;
		this.StateToBlock = StateToBlock;
		this.BlockToState = BlockToState;
		this.VisitedBlocks = new HashSet<Block>();
	}
	
	//returns all outgoing weak transitions from the given state in the old lts 
		public  HashSet<Transition> getOutgoingTransition(State state) {
				HashSet<Transition> res = new HashSet<Transition>();
				for (Transition trans: this.weakTransitions) {
					if(trans.getSrcState().equals(state))
						res.add(trans);
				}
				return res;
				
		}
		
		//returns the matching state in the new lts , based on the given state in the old lts
		public  State getMatchingState(State oldState) {
			Block containingBlock = StateToBlock.get(oldState);
			return BlockToState.get(containingBlock);
		}
	
	//goes recursive thorugh the old lts and builds the transitions in the new lts
	public void createTransitions(State currrentState, Boolean startState) {
			
			HashSet<Transition> outgoing = getOutgoingTransition(currrentState);
			State newSrcState = getMatchingState(currrentState);
			Block newSrcBlock = StateToBlock.get(currrentState);
			this.VisitedBlocks.add(newSrcBlock);
			for (Transition trans : outgoing) {
				Block newTarBlock = StateToBlock.get(trans.getTarState());
				if (newSrcBlock.equals(newTarBlock)) { //falls Start und Zielzustand im gleichen Block leigen
					if (trans.isIntern())  { //is the transition uses and internal action 
						if (startState) { //only draw an internal action from block->equal block if it is outgoing from the first state
							State newTarState = getMatchingState(trans.getTarState()); //tarState in new lts
							Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
							newTransitions.add(newTrans);
							//reachedStates.add(newTarState); first state is always reachable		
						}
					}
					else { //always create external transitions
						State newTarState = getMatchingState(trans.getTarState()); //tarState in new lts
						Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
						newTransitions.add(newTrans);
						//reachedStates.add(newTarState); muss glaub ich nit hin
					}
				}
				else { //falls verschiedene Blöcke muss ich Aktion auf jeden Fall bauen 
					State newTarState = getMatchingState(trans.getTarState()); //tarState in new lts
					Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
					newTransitions.add(newTrans);
					reachedStates.add(newTarState);
					if (!this.VisitedBlocks.contains(newTarBlock)) //tar oder source block? ich glaub jo tar
						this.createTransitions(trans.getTarState(), false);
				}
			}

	}

	public HashSet<State> getReachedStates() {
		return this.reachedStates;
	}
	
	public HashSet<Transition> getNewTransitions() {
		return this.newTransitions;
	}
	
	
}
