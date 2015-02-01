package front_end;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;

import solver.PuzzleSolver;
import back_end.GameState;
import back_end.CombineException;

public class GamePanel extends JPanel implements ActionListener, ComponentListener, MouseListener, MouseMotionListener {
	
	private static final float GOAL_HORIZONTAL_MULT = 0.05f; //percentage down the window to draw undo / goal / reset
	private static final float PIECES_HORIZONTAL_MULT = 0.30f; //percentage down the window to draw pieces
	private static final float MESSAGE_HORIZONTAL_MULT = 0.65f; //percentage down the window to draw messages
	private static final float OPS_HORIZONTAL_MULT = 0.75f; //percentage down the window to draw ops
	private static final int DRAGLINE_WIDTH = 4; //width of line drawn when mouse is dragged
	private static final int MARGIN = 5; //margin, in pixels, afforded to some elements
	
	private static final int MAX_NEWGAME_GENERATE_ATTEMPTS = 20; //maximum attempts to generate a new solvable game under current settings
	
	//some preset colors
	protected static final Color BORDER_COLOR = Color.decode("#707070");
	protected static final Color FIRST_BACKGROUND_COLOR = Color.decode("#a0ffa0");
	protected static final Color SECOND_BACKGROUND_COLOR = Color.decode("#a0a0ff");
	protected static final Color OP_BACKGROUND_COLOR = Color.decode("#a0ffff");
	protected static final Color DRAGLINE_COLOR = Color.decode("#ff5555");
	
	protected static final Color GOOD_MESSAGE_COLOR = Color.decode("#b0ffb0");
	protected static final Color NEUTRAL_MESSAGE_COLOR = Color.decode("#b0c0ff");
	protected static final Color BAD_MESSAGE_COLOR = Color.decode("#ffb0b0");
	
	private Stack<GameState> gameHistory; //the progression of game states. Bottom is initial state, top is current state.
	
	private Dimension size; //The dimensions of the game panel, in pixels
	private int width, height; //the width and height of the game panel, in pixels
	
	private int boxDim; //pixels to a side the pieces and ops boxs will be
	private int arcDim; //amount of rounding on boxes
	
	private int goalHorizontal; //pixels from top the undo / goal / reset are displayed
	private int piecesHorizontal; //pixels from the top the pieces are displayed
	private int opsHorizontal; //pixels from the top the ops are displayed
	private int messageHorizontal; //pixels from the top messages are displayed
	
	private List<Dimension> goalLocs; //locations of undo / goal / reset (in that order)
	private List<Dimension> piecesLocs; //locations of pieces buttons
	private List<Dimension> opsLocs; //locations of ops buttons
	
	private boolean mouseDragging; //tracks if the mouse is being dragged
	private List<Dimension> dragLine; //where the drag line should be drawn if the mouse is being dragged
	
	private List<PieceTile> pieceTiles; //JComponents for displaying the operands
	private List<PieceTile> opTiles; //JComponents for displaying the operators
	
	private ButtonTile undoButton; //JButton for undoing most recent move.
	private ButtonTile resetButton; //JButton for reseting game state.
	private BasicTile goalTile;
	
	private boolean showMessage; //tracks if there is a message to show
	private String centerMessage; //message to be displayed in center of game board.
	private Color messageColor; //Color of message's backing box
	
	private ChoiceProgress progress; //tracks player progress in specifying a move.
	private int firstIndex; //index of player's choice for first piece. -1 if there no choice yet.
	private int secondIndex; //index of player's choice for second piece. -1 if there no choice yet.
	private int opIndex; //index of player's choice of operation. -1 if there no choice yet.
	
	public GamePanel(){
		initUI();
	}
	
	private void initUI(){
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		
		gameHistory = new Stack<GameState>();
		gameHistory.push(new GameState()); //TODO: this will have to be changed when preferences changed
		
		goalLocs = new ArrayList<Dimension>();
		piecesLocs = new ArrayList<Dimension>();
		opsLocs = new ArrayList<Dimension>();
		
		mouseDragging = false;
		dragLine = new ArrayList<Dimension>(200); //TODO: reasonable starting size?
		
		pieceTiles = new ArrayList<PieceTile>(GameState.MAX_PIECES);
		opTiles = new ArrayList<PieceTile>(GameState.MAX_OPS);
		updateTiles();
		
		showMessage = false;
		
		updateDimensions();
		revalidate();
		repaint();
		
		System.out.println("Game panel init complete!");
	}
	
