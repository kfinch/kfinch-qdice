package front_end;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * A tile that represents an operator or operand.
 * 
 * @author Kelton Finch
 */
public class PieceTile extends BasicTile implements MouseMotionListener {
	
	private int index;
	private boolean isOperator;
	
	public PieceTile(int index, boolean isOperator, String symbol, GamePanel parent){
		super(symbol,parent);
		this.index = index;
		this.isOperator = isOperator;
		addMouseMotionListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		if(isOperator)
			parent.operatorEntered(index);
		else
			parent.operandEntered(index);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		parent.dispatchEvent(e);
		if(isOperator)
			parent.operatorClicked(index);
		else
			parent.operandClicked(index);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		parent.dispatchEvent(e);
	}

	@Override
	//Has to call a special method rather than translating the point and dispatching because if I do it that way
	//it thinks the cursor is in the wrong place for purposes of clicking on other buttons.
	//I don't fully understand why that was a side effect, but creating a custom method to call instead fixed it.
	public void mouseDragged(MouseEvent e) {
		Point thisLoc = getLocation();
		parent.childMouseDragged(thisLoc,e);
	}

	@Override
	public void mouseMoved(MouseEvent e) { }

}
