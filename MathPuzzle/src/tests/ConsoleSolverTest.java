package tests;

import java.util.Stack;

import solver.PuzzleSolver;
import back_end.CombineException;
import back_end.GameState;
import back_end.Move;

public class ConsoleSolverTest {

	public static void main(String args[]){
		
		GameState gs = new GameState();
		System.out.println(gs + "\n----------------\n");
		
		PuzzleSolver solver = new PuzzleSolver();
		Stack<Move> solution = solver.solve(gs);
		if(solution == null){
			System.out.println("No solution!");
			return;
		}
		
		Move m;
		while(!solution.isEmpty()){
			m = solution.pop();
			System.out.println(gs.moveString(m));
			try {
				gs.combine(m);
			} catch (CombineException e) {
				System.out.println("Solver tried an illegal move: " + m.firstIndex + " " + m.secondIndex + " " + m.opIndex);
				return;
			}
			System.out.println(gs + "\n----------------\n");
		}
	}
	
}