	//TODO: Remove
	public void testMethod(){
		updateTiles();
	}
	
	//TODO: is there a better way to do this than adding and removing tiles every time?
	private void updateTiles(){
		if(gameHistory.isEmpty())
			return;
		
		int numPieces = gameHistory.peek().getNumPieces();
		int numOps = gameHistory.peek().getNumOps();
		
		updateComponentLocs();
		
		for(PieceTile pt : pieceTiles)
			remove(pt);
		for(PieceTile pt : opTiles)
			remove(pt);
		
		pieceTiles = new ArrayList<PieceTile>(numPieces);
		opTiles = new ArrayList<PieceTile>(numOps);
		
		String s;
		for(int i=0; i<numPieces; i++){
			s = gameHistory.peek().pieceAt(i) + ""; //janky toString ahoy
			PieceTile pt = new PieceTile(i,false,s,this);
			Dimension loc = piecesLocs.get(i);
			pt.setBounds(loc.width, loc.height, boxDim, boxDim);
			pt.setVisible(true);
			pieceTiles.add(pt);
			add(pt);
		}
		for(int i=0; i<numOps; i++){
			s = gameHistory.peek().opAt(i).symbol;
			PieceTile pt = new PieceTile(i,true,s,this);
			Dimension loc = opsLocs.get(i);
			pt.setBounds(loc.width, loc.height, boxDim, boxDim);
			pt.setVisible(true);
			opTiles.add(pt);
			add(pt);
		}
		
		if(undoButton != null)
			remove(undoButton);
		if(resetButton != null)
			remove(resetButton);
		if(goalTile != null)
			remove(goalTile);
		
		undoButton = new ButtonTile("Undo", this);
		resetButton = new ButtonTile("Reset", this);
		goalTile = new BasicTile(gameHistory.peek().getGoal() + "", this, BasicTile.EDGE_WIDTH, Color.black, Color.black);
		
		undoButton.addActionListener(this);
		resetButton.addActionListener(this);
		
		Dimension loc = goalLocs.get(0);
		undoButton.setBounds(loc.width, loc.height, (int) (boxDim*1.5), boxDim);
		add(undoButton);
		
		loc = goalLocs.get(1);
		goalTile.setBounds(loc.width, loc.height, boxDim*2, boxDim);
		add(goalTile);
		
		loc = goalLocs.get(2);
		resetButton.setBounds(loc.width, loc.height, (int) (boxDim*1.5), boxDim);
		add(resetButton);
		
		repaint();
	}
	
	/*
	 * Called from a child when an operand is clicked.
	 * index is the child's index in its list
	 */
	protected void operandClicked(int index){
		if(progress == ChoiceProgress.FIRST){
			firstIndex = index;
			progress = ChoiceProgress.OPERATION;
			pieceTiles.get(firstIndex).setBackgroundColor(FIRST_BACKGROUND_COLOR);
			System.out.println("Operand index " + index + " clicked.\nChoice progress is " + progress);
		}
		else if(progress == ChoiceProgress.SECOND && index != firstIndex){
			secondIndex = index;
			progress = ChoiceProgress.DONE;
			pieceTiles.get(secondIndex).setBackgroundColor(SECOND_BACKGROUND_COLOR);
			System.out.println("Operand index " + index + " clicked.\nChoice progress is " + progress);
			if(!mouseDragging)
				doMove();
		}
	}
	
	/*
	 * Called from a child when an operator is clicked.
	 * index is the child's index in its list
	 */
	protected void operatorClicked(int index){
		if(progress == ChoiceProgress.OPERATION){
			opIndex = index;
			progress = ChoiceProgress.SECOND;
			opTiles.get(opIndex).setBackgroundColor(OP_BACKGROUND_COLOR);
			System.out.println("Operator index " + index + " clicked.\nChoice progress is " + progress);
		}
	}
	
	/*
	 * Called from a child when an operand is entered.
	 * index is the child's index in its list.
	 */
	protected void operandEntered(int index){
		if(mouseDragging)
			operandClicked(index);
	}
	
	/*
	 * Called from a child when an operator is entered.
	 * index is the child's index in its list.
	 */
	protected void operatorEntered(int index){
		if(mouseDragging)
			operatorClicked(index);
	}
	
	/*
	 * Called when a mouse drag starts on one of the child panels.
	 */
	protected void childMouseDragged(Point p, MouseEvent e){
		e.translatePoint(p.x, p.y);
		Dimension loc = new Dimension(e.getX(),e.getY());
		dragLine.add(loc);
		repaint();
	}
	
