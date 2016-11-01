import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

public class nfa {

	public static void main(String[] args) {
		ArrayList<State> states = new ArrayList<State>();
		String test = "aaaa";
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(args[0]));
		} catch (Exception e) {
		System.out.println(args[0] + " is not a valid file.");
		System.exit(-1);
		}
		
		//first line of file is the number of states
		int numStates = Integer.parseInt(fileScanner.nextLine());
//		System.out.println(numStates);
		for(int i=0;i<numStates;i++){
			states.add(new State(i));
		}
		
		
		//second line is availiable input tokens
		StringTokenizer inputTokens = new StringTokenizer(fileScanner.nextLine());
		
//		while(inputTokens.hasMoreTokens()){
//			System.out.print(inputTokens.nextToken()+" ");
//		}
		
		char[] inputs = new char[inputTokens.countTokens()+1];
		for(int i =0;i<inputs.length-1;i++){
			inputs[i] = inputTokens.nextToken().charAt(0);
		}
		inputs[inputs.length-1] = ' ';//add in empty character because tokenizer removes it
		System.out.print("Sigma: ");
		for(int i=0;i<inputs.length;i++){
			System.out.print(inputs[i]+" ");
		}System.out.println("\n------");
		
		
		//Third line until the number of states has been met
		//will be the stateName: and then the transitions available given the input character
		
		for(int i =0;i<numStates;i++){
			StringTokenizer stateTokens = new StringTokenizer(fileScanner.nextLine());
			stateTokens.nextToken();//already have name from earlier
			
			while(stateTokens.hasMoreTokens()){
				//remaining tokens for this line will be the transitions states
				for(int j=0;j<inputs.length;j++){
					String temp = stateTokens.nextToken();//this will be a list of transitions 
//					if(j==inputs.length-1){
//						//need to include itself in the lamda transition
//						temp = temp.charAt(0)+""+i+","+temp.substring(1);
//					}
					StringTokenizer st = new StringTokenizer(temp);
					while(st.hasMoreTokens()){
						states.get(i).addEdge(inputs[j]+st.nextToken());	
					}
				}//end of checking available transitions for given inputs
			}
		}//finished collecting all states
		
		for(int i =0;i<states.size();i++){
			System.out.print(i+": ");
			for(int j=0;j<inputs.length;j++){
				System.out.print("("+inputs[j]+","+states.get(i).transitions.get(j).substring(1)+") " );
			}System.out.println();
		}
		
		
		//next line is the startingState
		State startingState = states.get(Integer.parseInt(fileScanner.nextLine()));
		//System.out.println(startingState.name);
		
		//the final line will be a list of accepting states 
		ArrayList<State> acceptingStates = new ArrayList<State>();
		StringTokenizer acceptST = new StringTokenizer(fileScanner.nextLine(),"{,}");
		while(acceptST.hasMoreTokens()){
			acceptingStates.add(states.get(Integer.parseInt(acceptST.nextToken())));
		}
		System.out.println("------");
		System.out.println("s: "+startingState.name);
		System.out.print("A: {");
		for(int i =0;i<acceptingStates.size();i++){
			System.out.print(acceptingStates.get(i).name);
			if(i==acceptingStates.size()-1){
				System.out.println("}");
			}else{
				System.out.print(",");
			}
		}
		
		
		
