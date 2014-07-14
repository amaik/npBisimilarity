package com.pseuco.project;

import java.util.HashSet;

public class MinimizationMonitor {
	
	/*
	 * Fields
	 */
	private  HashSet<Block> partition = new HashSet<Block>();
	
	private  HashSet<BlockTuple> toDoList = new HashSet<BlockTuple>();
	
	private  HashSet<Block> currentlyDoneList = new HashSet<Block>();
	
	private  HashSet<Transition> weakTransitionRelation;
	
	private  boolean workFinished = false;
	
	
	/*
	 * Constructor
	 */
	public MinimizationMonitor(HashSet<State> startPartition,HashSet<Transition> weakTransRel ){
		Block startP = new Block(startPartition);
		partition.add(startP);
		weakTransitionRelation = weakTransRel;
		toDoList.add(new BlockTuple(startP, startP));
	}
	
	public  HashSet<Transition> getWeakTransitionRelation() {
		return weakTransitionRelation;
	}
	
	
	/*
	 * Getter and Setter
	 */
	synchronized  public HashSet<Block> getPartition(){
		return partition;
	}
	
	synchronized  boolean getWorkFinished(){
		return workFinished;
	}
	/*
	 * Unlocked Methodds
	 */
	//returns the predecessors for all states in the given block b with action alpha
	public   HashSet<State> pre(Block b, Action alpha){
		HashSet<State> res = new HashSet<State>();
		for (State state : b.getStates()) {
			for (Transition trans : this.weakTransitionRelation)	{
				if (trans.getTarState().equals(state) && trans.getTransAction().equals(alpha))	{ //target state and action equal
					res.add(trans.getSrcState()); //=> add the start-state to res
				}
			}
		}
		
		return (HashSet<State>) res.clone();
	}
	