	/*
	 * Starts a new game with the set preferences.
	 */
	protected void newGame(){
		System.out.println("Starting new game!"); //TODO: Remove debugging
		//if a click happens in a child, this fails to release normally, so doing it manually here.
		//kind of a hack :/
		mouseDragging = false;
		clearMessage();
		gameHistory.clear();
		
		GameState newGameState = new GameState();
		int attempts = 0;
		PuzzleSolver solver = new PuzzleSolver();
		boolean solvable = false;
		while(!solvable){
			if(solver.solve(newGameState) != null){
				solvable = true;
			}
			else if(attempts >= MAX_NEWGAME_GENERATE_ATTEMPTS){
				break;
				//TODO: change this to for loop? Make it handle the bad attempt
			}
			attempts++;
			newGameState = new GameState(); //TODO: this line must be changed when prefs implemented
		}
		
		gameHistory.push(newGameState);
		goalTile.setSymbol(gameHistory.peek().getGoal() + "");
		updateTiles();
		resetTurnProgress();
		checkVictory();
		repaint();
	}
	
	/*
	 * executes the move specified by firstIndex, secondIndex, opIndex.
	 * Doesn't check bounds. Don't call this until the indices are set!
	 */
	private void doMove(){
		System.out.println("Doing a move!"); //TODO: Remove debugging
		clearMessage();
		try {
			gameHistory.push(gameHistory.peek().afterCombine(firstIndex, secondIndex, opIndex));
			updateTiles();
		} catch (CombineException e) {
			setMessage(e.getMessage(), BAD_MESSAGE_COLOR);
		}
		resetTurnProgress();
		checkVictory();
		repaint();
	}
	
	/*
	 * Executes an undo of the most recent move (or does nothing if game is in initial state).
	 */
	private void doUndo(){
		System.out.println("Doing an undo!"); //TODO: Remove debugging
		clearMessage();
		if(progress != ChoiceProgress.FIRST){
			resetTurnProgress();
			return;
		}
		if(gameHistory.size() == 1) //can't undo a move if haven't yet made a move
			return; 
		gameHistory.pop();
		updateTiles();
		resetTurnProgress();
		checkVictory();
		repaint();
	}
	
	/*
	 * Resets the current game to its initial state (or does nothing if the game is already at its initial state).
	 */
	private void doReset(){
		System.out.println("Doing a reset!"); //TODO: Remove debugging
		clearMessage();
		while(gameHistory.size() != 1)
			gameHistory.pop();
		updateTiles();
		resetTurnProgress();
		checkVictory();
		repaint();
	}
	
	private void resetTurnProgress(){
		progress = ChoiceProgress.FIRST;
		firstIndex = -1;
		secondIndex = -1;
		opIndex = -1;
		for(PieceTile pt : pieceTiles)
			pt.clearBackground();
		for(PieceTile pt : opTiles)
			pt.clearBackground();
		repaint();
	}
	
	/*
	 * Checks if the current game state is a winning (or an incorrect) state. Changes the message if needed.
	 */
	private void checkVictory(){
		GameState gameState = gameHistory.peek();
		if(gameState.getNumPieces() == 1){
			if(gameState.pieceAt(0) == gameState.getGoal()){
				setMessage("You win!", GOOD_MESSAGE_COLOR);
			}
			else{
				setMessage("NOPE", BAD_MESSAGE_COLOR);
			}
		}
	}
	
	/*
	 * Sets the message bar to display message with the default colored backing box.
	 */
	private void setMessage(String message){
		this.setMessage(message, NEUTRAL_MESSAGE_COLOR);
	}
	
	/*
	 * Sets the message bar to display message with the specified colored backing box.
	 */
	private void setMessage(String message, Color backingColor){
		showMessage = true;
		centerMessage = message;
		messageColor = backingColor;
		repaint();
	}
	
	/*
	 * Clears the message bar.
	 */
	private void clearMessage(){
		showMessage = false;
		repaint();
	}
	
	/*
	 * Updates a variety of variables used for drawing to reflect the current window size.
	 */
	private void updateDimensions(){
		size = getSize();
		width = size.width;
		height = size.height;
		
		//for now I'll be leaving these as "magic numbers". As these values suggest, ideal aspect ratio is 4:3
		boxDim = Math.min(width/8, height/6);
		arcDim = boxDim/5;
		goalHorizontal = (int)(height * GOAL_HORIZONTAL_MULT);
		piecesHorizontal = (int) (height * PIECES_HORIZONTAL_MULT);
		opsHorizontal = (int) (height * OPS_HORIZONTAL_MULT);
		messageHorizontal = (int) (height * MESSAGE_HORIZONTAL_MULT);
		
		updateComponentLocs();
	}
	
