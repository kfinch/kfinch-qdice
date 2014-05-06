package back_end;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameState {

	private static final int DEFAULT_MIN_GOAL = 20;
	private static final int DEFAULT_MAX_GOAL = 70;
	private static final int DEFAULT_NUM_PIECES = 5;
	private static final int DEFAULT_MAX_PIECE = 6;
	
	private List<Integer> pieces;
	private List<Operation> ops;
	private int numPieces;
	private int numOps;
	private int goal;
	private boolean opsReusable;
	
	/**
	 * Generates a new game state from explicitly given variables
	 * @param pieces A list of integers which represent the game pieces
	 * @param ops A list of operations which represent the usable operations
	 * @param goal The goal integer
	 */
	public GameState(List<Integer> pieces, List<Operation> ops, int goal, boolean opsReusable){
		this.pieces = pieces;
		this.ops = ops;
		this.goal = goal;
		this.numPieces = pieces.size();
		this.numOps = ops.size();
		this.opsReusable = opsReusable;
	}
	
	/**
	 * Generates a new game state as an exact duplicate of another game state.
	 * @param toCopy the game state to be copied
	 */
	public GameState(GameState toCopy){
		this.pieces = new ArrayList<Integer>(toCopy.pieces);
		this.ops = new ArrayList<Operation>(toCopy.ops);
		this.goal = toCopy.goal;
		this.numPieces = toCopy.numPieces;
		this.numOps = toCopy.numOps;
		this.opsReusable = toCopy.opsReusable;
	}
	
	/**
	 * Randomly generates a puzzle given some parameters. All ops available and reusable.
	 * @param minGoal The lowest the goal can be generated (inclusive)
	 * @param maxGoal The highest the goal can be generated (exclusive)
	 * @param numPieces The number of pieces to be generated
	 * @param maxPiece Pieces generated will be between 1 and maxPiece (inclusive)
	 */
	public GameState(int minGoal, int maxGoal, int numPieces, int maxPiece){
		goal = (int) ((maxGoal-minGoal)*(Math.random()) + minGoal);
		this.numPieces = numPieces;
		pieces = new ArrayList<Integer>();
		for(int i=0; i<numPieces; i++)
			pieces.add((int)(Math.random()*maxPiece + 1));
		ops = defaultOps();
		numOps = ops.size();
		opsReusable = true;
	}
	
	/**
	 * Randomly generates a puzzle within default parameters
	 */
	public GameState(){
		this(DEFAULT_MIN_GOAL, DEFAULT_MAX_GOAL, DEFAULT_NUM_PIECES, DEFAULT_MAX_PIECE);
	}
	
	private List<Operation> defaultOps(){
		ArrayList<Operation> defaultOps = new ArrayList<Operation>(5);
		defaultOps.add(Operation.PLUS);
		defaultOps.add(Operation.MINUS);
		defaultOps.add(Operation.TIMES);
		defaultOps.add(Operation.DIVIDE);
		defaultOps.add(Operation.EXPONENT);
		return defaultOps;
	}
	
	public int getNumPieces(){
		return numPieces;
	}
	
	public int getNumOps(){
		return numOps;
	}
	
	public int getGoal(){
		return goal;
	}
	
	public List<Integer> getPieces(){
		return pieces;
	}
	
	public List<Operation> getOps(){
		return ops;
	}
	
	public int pieceAt(int index){
		return pieces.get(index);
	}
	
	public Operation opAt(int index){
		return ops.get(index);
	}
	
	/**
	 * Combines two pieces using the specified operation. New piece will always be at end of list.
	 * @param firstIndex The index of the first piece to be combined
	 * @param secondIndex The index of the second piece to be combined
	 * @param op The operation to combine with
	 * @return The value of the combined piece
	 */
	public int combine(int firstIndex, int secondIndex, int opIndex){
		int first = pieces.remove(firstIndex);
		if(secondIndex > firstIndex)
			secondIndex--;
		int second = pieces.remove(secondIndex);
		Operation op;
		if(opsReusable){
			op = ops.get(opIndex);
		}
		else{
			op = ops.remove(opIndex);
			numOps--;
		}
		
		int result = 0;
		switch(op){
		case PLUS : result = first + second; break;
		case MINUS : result = first - second; break;
		case TIMES : result = first * second; break;
		case DIVIDE : result = first / second; break;
		case EXPONENT : result = (int) Math.pow(first, second); break;
		case ROOT : result = (int) Math.pow(first, 1/(double)(second)); break; //TODO: Is that cast needed?
		}
		pieces.add(result);
		numPieces--;
		return result;
	}
	
	/**
	 * Combines two pieces using the specified operation. New piece will always be at end of list.
	 * @param firstIndex The index of the first piece to be combined
	 * @param secondIndex The index of the second piece to be combined
	 * @param op The operation to combine with
	 * @return A game state representing this game state after the specified combine operation.
	 */
	public GameState afterCombine(int firstIndex, int secondIndex, int opIndex){
		GameState result = new GameState(this);
		result.combine(firstIndex, secondIndex, opIndex);
		return result;
	}
}
