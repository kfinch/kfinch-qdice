package front_end;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JPanel;

import back_end.GameState;
import back_end.CombineException;
import back_end.Operation;

public class GamePanel extends JPanel implements ActionListener {
	
	private static final float GOAL_HORIZONTAL_MULT = 0.05f; //percentage down the window to draw undo / goal / reset
	private static final float PIECES_HORIZONTAL_MULT = 0.30f; //percentage down the window to draw pieces
	private static final float MESSAGE_HORIZONTAL_MULT = 0.65f; //percentage down the window to draw messages
	private static final float OPS_HORIZONTAL_MULT = 0.80f; //percentage down the window to draw ops
	private static final int PIXEL_MARGIN = 5; //margin on left of boxes between box edge and start of label. TODO: Deprecate once I can center properly.
	private static final int DRAGLINE_RADIUS = 3; //width of line drawn when mouse is dragged
	
	private static final Color BUTTON_EDGE_COLOR = Color.decode("#707070");
	private static final Color FIRST_BACKGROUND_COLOR = Color.decode("#a0ffa0");
	private static final Color SECOND_BACKGROUND_COLOR = Color.decode("#a0a0ff");
	private static final Color OP_BACKGROUND_COLOR = Color.decode("#ffa0ff");
	
	private Stack<GameState> gameHistory; //the progression of game states. Bottom is initial state, top is current state.
	
	Dimension size; //The dimensions of the game panel, in pixels
	private int width, height; //the width and height of the game panel, in pixels
	private int numPieces; //number of pieces currently displayed
	private int numOps; //number of ops displayed (this will usually be 5)
	
	private int boxDim; //pixels to a side the pieces and ops boxs will be
	private int arcDim; //amount of rounding to boxs
	
	private int goalHorizontal; //pixels from top the undo / goal / reset are displayed
	private int piecesHorizontal; //pixels from the top the pieces are displayed
	private int opsHorizontal; //pixels from the top the ops are displayed
	private int messageHorizontal; //pixels from the top messages are displayed
	
	private List<Dimension> goalLocs; //locations of undo / goal / reset (in that order)
	private List<Dimension> piecesLocs; //locations of pieces buttons
	private List<Dimension> opsLocs; //locations of ops buttons
	
	private boolean mouseDragging; //tracks if the mouse is being dragged
	private List<Dimension> dragLine; //where the drag line should be drawn if the mouse is being dragged
	
	private String centerMessage; //Message to be displayed in center of game board.
	
	private MouseInput input; //adapter for mouse input
	private MouseMotionInput motionInput; //adapter for mouse motion input
	private ChoiceProgress progress; //tracks player progress in specifying a move.
	private int firstIndex; //index of player's choice for first piece. -1 if there no choice yet.
	private int secondIndex; //index of player's choice for second piece. -1 if there no choice yet.
	private int opIndex; //index of player's choice of operation. -1 if there no choice yet.
	
	public GamePanel(Stack<GameState> gameHistory){
		this.gameHistory = gameHistory;
		goalLocs = new ArrayList<Dimension>();
		piecesLocs = new ArrayList<Dimension>();
		opsLocs = new ArrayList<Dimension>();
		
		mouseDragging = false;
		dragLine = new ArrayList<Dimension>(200); //TODO: reasonable starting size?
		
		centerMessage = "";
		
		input = new MouseInput();
		motionInput = new MouseMotionInput();
		addMouseListener(input);
		addMouseMotionListener(motionInput);
		
		progress = ChoiceProgress.FIRST;
		firstIndex = -1;
		secondIndex = -1;
		opIndex = -1;
		
		repaint();
	}
	
	public void newGame(){
		System.out.println("Starting new game!"); //TODO: Remove debugging
		clearMessage();
		gameHistory.clear();
		gameHistory.push(new GameState());
		checkVictory();
		repaint();
	}
	
