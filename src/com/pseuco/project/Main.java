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

		@SuppressWarnings("unused")
		String initialStateLabel = ltsObject.getString("initialState");

		//System.out.println("The initial state is \"" + initialStateLabel
			//	+ "\".");

		JsonObject statesObject = ltsObject.getJsonObject("states");

		for (String state : statesObject.keySet()) {

			JsonObject stateObject = statesObject.getJsonObject(state);

			JsonArray transitionsArray = stateObject
					.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				@SuppressWarnings("unused")
				String label = transition.getString("label");

				// detailsLabel contains additional information
				// e.g.: which actions were synced and created the tau
				// transition
				// This property is optional, so it might not exists.
				// You can ignore it for the project.
				// However, if you are adventurous, you can keep it
				// and add some helpful detailsLabels to the transitions in your
				// output
				@SuppressWarnings("unused")
				String detailsLabel = null;
				try {
					detailsLabel = transition.getString("detailsLabel");
				} catch (ClassCastException e) {
					// ignore - detailsLabel = null
				}

				@SuppressWarnings("unused")
				String target = transition.getString("target");

				/*System.out.println("Saw a transition from \""
						+ state
						+ "\" with \""
						+ label
						+ "\""
						+ (detailsLabel != null ? " [\"" + detailsLabel + "\"]"
								: "") + " to \"" + target + "\".");*/

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

		//System.out.println(ltsObject.toString());
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
			//System.out.print("Submitting file...");
			sharer.submitAndOpenLts(data);
			//System.out.println(" Done.");
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
			//String input = readStandardInput();
			String input = Utf8IO.readStdin();

			/*
			 * Parse the Input LTS and Generate the Class
			 */
			
			LTS lts = parseAndGenerateLTS(input);

			/*
			 * Minify LTS - has to be concurrent TODO
			 */
			
			LTS minified = minifyLTS(lts);
			/*
			 * Generate new LTS in LTS-JSON-Format
			 */
			String minJSON = minified.generateJSONLtsString();
			/*
			 * Print minified LTS
			 */
			
			System.out.print(minJSON);
			

		} 
		else if (args.length == 1 && args[0].equals("-b")) {
		
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
			//String minJSON = minified.generateJSONLtsString();
			/*
			 * Print minified LTS
			 */
			//System.out.println(minified.toString());
			openInBrowserDemo(minified.generateJSONLtsForm());
			
			while(true){
				System.out.println("..");
				 input = readStandardInput();
				 lts = parseAndGenerateLTS(input);
				openInBrowserDemo(lts.generateJSONLtsForm());

			}
			
		} else if (args.length == 1 && args[0].equals("-wk")) {
			// started with command line argument -wk


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

			//System.out.println("weak Transitions: " + weakTransitions.size());
			//for (String trans : transList) {
				//System.out.println(trans.toString());
			//}

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
			/*
			 * Unser LTS ist gewollt ungeschützt, da es einmal erstellt wird und die arbeitenden Threads erst danach alle lesend darauf zugreifen.
			 * laut http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html : 17.4.5. Happens-before Order:
			 * "A call to start() on a thread happens-before any actions in the started thread."
			 * Daher wird garantiert, dass unsere Run-Methoden die gewünschten Werte aus dem Arbeitsspeicher lesen.
			 * */
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
		HashSet<Block> partition = minifyPartition(lts);
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
			 i++;
		 } 
		
		 LtsBuilder builder= new LtsBuilder(StateToBlock, BlockToState, lts.getWeakTransitionRelation(), lts.getTransitionRelation());
		 
		 State oldStart = lts.getStartState();
		 State newStart = builder.getMatchingState(oldStart);
		 builder.getReachedStates().add(newStart);
		 builder.createTransitions(oldStart, true);
	
		 
 		 LTS newLts = new LTS(newStart, builder.getReachedStates(), builder.getNewTransitions());
 		 newLts.minimizeTransitions();
		return newLts;
	}
	
	
}
