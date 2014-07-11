package com.pseuco.project;

import java.util.HashSet;

public class MinimizationMonitor {
	
	/*
	 * Fields
	 */
	private static HashSet<Block> partition = new HashSet<Block>();
	
	private static HashSet<BlockTuple> toDoList = new HashSet<BlockTuple>();
	
	private static HashSet<Block> currentlyDoneList = new HashSet<Block>();
	
	private static HashSet<Transition> weakTransitionRelation;
	
	private static boolean workFinished = false;
	
	
	/*
	 * Constructor
	 */
	public MinimizationMonitor(HashSet<State> startPartition,HashSet<Transition> weakTransRel ){
		Block startP = new Block(startPartition);
		partition.add(startP);
		weakTransitionRelation = weakTransRel;
		toDoList.add(new BlockTuple(startP, startP));
	}
	
	public static HashSet<Transition> getWeakTransitionRelation() {
		return weakTransitionRelation;
	}
	
	
	/*
	 * Getter and Setter
	 */
	synchronized static public HashSet<Block> getPartition(){
		return partition;
	}
	
	synchronized static boolean getWorkFinished(){
		return workFinished;
	}
	
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
		for(BlockTuple b : toDoList){
			if(! ( currentlyDoneList.contains(b.getBlockOne())
					|| currentlyDoneList.contains(b.getBlockTwo())))
			res = b;		
		}
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
	synchronized static public void computeNewToDosAndEnd(BlockTuple newBlocks,Block splitted,Block toDelete){
		//Füge zuerst in HashSet ein und vereinige died mit toDoList
		//sonst iterator überschreiben
		HashSet<BlockTuple> toAdd = new HashSet<BlockTuple>();
		HashSet<BlockTuple> toDel = new HashSet<BlockTuple>();
		Block nOne = newBlocks.getBlockOne();
		Block nTwo = newBlocks.getBlockTwo();
		Block bOne;
		Block bTwo;
		boolean check1 = false,check2= false;
		//Berechne neue todos und markiere welche zu löschen sind
		for(BlockTuple b : toDoList){
			bOne = b.getBlockOne();
			bTwo = b.getBlockTwo();
			if(! bOne.equals(splitted)){
				toAdd.add(new BlockTuple(nOne,bOne));
				toAdd.add(new BlockTuple(nTwo,bOne));
				check1 = true;
			}
			if(!bTwo.equals(splitted)){
				toAdd.add(new BlockTuple(nOne,bTwo));
				toAdd.add(new BlockTuple(nTwo,bTwo));
				check2 = true;
			}
			if(check1 || check2)
				toDel.add(b);
			check1 = false; check2 = false;
		}
		//Füge Hinzu/lösche
		toDoList.addAll(toAdd);
		toDoList.removeAll(toDel);
		
		//Remove the Blocks from CurrentlyDone
		currentlyDoneList.remove(splitted);
		currentlyDoneList.remove(toDelete);
		
		//workFinished muss gesetzt werden
		if(toDoList.isEmpty() && currentlyDoneList.isEmpty())
			workFinished = true;
	}
}
