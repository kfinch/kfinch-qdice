package solver;

import java.util.Stack;

import back_end.CombineException;
import back_end.GameState;
import back_end.Move;
import back_end.Operation;

public class PuzzleSolver {
	
	public PuzzleSolver(){
		
	}
	
	/**
	 * Finds a solution for the given game state.
	 * @param gs The game state to be solved for.
	 * @return A solution, in the form of a stack of moves. Pop a move off the stack, apply it to the game state,
	 * 		   and repeat until the stack is empty, and the resulting position will be winning.
	 * 		   Returns null if the given game state has no solution.
	 */
	public Stack<Move> solve(GameState gs){
		return solveRecurse(gs,null);
	}
	
	private Stack<Move> solveRecurse(GameState gs, Move m){
		if(gs.getNumPieces() == 1){ //ending position
			if(gs.pieceAt(0) == gs.getGoal()){ //winning position
				Stack<Move> result = new Stack<Move>();
				result.push(m);
				return result;
			}
			else{ //losing position
				return null;
			}
		}
		
		//recurse with the result of each legal move from this position
		Operation op;
		Move next;
		Stack<Move> path = null;
		for(int i=0; i<gs.getNumPieces(); i++){
			for(int j=i+1; j<gs.getNumPieces(); j++){
				for(int k=0; k<gs.getNumOps(); k++){
					op = gs.opAt(k);
					next = new Move(i,j,k);
					try {
						path = solveRecurse(gs.afterCombine(next), next);
					} catch (CombineException e) {
						path = null;
					}
					if(path != null){
						if(m != null)
							path.push(m);
						return path;
					}
					if(op.isCommutative){
						next = new Move(j,i,k);
						try {
							path = solveRecurse(gs.afterCombine(next), next);
						} catch (CombineException e) {
							path = null;
						}
						if(path != null){
							if(m != null)
								path.push(m);
							return path;
						}
					}
				}
			}
		}
		return null;
	}
	
}