	private void doMove(){
		System.out.println("Doing a move!"); //TODO: Remove debugging
		clearMessage();
		try {
			gameHistory.push(gameHistory.peek().afterCombine(firstIndex, secondIndex, opIndex));
		} catch (CombineException e) {
			setMessage(e.getMessage());
		}
		progress = ChoiceProgress.FIRST;
		firstIndex = -1;
		secondIndex = -1;
		opIndex = -1;
		checkVictory();
		repaint();
	}
	
	private void doUndo(){
		System.out.println("Doing an undo!"); //TODO: Remove debugging
		clearMessage();
		if(progress != ChoiceProgress.FIRST){
			progress = ChoiceProgress.FIRST;
			firstIndex = -1;
			secondIndex = -1;
			opIndex = -1;
			repaint();
			return;
		}
		if(gameHistory.size() == 1) //can't undo a move if haven't yet made a move
			return; 
		gameHistory.pop();
		progress = ChoiceProgress.FIRST;
		firstIndex = -1;
		secondIndex = -1;
		opIndex = -1;
		checkVictory();
		repaint();
	}
	
	private void doReset(){
		System.out.println("Doing a reset!"); //TODO: Remove debugging
		clearMessage();
		while(gameHistory.size() != 1)
			gameHistory.pop();
		progress = ChoiceProgress.FIRST;
		firstIndex = -1;
		secondIndex = -1;
		opIndex = -1;
		checkVictory();
		repaint();
	}
	
	private void checkVictory(){
		GameState gameState = gameHistory.peek();
		if(gameState.getNumPieces() == 1){
			if(gameState.pieceAt(0) == gameState.getGoal()){
				setMessage("You win!");
			}
			else{
				setMessage("WRONG");
			}
		}
	}
	
	private void setMessage(String message){
		centerMessage = message;
		repaint();
	}
	
	private void clearMessage(){
		centerMessage = "";
		repaint();
	}
	
	/*
	 * Updates a variety of variables used for drawing to reflect the current window size.
	 */
	private void updateDimensions(){
		size = getSize();
		width = size.width;
		height = size.height;
		
		//for now I'll be leaving these as "magic numbers". As these values suggest, ideal aspect ratio is 10:7
		boxDim = Math.min(width/10, height/7);
		arcDim = boxDim/8;
		goalHorizontal = (int)(height * GOAL_HORIZONTAL_MULT);
		piecesHorizontal = (int) (height * PIECES_HORIZONTAL_MULT);
		opsHorizontal = (int) (height * OPS_HORIZONTAL_MULT);
		messageHorizontal = (int) (height * MESSAGE_HORIZONTAL_MULT);
		
		updatePieceAndOpLocs();
	}
	
	private void updatePieceAndOpLocs(){
		if(gameHistory.isEmpty())
			return;
		GameState gameState = gameHistory.peek(); //grabs relevant game state info
		
		//locates where the pieces and ops should be drawn
		numPieces = gameState.getNumPieces();
		numOps = gameState.getNumOps();
		buildLocs(width, piecesHorizontal, numPieces, piecesLocs);
		buildLocs(width, opsHorizontal, numOps, opsLocs);
		
		//locates where the undo button, goal, and reset button should be drawn.
		goalLocs.clear();
		goalLocs.add(new Dimension(boxDim, goalHorizontal));
		goalLocs.add(new Dimension((width/2)-boxDim, goalHorizontal));
		goalLocs.add(new Dimension(width-3*boxDim, goalHorizontal));
	}
	
	/*
	 * Helper method to generate the location of the piece and op buttons.
	 * Lists are modified as a side effect rather than created new as a return value to improve performance.
	 * (Performance may be relevant as this method will be repeatedly called during a window resize)
	 */
	private void buildLocs(int width, int horizontal, int numElements, List<Dimension> toModify){
		toModify.clear();
		
		if(numElements <= 5){ //centers boxes, 1*boxDim width boxes, 1*boxDim spacing between boxes
			int start = (int) (width/2 - boxDim*(numElements-0.5f));
			for(int i=0; i<numElements; i++)
				toModify.add(new Dimension(start+(i*2*boxDim),horizontal));
		}
		else{
			//TODO: Add handling for higher number of elements. (For now no scenario will have more than 5, so spacing will work)
		}
	}
	
