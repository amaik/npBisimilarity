package com.pseuco.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.pseuco.project.share.PseuCoShare;

public class Main {

	/**
	 * Demonstrates how to parse an LTS string. Writes information about the LTS
	 * to standard output.
	 */
	public static void ltsParsingDemo() {
		// we'll parse this string:
		// {"initialState":"a.b.(0 | 0) + (c?.0 | c!.0) \\ {c}","states":{"a.b.(0 | 0) + (c?.0 | c!.0) \\ {c}":{"transitions":[{"label":"a","detailsLabel":false,"target":"b.(0 | 0) \\ {c}"},{"label":"τ","detailsLabel":"c","target":"0 | 0 \\ {c}"}]},"b.(0 | 0) \\ {c}":{"transitions":[{"label":"b","detailsLabel":false,"target":"0 | 0 \\ {c}"}]},"0 | 0 \\ {c}":{"transitions":[]}}}
		// note that " and \ have to be escaped with \ below so they represent
		// this string in Java
		// see the source CCS term online:
		// http://pseuco.com/#/edit/remote/dzsk0qbxnbcm2mcumw5i
		String encodedLts = "{\"initialState\":\"a.b.(0 | 0) + (c?.0 | c!.0) \\\\ {c}\",\"states\":{\"a.b.(0 | 0) + (c?.0 | c!.0) \\\\ {c}\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"b.(0 | 0) \\\\ {c}\"},{\"label\":\"τ\",\"detailsLabel\":\"c\",\"target\":\"0 | 0 \\\\ {c}\"}]},\"b.(0 | 0) \\\\ {c}\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0 | 0 \\\\ {c}\"}]},\"0 | 0 \\\\ {c}\":{\"transitions\":[]}}}";

		JsonObject ltsObject = Json.createReader(new StringReader(encodedLts))
				.readObject();

		String initialStateLabel = ltsObject.getString("initialState");

		System.out.println("The initial state is \"" + initialStateLabel
				+ "\".");

		JsonObject statesObject = ltsObject.getJsonObject("states");

		for (String state : statesObject.keySet()) {

			JsonObject stateObject = statesObject.getJsonObject(state);

			JsonArray transitionsArray = stateObject
					.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				String label = transition.getString("label");

				// detailsLabel contains additional information
				// e.g.: which actions were synced and created the tau
				// transition
				// This property is optional, so it might not exists.
				// You can ignore it for the project.
				// However, if you are adventurous, you can keep it
				// and add some helpful detailsLabels to the transitions in your
				// output
				String detailsLabel = null;
				try {
					detailsLabel = transition.getString("detailsLabel");
				} catch (ClassCastException e) {
					// ignore - detailsLabel = null
				}

				String target = transition.getString("target");

				System.out.println("Saw a transition from \""
						+ state
						+ "\" with \""
						+ label
						+ "\""
						+ (detailsLabel != null ? " [\"" + detailsLabel + "\"]"
								: "") + " to \"" + target + "\".");

			}

		}
	}

	/**
	 * Demonstrates how to serialize a LTS. Builds a JSON representation of a
	 * hardcoded transition system.
	 * 
	 * @return the JSON representation
	 */
	public static JsonObject ltsSerializationDemo() {
		// we'll try to construct this LTS:
		//
		// -------τ-------\
		// / v
		// 1 --a--> 2 --τ--> 3

		JsonObject statesObject = Json
				.createObjectBuilder()
				.add("1",
						Json.createObjectBuilder()
								.add("transitions",
										Json.createArrayBuilder()
												.add(Json
														.createObjectBuilder()
														.add("label", "a")
														.add("detailsLabel",
																false)
														.add("target", "2")
														.build())
												.add(Json
														.createObjectBuilder()
														.add("label", "τ")
														.add("detailsLabel",
																false)
														.add("target", "3")
														.build()).build())
								.build())
				.add("2",
						Json.createObjectBuilder()
								.add("transitions",
										Json.createArrayBuilder()
												.add(Json
														.createObjectBuilder()
														.add("label", "τ")
														.add("detailsLabel",
																false)
														.add("target", "3")
														.build()).build())
								.build())
				.add("3",
						Json.createObjectBuilder()
								.add("transitions", Json.createArrayBuilder()
								// no transitions
										.build()).build()).build();

		JsonObject ltsObject = Json.createObjectBuilder()
				.add("initialState", "1").add("states", statesObject).build();

		System.out.println(ltsObject.toString());
		return ltsObject;
	}

	/**
	 * Demonstrates how to open a LTS in pseuCo.com
	 * 
	 * @param data
	 *            The LTS as a JSON object.
	 */
	public static void openInBrowserDemo(JsonObject data) {
		PseuCoShare sharer = new PseuCoShare();

		try {
			System.out.print("Submitting file...");
			sharer.submitAndOpenLts(data);
			System.out.println(" Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses a LTS Json String and generates a new LTS Class
	 */
	public static LTS parseAndGenerateLTS(String jsonLTS) {
		// LTS Constructor needs a Start state, and a set for each
		// actions,transitions and States
		HashSet<State> states = new HashSet<State>();
		HashSet<Action> actions = new HashSet<Action>();
		HashSet<Transition> transitions = new HashSet<Transition>();
		State startState;

		// Create JSON Object
		JsonObject ltsObject = Json.createReader(new StringReader(jsonLTS))
				.readObject();

		// Get the Start state and Create Object, Put start state in the states
		// Set
		String initialStateLabel = ltsObject.getString("initialState");
		startState = new State(initialStateLabel);
		states.add(startState);

		/*
		 * System.err.println("The initial state is \"" + initialStateLabel +
		 * "\".");
		 */

		// Create new Object of JSON LTS States
		JsonObject statesObject = ltsObject.getJsonObject("states");

		// Iterate through states and create new States
		for (String state : statesObject.keySet()) {

			// Create new State from String and add to Set
			State newState = new State(state);
			states.add(newState);

			JsonObject stateObject = statesObject.getJsonObject(state);

			JsonArray transitionsArray = stateObject
					.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				String actionLabel = transition.getString("label");
				// Create new Action and add to action Set
				Action newAction = new Action(actionLabel);
				actions.add(newAction);

				// Create target State and add to states
				String target = transition.getString("target");
				State tarState = new State(target);
				states.add(tarState);

				// Create new Transition from newState to
				Transition newTrans = new Transition(newState, tarState,
						newAction);
				transitions.add(newTrans);
			}

		}

		return new LTS(startState, states, actions, transitions);
	}

	/**
	 * Reads the standard input.
	 * 
	 * @return The text entered on standard input, without newlines.
	 * @throws IOException
	 */
	public static String readStandardInput() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		StringBuilder builder = new StringBuilder();

		String s;
		while ((s = in.readLine()) != null && s.length() != 0) { // read until
																	// end or
																	// empty
																	// line
			// reading this way strips empty lines, but they are not needed
			// anyway
			builder.append(s);
		}

		return builder.toString();
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		if (args.length == 1 && args[0].equals("-i")) {
			// started with command line argument -i
			// read the input
			String input = readStandardInput();

			/*
			 * Parse the Input LTS and Generate the Class
			 */
			LTS lts = parseAndGenerateLTS(input);

			/*
			 * Minify LTS - has to be concurrent TODO
			 */
			
			LTS minified = minifyLTS(lts);
			/*
			 * Genrate new LTS in LTS-JSON-Format
			 */

			String minJSON = minified.genereateJSONLtsForm();
			System.out.print(minJSON);

		} else if (args.length == 1 && args[0].equals("-wk")) {
			// started with command line argument -i

			// read the input
			String input = readStandardInput();

			/*
			 * Parse the Input LTS and Generate the Class
			 */
			LTS lts = parseAndGenerateLTS(input);
			/*
			 * Compute and get weakTransitions
			 */
			HashSet<Transition> weakTransitions = lts
					.getWeakTransitionRelation();
			LinkedList<String> transList = new LinkedList<String>();
			for (Transition trans : weakTransitions) {
				transList.add(trans.toString());
			}
			Collections.sort(transList);

			System.out.println("weak Transitions: " + weakTransitions.size());
			for (String trans : transList) {
				System.out.println(trans.toString());
			}

		} else {
			// other command line arguments
			System.err
					.println("No valid Arguments submitted, sorry! Shutting Down");
		}
	}

	// creates a minimised Partition, based on the given lts
	public static HashSet<Block> minifyPartition(LTS lts)
			throws InterruptedException {

		MinimizationMonitor moni = new MinimizationMonitor(lts.getStates(),
				lts.getWeakTransitionRelation());
		int avProc = Runtime.getRuntime().availableProcessors();
		//int avProc = 1;
		Thread[] threads = new Thread[avProc + 1];
		for (int i = 0; i <= avProc; i++) {
			Thread freddi = new Thread(moni.runner);
			freddi.start();
			threads[i] = freddi;
		}

		for (int i = 0; i <= avProc; i++) {
			Thread freddi = threads[i];
			freddi.join();
		}

		return moni.getPartition();

	}

	// creates a new beobachtungskongruentes lts to the given lts
	public static LTS minifyLTS(LTS lts) throws InterruptedException {
		System.out.println("alex hat scheisse gebaut");
		HashSet<Block> partition = minifyPartition(lts);
		System.out.println("Final Partition");
		for (Block b : partition) 
			System.out.println(b.toString());
		System.out.println("max hat scheisse gebaut	");
		HashMap<State,Block> StateToBlock = new HashMap<State,Block>(); //Maps a state in the old lts to its containing block
		HashMap<Block, State> BlockToState = new HashMap<Block,State>(); //maps a block to a state in the new lts
		HashSet<State> newStates = new HashSet<State>();
		int i=0;
		 for (Block block : partition) { 
			 for (State stat : block.getStates()) {
				 StateToBlock.put(stat, block);
			 }
			 State newState= new State(Integer.toString(i));
			 newStates.add(newState);
			 BlockToState.put(block, newState);	 
		 } 
		 HashSet<State> reachedStates = new HashSet<State>(); 
		 HashSet<Transition> newTransitions= new HashSet<Transition>();
 
		 State oldStart = lts.getStartState();
		 State newStart = getMatchingState(oldStart, StateToBlock, BlockToState);
		 reachedStates.add(newStart);
		 
		 createTransitions(oldStart, reachedStates, newTransitions, lts.getWeakTransitionRelation(), StateToBlock, BlockToState, true);
		 
		 for (Block block : partition) { //start createtransitions once for a state out of every block
			 State state=null;
			 for (State stateGetter : block.getStates()) {//alter wie hässlich fuck u java
				 state=stateGetter; //hoffen ma ma es gibt keene leere states
				 break;	 
			 }
			 createTransitions(state, reachedStates, newTransitions, lts.getWeakTransitionRelation(), StateToBlock, BlockToState, false);
		 } 
		 
 		 LTS newLts = new LTS(newStart, reachedStates, newTransitions);
		return newLts;
	}
	
	
	//returns all outgoing transitions from the given state 
	public static HashSet<Transition> getOutgoingTransition(State state, HashSet<Transition> weakTrans) {
			HashSet<Transition> res = new HashSet<Transition>();
			for (Transition trans: weakTrans) {
				if(trans.getSrcState().equals(state))
					res.add(trans);
			}
			return res;
			
	}
	
	//returns the matching state in the new lts , based on the given state in the old lts
	public static State getMatchingState(State oldState ,HashMap<State,Block> StateToBlock, HashMap<Block, State> BlockToState) {
		Block containingBlock = StateToBlock.get(oldState);
		return BlockToState.get(containingBlock);
	}
	
		
	
	//creates outgoing transitions from the given state into the newTransitions hashSet
	public static void createTransitions(State currrentState, HashSet<State> reachedStates, HashSet<Transition> newTransitions, 
			HashSet<Transition> weakTransitions, HashMap<State,Block> StateToBlock, HashMap<Block, State> BlockToState, Boolean startState) {
			
			HashSet<Transition> outgoing = getOutgoingTransition(currrentState, weakTransitions);
			State newSrcState = getMatchingState(currrentState, StateToBlock, BlockToState);
			Block newSrcBlock = StateToBlock.get(currrentState);
			for (Transition trans : outgoing) {
				Block newTarBlock = StateToBlock.get(trans.getTarState());
				if (newSrcBlock.equals(newTarBlock)) { //falls Start und Zielzustand im gleichen Block leigen
					if (trans.isIntern())  { //is the transition uses and internal action 
						if (startState) { //only draw an internal action from block->equal block if it is outgoing from the first state
							State newTarState = getMatchingState(trans.getTarState(), StateToBlock, BlockToState); //tarState in new lts
							Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
							newTransitions.add(newTrans);
							//reachedStates.add(newTarState); first state is always reachable		
						}
					}
					else { //always create external transitions
						State newTarState = getMatchingState(trans.getTarState(), StateToBlock, BlockToState); //tarState in new lts
						Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
						newTransitions.add(newTrans);
						reachedStates.add(newTarState);
					}
				}
				else { //falls verschiedene Blöcke muss ich Aktion auf jeden Fall bauen 
					State newTarState = getMatchingState(trans.getTarState(), StateToBlock, BlockToState); //tarState in new lts
					Transition newTrans = new Transition(newSrcState,newTarState, trans.getTransAction());
					newTransitions.add(newTrans);
					reachedStates.add(newTarState);					
				}
			}
	}

	
}