	/*
	 * Updates the locations of the operator and operand tiles.
	 * Needed after the game state changes.
	 */
	private void updateComponentLocs(){
		if(gameHistory.isEmpty())
			return;
		
		//locates where the pieces and ops should be drawn
		GameState gameState = gameHistory.peek();
		int numPieces = gameState.getNumPieces();
		int numOps = gameState.getNumOps();
		buildLocs(width, piecesHorizontal, numPieces, piecesLocs);
		buildLocs(width, opsHorizontal, numOps, opsLocs);
		
		//updates piece tile locations and sizes
		if(pieceTiles.size() == numPieces){
			for(int i=0; i<numPieces; i++)
				pieceTiles.get(i).setBounds(piecesLocs.get(i).width, piecesLocs.get(i).height, boxDim, boxDim);
		}
		if(pieceTiles.size() == numOps){
			for(int i=0; i<numOps; i++)
				opTiles.get(i).setBounds(opsLocs.get(i).width, opsLocs.get(i).height, boxDim, boxDim);
		}
		
		//locates where the undo button, goal, and reset button should be drawn.
		goalLocs.clear();
		goalLocs.add(new Dimension((int) (0.5*boxDim), goalHorizontal));
		goalLocs.add(new Dimension((width/2)-boxDim, goalHorizontal));
		goalLocs.add(new Dimension((int) (width-2*boxDim), goalHorizontal));
		
	}
	
	/*
	 * Helper method to generate the location of the piece and op buttons.
	 * Lists are modified as a side effect rather than created new as a return value to improve performance.
	 * (Performance may be relevant as this method will be repeatedly called during a window resize)
	 */
	private void buildLocs(int width, int horizontal, int numElements, List<Dimension> toModify){
		toModify.clear();
		
		float portion = width/numElements;
		for(int i=0; i<numElements; i++)
			toModify.add(new Dimension((int) (((i+0.5)*portion)-boxDim/2), horizontal));
	}
	
	public void doDrawing(Graphics g){
		if(gameHistory.isEmpty())
			return;
		
		updateComponentLocs(); //TODO: Must this stay here?
		
		Graphics2D g2d = (Graphics2D) g;
		GameState gameState = gameHistory.peek();
		
		//Draw message (if needed)
		if(showMessage){
			g2d.setFont(new Font("Sans_Serif", Font.BOLD, boxDim/2));
			Rectangle2D symbolDims = g2d.getFontMetrics().getStringBounds(centerMessage, g2d);
			int textHeight = (int) symbolDims.getHeight();
			int textWidth = (int) symbolDims.getWidth();
			int sx = (int) ((width - textWidth)/2);
			
			g2d.setColor(messageColor);
			g2d.fillRect(sx-MARGIN, messageHorizontal-textHeight+2*MARGIN, textWidth+2*MARGIN, textHeight);
			
			g2d.setColor(Color.black);
			g2d.drawString(centerMessage,sx,messageHorizontal);
		}
		
		//Children painted manually *before* the dragline (dragline needs to be on top)
		paintChildren(g);
		
		//Draw drag line
		if(mouseDragging && dragLine.size() >= 10){
			g2d.setColor(DRAGLINE_COLOR);
			g2d.setStroke(new BasicStroke(DRAGLINE_WIDTH));
			for(int i=1; i<dragLine.size(); i++)
				g2d.drawLine(dragLine.get(i-1).width, dragLine.get(i-1).height, dragLine.get(i).width, dragLine.get(i).height);
		}
	}
	
	public void paint(Graphics g){
		super.paint(g);
		doDrawing(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Undo")){
			doUndo();
		}
		if(command.equals("Reset")){
			doReset();
		}
	}
	
	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) {
		updateDimensions();
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent e) { }
	
	private enum ChoiceProgress{
		FIRST,OPERATION,SECOND,DONE
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Dimension loc = new Dimension(e.getX(),e.getY());
		dragLine.add(loc);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) { }

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDragging = true;
		dragLine.clear();
		System.out.println("Mouse pressed!"); //TODO: Remove debugging
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDragging = false;
		if(progress == ChoiceProgress.DONE)
			doMove();
		System.out.println("Mouse released!"); //TODO: Remove debugging
		repaint();
	}
	
}

