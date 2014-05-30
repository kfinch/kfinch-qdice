package tests;

import java.util.Stack;

import solver.PuzzleSolver;
import back_end.GameState;
import back_end.Move;

public class MiscTester {

	public static void main(String args[]){
		percentSolvable(10000);
	}
	
	private static void percentSolvable(int reps){
		int numSolvable = 0;
		GameState gs;
		PuzzleSolver solver = new PuzzleSolver();
		Stack<Move> solution;
		
		for(int i=0; i<reps; i++){
			System.out.println("Test #" + i + "...");
			gs = new GameState();
			solution = solver.solve(gs);
			if(solution != null)
				numSolvable++;
		}
		
		System.out.println("Tried " + reps + " rolls, solved " + numSolvable + " of them.");
		System.out.println("That's " + (float)numSolvable / (float)reps * 100 + "%");
	}
	
}