	public void doDrawing(Graphics g){
		if(gameHistory.isEmpty())
			return;
		
		updateDimensions();
		
		Graphics2D g2d = (Graphics2D) g;
		GameState gameState = gameHistory.peek();
		
		Dimension loc;
		String label;
		
		//Draw highlighting for choices
		if(firstIndex != -1){
			g2d.setColor(FIRST_BACKGROUND_COLOR);
			g2d.fillRoundRect(piecesLocs.get(firstIndex).width, piecesLocs.get(firstIndex).height, boxDim, boxDim, arcDim, arcDim);
		}
		if(opIndex != -1){
			g2d.setColor(OP_BACKGROUND_COLOR);
			g2d.fillRoundRect(opsLocs.get(opIndex).width, opsLocs.get(opIndex).height, boxDim, boxDim, arcDim, arcDim);
		}
		if(secondIndex != -1){
			g2d.setColor(SECOND_BACKGROUND_COLOR);
			g2d.fillRoundRect(piecesLocs.get(secondIndex).width, piecesLocs.get(secondIndex).height, boxDim, boxDim, arcDim, arcDim);
		}
		
		//Draw message (if needed)
		g2d.setColor(Color.black);
		g2d.setFont(new Font("Sans_Serif", Font.BOLD, boxDim));
		g2d.drawString(centerMessage,width/3,messageHorizontal);
		
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("Sans_Serif", Font.BOLD, boxDim/2));
		
		//Draw Undo / Goal / Reset
		for(int i=0; i<3; i++){
			loc = goalLocs.get(i);
			switch(i){
			case 0 : label = "Undo"; break;
			case 1 : label = String.valueOf(gameState.getGoal()); break;
			case 2 : label = "Reset"; break;
			default : label = "ERROR"; break;
			}
			g2d.setColor(BUTTON_EDGE_COLOR);
			g2d.drawRoundRect(loc.width, loc.height, boxDim*2, boxDim, arcDim, arcDim);
			g2d.setColor(Color.black);
			g2d.drawString(label, loc.width+PIXEL_MARGIN, (int) (loc.height+(boxDim*0.7)));
		}
		
		//Draw pieces
		int piece;
		for(int i=0; i<numPieces; i++){
			piece = gameState.pieceAt(i);
			loc = piecesLocs.get(i);
			g2d.setColor(BUTTON_EDGE_COLOR);
			g2d.drawRoundRect(loc.width, loc.height, boxDim, boxDim, arcDim, arcDim);
			g2d.setColor(Color.black);
			g2d.drawString(String.valueOf(piece), loc.width+PIXEL_MARGIN, (int) (loc.height+(boxDim*0.7)));
		}
		
		//Draw Ops
		Operation op;
		for(int i=0; i<numOps; i++){
			op = gameState.opAt(i);
			label = op.symbol;
			loc = opsLocs.get(i);
			g2d.setColor(BUTTON_EDGE_COLOR);
			g2d.drawRoundRect(loc.width, loc.height, boxDim, boxDim, arcDim, arcDim);
			g2d.setColor(Color.black);
			g2d.drawString(label, loc.width+PIXEL_MARGIN, (int) (loc.height+(boxDim*0.7)));
		}
		
		//Draw drag line
		if(mouseDragging){
			g2d.setColor(Color.red);
			for(Dimension d : dragLine)
				g2d.fillOval(d.width-DRAGLINE_RADIUS, d.height-DRAGLINE_RADIUS, DRAGLINE_RADIUS*2, DRAGLINE_RADIUS*2);
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) { }
	
	private enum ChoiceProgress{
		FIRST,OPERATION,SECOND,DONE
	}
	
