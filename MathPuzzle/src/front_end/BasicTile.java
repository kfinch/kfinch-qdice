package front_end;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

/**
 * A basic 'tile' for use displaying elements of the game board.
 * Has a custom look, and centers some portion of text in its center.
 * Changes appearance on mouse over.
 * 
 * Currently it looks very simple, can be improved later.
 * 
 * @author Kelton Finch
 */
public class BasicTile extends JPanel implements MouseListener{

	protected static final int EDGE_WIDTH = 4;
	protected static final Color BORDER_COLOR = GamePanel.BORDER_COLOR;
	protected static final Color MOUSEOVER_COLOR = Color.decode("#FF0000");
	
	protected int width;
	protected int height;
	protected int arcDim;
	protected int fontSize;
	
	protected int edgeWidth;
	protected Color borderColor;
	protected Color mouseoverColor;
	
	protected boolean hasBackground;
	protected Color backgroundColor;
	
	protected GamePanel parent;
	
	protected String symbol;
	
	protected boolean mouseEntered;
	
	public BasicTile(String symbol, GamePanel parent, int edgeWidth, Color borderColor, Color mouseoverColor){
		this.symbol = symbol;
		this.parent = parent;
		this.edgeWidth = edgeWidth;
		this.borderColor = borderColor;
		this.mouseoverColor = mouseoverColor;
		hasBackground = false;
		backgroundColor = Color.white;
		mouseEntered = false;
		addMouseListener(this);
	}
	
	public BasicTile(String symbol, GamePanel parent){
		this(symbol, parent, EDGE_WIDTH, BORDER_COLOR, MOUSEOVER_COLOR);
	}
	
	public void setSymbol(String newSymbol){
		symbol = newSymbol;
		repaint();
	}
	
	public void setBackgroundColor(Color newBackgroundColor){
		hasBackground = true;
		backgroundColor = newBackgroundColor;
	}
	
	public void clearBackground(){
		hasBackground = false;
	}
	
	private void updateDimensions(){
		Dimension size = getSize();
		width = size.width;
		height = size.height;
		int boxDim = Math.min(width, height);
		arcDim = (int) (boxDim * 0.2);
		fontSize = (int) (boxDim * 0.4);
	}
	
	private void doDrawing(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		updateDimensions();
		
		//draw background, if it's turned on
		if(hasBackground){
			g2d.setColor(backgroundColor);
			g2d.fillRoundRect(edgeWidth, edgeWidth, width-edgeWidth*2, height-edgeWidth*2, arcDim, arcDim);
		}
		
		//draw border
		g2d.setStroke(new BasicStroke(edgeWidth));
		g2d.setColor(borderColor);
		if(mouseEntered)
			g2d.setColor(mouseoverColor);
		g2d.drawRoundRect(edgeWidth, edgeWidth, width-edgeWidth*2, height-edgeWidth*2, arcDim, arcDim);
		
		//draw (centered) symbol
		g2d.setColor(Color.black);
		g2d.setFont(new Font(Font.SANS_SERIF,Font.BOLD,fontSize));
		Rectangle2D symbolDims = g2d.getFontMetrics().getStringBounds(symbol, g2d);
		int sy = (int) ((height + symbolDims.getHeight())/2);
		int sx = (int) ((width - symbolDims.getWidth())/2);
		g2d.drawString(symbol, sx, sy);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseEntered = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseEntered = false;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }
	
}
