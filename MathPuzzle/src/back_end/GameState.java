package back_end;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameState {

	private static final int DEFAULT_MIN_GOAL = 20;
	private static final int DEFAULT_MAX_GOAL = 70;
	private static final int DEFAULT_NUM_PIECES = 5;
	private static final int DEFAULT_MAX_PIECE = 6;
	
	public static final int MAX_PIECES = 10;
	public static final int MAX_OPS = 10;
	
	public static final int MAX_PIECE_SIZE = 999;
	public static final int MIN_PIECE_SIZE = -999;
	
	private int pieces[];
	private Operation ops[];
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
	public GameState(int pieces[], Operation ops[], int numPieces, int numOps, int goal, boolean opsReusable){
		this.pieces = pieces;
		this.ops = ops;
		this.goal = goal;
		this.numPieces = numPieces;
		this.numOps = numOps;
		this.opsReusable = opsReusable;
	}
	
	/**
	 * Generates a new game state as an exact duplicate of another game state.
	 * @param toCopy the game state to be copied
	 */
	public GameState(GameState toCopy){
		this.pieces = Arrays.copyOf(toCopy.pieces, toCopy.numPieces);
		this.ops = Arrays.copyOf(toCopy.ops, toCopy.numOps);
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
		pieces = new int[numPieces];
		for(int i=0; i<numPieces; i++)
			pieces[i] = ((int)(Math.random()*maxPiece + 1));
		ops = defaultOps();
		numOps = ops.length;
		opsReusable = true;
	}
	
	/**
	 * Randomly generates a puzzle within default parameters
	 */
	public GameState(){
		this(DEFAULT_MIN_GOAL, DEFAULT_MAX_GOAL, DEFAULT_NUM_PIECES, DEFAULT_MAX_PIECE);
	}
	
	private Operation[] defaultOps(){
		Operation defaultOps[] = new Operation[5];
		defaultOps[0] = Operation.PLUS;
		defaultOps[1] = Operation.MINUS;
		defaultOps[2] = Operation.TIMES;
		defaultOps[3] = Operation.DIVIDE;
		defaultOps[4] = Operation.EXPONENT;
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
	
	public int[] getPieces(){
		return pieces;
	}
	
	public Operation[] getOps(){
		return ops;
	}
	
	public int pieceAt(int index){
		return pieces[index];
	}
	
	public Operation opAt(int index){
		return ops[index];
	}
	
	/**
	 * Combines two pieces using the specified operation. New piece will always take the place of the second operand.
	 * @param firstIndex The index of the first piece to be combined
	 * @param secondIndex The index of the second piece to be combined
	 * @param op The operation to combine with
	 * @return The value of the combined piece
	 * @throws CombineException 
	 */
	public int combine(int firstIndex, int secondIndex, int opIndex) throws CombineException{
		int first = pieces[firstIndex];
		int second = pieces[secondIndex];
		Operation op = ops[opIndex];
		
		int result = 0;
		switch(op){
		case PLUS:
			result = first + second;
			break;
		case MINUS:
			result = first - second;
			break;
		case TIMES:
			result = first * second;
			break;
		case DIVIDE:
			if(second == 0)
				throw new CombineException("DIV BY ZERO");
			if(first % second != 0)
				throw new CombineException("FRACTION");
			result = first / second;
			break;
		case EXPONENT:
			//TODO: this has a chance to overflow in an epic manner before hitting the bounds check. Add handling.
			result = (int) Math.pow(first, second);
			break;
		case ROOT:
			//TODO: add fraction detection
			result = (int) Math.pow(first, 1/(double)(second));
			break;
		}
		
		if(result > MAX_PIECE_SIZE)
			throw new CombineException("TOO LARGE");
		if(result < MIN_PIECE_SIZE)
			throw new CombineException("TOO SMALL");

		pieces[secondIndex] = result;
		for(int i=firstIndex+1; i<numPieces; i++)
			pieces[i-1] = pieces[i];
		
		numPieces--;
		return result;
	}
	
	/**
	 * Combines two pieces using the specified operation. New piece will always take the place of the second operand.
	 * @param firstIndex The index of the first piece to be combined
	 * @param secondIndex The index of the second piece to be combined
	 * @param op The operation to combine with
	 * @return A game state representing this game state after the specified combine operation.
	 * @throws CombineException 
	 */
	public GameState afterCombine(int firstIndex, int secondIndex, int opIndex) throws CombineException{
		GameState result = new GameState(this);
		result.combine(firstIndex, secondIndex, opIndex);
		return result;
	}
}
