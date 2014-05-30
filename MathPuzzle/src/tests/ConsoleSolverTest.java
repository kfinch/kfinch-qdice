package tests;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import solver.PuzzleSolver;
import back_end.CombineException;
import back_end.GameState;
import back_end.Move;
import back_end.Operation;

public class ConsoleSolverTest {

	public static void main(String args[]){
		Scanner inputReader = new Scanner(System.in);
		String command = "";
		Scanner inputParser;
		GameState gs;
		PuzzleSolver solver = new PuzzleSolver();
		Stack<Move> solution;
		
		while(true){
			command = inputReader.nextLine();
			if(command == "q")
				break;
			inputParser = new Scanner(command);
			
			int goal = inputParser.nextInt();
			Operation ops[] = GameState.defaultOps();
			ArrayList<Integer> piecesList = new ArrayList<Integer>();
			while(inputParser.hasNextInt())
				piecesList.add(inputParser.nextInt());
			int pieces[] = new int[piecesList.size()];
			for(int i=0; i<pieces.length; i++)
				pieces[i] = piecesList.get(i);
			
			gs = new GameState(pieces, ops, pieces.length, ops.length, goal, true);
			solution = solver.solve(gs);
			printSolution(solution, gs);
		}
	}
	
	private static void printSolution(Stack<Move> solution, GameState initialState){
		if(solution == null){
			System.out.println("No solution!");
			return;
		}
		
		Move m;
		while(!solution.isEmpty()){
			m = solution.pop();
			System.out.println(initialState.moveString(m));
			try {
				initialState.combine(m);
			} catch (CombineException e) {
				System.out.println("Solver tried an illegal move: " + m.firstIndex + " " + m.secondIndex + " " + m.opIndex);
				return;
			}
			System.out.println(initialState + "\n----------------\n");
		}
	}
	
}