//		for(int i =0;i<acceptingStates.size();i++){
//			System.out.println(acceptingStates.get(i).name);
//		}
	
		
//		-------------------------------conversion to DFA -------------------------------
		
		System.out.print("\nTo DFA: {");
		ArrayList<State> temp = State.closure(startingState,states); 

		for(int i = 0;i<temp.size();i++){
			System.out.print(temp.get(i).name);
			if(i == temp.size()-1){
				System.out.print("} ");
			}else{
				System.out.print(",");
			}
		}
		
		//temp at this point will have the nfa states that will make up the starting dfa state
		ArrayList<State> dfaStates = new ArrayList<State>();
		State dfaStartingState = new State(0);
		dfaStates.add(dfaStartingState);
		
		
		
		
		//to complete the dfa starting state we need to do the following for each input symbol
		
			//apply move function to newly created state and the input symbol this will return a set of states
			//then we apply closure to this set of states, which may give another set of states
		
		//this set of nfa states will be a new dfa state
	
		
		
		ArrayList<State> testing = new ArrayList<State>();
		
		for(int i = 0;i<inputs.length;i++){//length-1 one because we don't worry about ' ' here
			//temp has nfa states
			String tempTransitions = "";
			
			tempTransitions += inputs[i]+"{";//adds transition character to the string
			
			for(int j = 0;j<temp.size();j++){
				//this loop will move through the nfa states for this state checking where they go for given character
				
				ArrayList<State>tempListOfTransitions = State.move(temp.get(j),states,inputs[i]);
				
				for(int k = 0;k<tempListOfTransitions.size();k++){
					//these are the states reachable with given char
					tempTransitions += tempListOfTransitions.get(k).name;
					testing.add(tempListOfTransitions.get(k));
					testing.add(temp.get(j));
				}
				if(i==inputs.length-1){
					//add current state to the list it can reach itself with lambda 
					tempTransitions += ","+j;
				}
				
				if(j==tempListOfTransitions.size()-1){
					tempTransitions += ",";
				}else{
					tempTransitions += "}";
				}
				
			}
			//set this transition for the state
//			System.out.println(tempTransitions);
			
			dfaStartingState.addEdge(tempTransitions);
			
		}
//		System.out.println("\n------");
//		for(int i =0;i<testing.size();i++){
//			System.out.println(testing.get(i));
//		}
		
		
				//need to add in the closure of the set of states in testing.
		Set<State> closureStates = new HashSet<State>();
		for(int i =0;i<testing.size();i++){
			String temp2 = " {";
			closureStates.addAll(State.closure(testing.get(i), states));

		}
		
//		System.out.println();
//		System.out.println(closureStates);
		//closureStates is now a set of nfa states that will make up the next dfa state
		
		//add set back to an arraylist
		testing.clear();
		testing.addAll(closureStates);
		closureStates.clear();
		
		for(int i=0;i<testing.size();i++){
			if(i==0)System.out.print("{");
			System.out.print(testing.get(i));
			if(i!=testing.size()-1){
				System.out.print(",");
			}else{
				System.out.print("}");
			}
		}
		
		
	}
}


class State {
	
	public int name;
	public boolean isFinal = false;
	public ArrayList<String> transitions = new ArrayList<String>();
	
	
	public State(int name){
		this.name = name;
	}
	
	public void addEdge(String transition){
//		System.out.println(name);
//		System.out.println(transition);
//		this is string that begins with the input letter followed by list of states it can go to
		transitions.add(transition);
	}
	
	//takes a state and returns the set of states reachable givein ' '; including itself
	public static ArrayList<State> closure(State state,ArrayList<State> possibleStates){
		ArrayList<State> closureStates = new ArrayList<State>();
		closureStates.add(state);
		if(state.transitions.get(state.transitions.size()-1) != "{}"){
			//there are states that it can go to
//			System.out.println(state.transitions.get(state.transitions.size()-1));
			StringTokenizer st = new StringTokenizer(state.transitions.get(state.transitions.size()-1),"{,}");
			st.nextToken();//need to remove the ' ' character 
			while(st.hasMoreTokens()){
				String temp = st.nextToken();
//				System.out.println("token: "+temp);
				closureStates.add(possibleStates.get(Integer.parseInt(temp)));
			}
			
		}
		return closureStates;
	}
	
	//takes state and character and returns the set of states that are reachable by one transition 
	//with that character
	public static ArrayList<State> move(State state, ArrayList<State> possibleStates, char c){
		ArrayList<State> possibleMoves = new ArrayList<State>();
		//need to get the transition string that is responsible for the given character
		int i = 0;
		for(;i<state.transitions.size();i++){
			if(state.transitions.get(i).charAt(0) == c){
				break;
			}
		}
		
		StringTokenizer st = new StringTokenizer(state.transitions.get(i),"{,}");
		st.nextToken();//remove the char from the start of the string
		while(st.hasMoreTokens()){
			String temp = st.nextToken();
			possibleMoves.add(possibleStates.get(Integer.parseInt(temp)));
		}
		
		return possibleMoves; 
	}
	
	
	public void transHelper(){
		System.out.println("--------transitions helper---------");
		for(int i =0;i<transitions.size();i++){
			System.out.println(transitions.get(i));
		}
	}
	
	public String toString(){
		String temp = ""+name;
		return temp;
	}
	
}