	//Adapter for receiving mouse input.
	private class MouseInput extends MouseAdapter {
			
		//TODO: Either make button handler helper methods, or find a way to do this with library buttons, cause this is super ugly.
		@Override
		public void mouseClicked(MouseEvent e){
			int mx = e.getX();
			int my = e.getY();
			int by, bx;
			List<Dimension> buttonRow;
			
			buttonRow = goalLocs;
			by = goalLocs.get(0).height;
			bx = goalLocs.get(0).width;
			if(my >= by && my <= by+boxDim && mx >= bx && mx <= bx+boxDim*2)
				doUndo();
			
			by = goalLocs.get(2).height;
			bx = goalLocs.get(2).width;
			if(my >= by && my <= by+boxDim && mx >= bx && mx <= bx+boxDim*2)
				doReset();
			
			switch(progress){
			case FIRST: case SECOND: by = piecesHorizontal; buttonRow = piecesLocs; break;
			case OPERATION: by = opsHorizontal; buttonRow = opsLocs; break;
			case DONE: return;
			default: return;
			}
			if(my < by || my > by+boxDim)
				return;
			
			for(int i = 0; i<buttonRow.size(); i++){
				bx = buttonRow.get(i).width;
				if(mx >= bx && mx <= bx+boxDim){
					System.out.println("Clicked a button!"); //TODO: Remove debugging code
					switch(progress){
					case FIRST: firstIndex = i; progress = ChoiceProgress.OPERATION; break;
					case SECOND: secondIndex = i; progress = ChoiceProgress.DONE; break;
					case OPERATION: opIndex = i; progress = ChoiceProgress.SECOND; break;
					}
					repaint();
				}
			}
			
			if(progress == ChoiceProgress.DONE)
				doMove();
		}
		
		@Override
		public void mousePressed(MouseEvent e){
			Dimension loc = new Dimension(e.getX(),e.getY());
			mouseDragging = true;
			dragLine.clear();
		}
		
		@Override
		public void mouseReleased(MouseEvent e){
			Dimension loc = new Dimension(e.getX(),e.getY());
			mouseDragging = false;
			if(progress == ChoiceProgress.DONE)
				doMove();
			repaint();
		}
		
	}
	
	private class MouseMotionInput extends MouseMotionAdapter {
		
		@Override
		public void mouseDragged(MouseEvent e){
			Dimension loc = new Dimension(e.getX(),e.getY());
			dragLine.add(loc);
			repaint();
			
			int mx = e.getX();
			int my = e.getY();
			int by, bx;
			List<Dimension> buttonRow;
			
			buttonRow = goalLocs;
			by = goalLocs.get(0).height;
			bx = goalLocs.get(0).width;
			if(my >= by && my <= by+boxDim && mx >= bx && mx <= bx+boxDim*2)
				doUndo();
			
			by = goalLocs.get(2).height;
			bx = goalLocs.get(2).width;
			if(my >= by && my <= by+boxDim && mx >= bx && mx <= bx+boxDim*2)
				doReset();
			
			switch(progress){
			case FIRST: case SECOND: by = piecesHorizontal; buttonRow = piecesLocs; break;
			case OPERATION: by = opsHorizontal; buttonRow = opsLocs; break;
			case DONE: return;
			default: return;
			}
			if(my < by || my > by+boxDim)
				return;
			
			for(int i = 0; i<buttonRow.size(); i++){
				bx = buttonRow.get(i).width;
				if(mx >= bx && mx <= bx+boxDim){
					System.out.println("Clicked a button!"); //TODO: Remove debugging code
					switch(progress){
					case FIRST: firstIndex = i; progress = ChoiceProgress.OPERATION; break;
					case SECOND: secondIndex = i; progress = ChoiceProgress.DONE; break;
					case OPERATION: opIndex = i; progress = ChoiceProgress.SECOND; break;
					}
					repaint();
				}
			}
		}
		
	}
	
}