	//returns all actions for the given weakTransitionRelation
	public  HashSet<Action> giveActions(){
		HashSet<Action> acts = new HashSet<Action>();
		for(Transition t : this.weakTransitionRelation)
			acts.add(t.getTransAction());
		return acts;
	}
	/*
	 * Create Runnable
	 */
	public  Runnable runner = new Runnable() {
		public void run() {
			//Arbeite solange es arbeit gibt
			while(!workFinished){
				//Hohle die nächste toDos
				BlockTuple next = null;
				try {
					next = getNextToDoAndStart();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(next){
					Block one;
					Block two;
					if(next != null){
						one = next.getBlockOne();
						two = next.getBlockTwo();
					}
					else return;
					//Prüfe die Zerlegungseigenschaften in beide Richtungen
				
					HashSet<Action> acts = giveActions();
					HashSet<State> stateIntersection,stateComplement;
					boolean success = false;
					for(Action a : acts){
						//Richtung 1
					
						//Diese Reihenfolge ist so wichtig, da stateIntersection 
						//zu beginn = pre(one, a) und pre(one,a) gebraucht wird um 
						//stateComplement zu bilden
						stateIntersection = pre(two,a);
						stateComplement = (HashSet<State>) one.getStates().clone();		
						stateComplement.removeAll(stateIntersection);
						stateIntersection.retainAll(one.getStates());
						if(!(stateIntersection.isEmpty()) && !(stateComplement.isEmpty())){
							Block newBlockOne = new Block(stateIntersection);
							Block newBlockTwo = new Block(stateComplement);
							BlockTuple newBlocks = new BlockTuple(newBlockOne,newBlockTwo);
							computeNewToDosAndEnd(newBlocks, one, two);
							success = true;
							break;
						}
					
						//Richtung 2
					
						stateIntersection = pre(one,a);
						stateComplement = (HashSet<State>) two.getStates().clone();
						stateComplement.removeAll(stateIntersection);
						stateIntersection.retainAll(two.getStates());
						if(!(stateIntersection.isEmpty()) && !(stateComplement.isEmpty())){
							Block newBlockOne = new Block(stateIntersection);
							Block newBlockTwo = new Block(stateComplement);
							BlockTuple newBlocks = new BlockTuple(newBlockOne,newBlockTwo);
							computeNewToDosAndEnd(newBlocks, two, one);
							success = true;
							break;
						}
					}
					if(!success)
						takeOutOfCurrentlyDoneList(one, two);
				}
			}
		}
	};
	
	
	
	/*
	 * Locked Methods
	 */
	
	
	/*
	 *Computes the todo that another thread can compute the partition for, returns null when no Todo is 
	 *available. It also saves the computed Blocks inside.
	 *Returns Null if no Block is available 
	 */
	synchronized public BlockTuple getNextToDoAndStart() throws InterruptedException{

		BlockTuple res = null;
		if(toDoList.isEmpty())
			wait();
		if(workFinished)
			//Schalte Thread aus, arbeit ist vorbei;
			Thread.currentThread().interrupt();
		//
		for(BlockTuple b : toDoList){
			if(! ( currentlyDoneList.contains(b.getBlockOne())
					|| currentlyDoneList.contains(b.getBlockTwo()))){
				res = b;
				break;
			}
					
		}
		toDoList.remove(res);
		currentlyDoneList.add(res.getBlockOne());
		currentlyDoneList.add(res.getBlockTwo());
		return res;
	}
	
	/*
	 * Computes the new blocks that are todo. It erases the blocks out of currentlyDoneList, that 
	 * are not needed anymore.
	 * newBlocks contains the new Blocks.
	 * toDelete is the Block that has to be erased from currentlyDoneList
	 * splitted is the Block that was splitted apart
	 */
	synchronized  public void computeNewToDosAndEnd(BlockTuple newBlocks,Block splitted,Block toDelete){
		//Füge zuerst in HashSet ein und vereinige died mit toDoList
		//sonst iterator überschreiben
		HashSet<BlockTuple> toAdd = new HashSet<BlockTuple>();
		HashSet<BlockTuple> toDel = new HashSet<BlockTuple>();
		Block nOne = newBlocks.getBlockOne();
		Block nTwo = newBlocks.getBlockTwo();
		//Füge zu toAdd die neun BlockTuple hinzu, diemit toDelete entstehe, da toDelete eventuell nicht 
		//mehr in der toDoList ist
		toAdd.add(new BlockTuple(nOne,toDelete));
		toAdd.add(new BlockTuple(nTwo,toDelete));
		Block bOne;
		Block bTwo;
		boolean check1 = false,check2= false;
		//Berechne neue todos und markiere welche zu löschen sind
		for(BlockTuple b : toDoList){
			bOne = b.getBlockOne();
			bTwo = b.getBlockTwo();
			if(bOne.equals(splitted)){
				toAdd.add(new BlockTuple(nOne,bOne));
				toAdd.add(new BlockTuple(nTwo,bOne));
				check1 = true;
			}
			if(bTwo.equals(splitted)){
				toAdd.add(new BlockTuple(nOne,bTwo));
				toAdd.add(new BlockTuple(nTwo,bTwo));
				check2 = true;
			}
			if(check1 || check2)
				toDel.add(b);
			check1 = false; check2 = false;
		}
		//Es kann sein dass verschiedene Blöcke nicht in der ToDo List stehen aber 
		// trotzdem neue toDos damit berechnet werden müssen
		for(Block b : currentlyDoneList){
			toAdd.add(new BlockTuple(nOne,b));
			toAdd.add(new BlockTuple(nTwo,b));
		}
		//Füge Hinzu/lösche
		toDoList.addAll(toAdd);
		toDoList.removeAll(toDel);
		
		//Remove the Blocks from CurrentlyDone
		currentlyDoneList.remove(splitted);
		currentlyDoneList.remove(toDelete);
		
		//Füge die neuen Blöcke in die Partition ein
		partition.remove(splitted);
		partition.add(nOne);
		partition.add(nTwo);
		
		//workFinished muss gesetzt werden
		if(toDoList.isEmpty() && currentlyDoneList.isEmpty())
			workFinished = true;
		
		//Sage bescheid dass es neue ToDoS gibt
		notifyAll();
	}
	/*
	 * This method is used when no block could be splitted
	 */
	synchronized public void takeOutOfCurrentlyDoneList(Block one, Block two){
		currentlyDoneList.remove(one);
		currentlyDoneList.remove(two);
	}
}
