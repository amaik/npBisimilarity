package com.pseuco.project;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public class MinimizationMonitor {
	
	/*
	 * Fields
	 */
	private static HashSet<Block> partition = new HashSet<Block>();
	
	private static HashSet<Block> toDoList = new HashSet<Block>();
	
	private static HashSet<Block> currentlyDoneList = new HashSet<Block>();
	
	private static HashSet<Transition> weakTransitionRelation;
	
	
	/*
	 * Constructor
	 */
	public MinimizationMonitor(HashSet<State> startPartition,HashSet<Transition> weakTransRel ){
		Block startP = new Block(startPartition);
		this.partition.add(startP);
		this.weakTransitionRelation = weakTransRel;
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
	
	/*
	 * Locked Methods
	 */
	
	synchronized static public BlockTuple getNextToDoAndStart(){
		// berechnet welches TODO als nächstes berechnet werden kann und speichert die Beobachteten Blöcke in 
		// der currentlyDoneLis
		return null;
	}
	
	synchronized static public void computeNewToDosAndEnd(){
		//TODO berechnet die neuen TODO-Blöcke, die beim berechnen notwendig geworden sind. Entfernt Außerdem die 
	}
}
