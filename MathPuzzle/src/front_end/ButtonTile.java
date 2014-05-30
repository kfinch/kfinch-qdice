package front_end;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * An extension of a BasicTile that acts as a custom button.
 * Given action command will be the same as tile's symbol.
 * 
 * @author Kelton Finch
 */
public class ButtonTile extends BasicTile{
	
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	protected static final Color CLICK_COLOR = Color.decode("#ffc0c0");
	
	protected Color clickColor; //what color the background is when button is clicked on.
	protected Color notClickColor; //stores what color it is when not being clicked on.

	public ButtonTile(String command, GamePanel parent) {
		this(command, parent, CLICK_COLOR);
	}
	
	public ButtonTile(String command, GamePanel parent, Color clickColor){
		super(command, parent);
		this.clickColor = clickColor;
		this.notClickColor = backgroundColor;
	}
	
	public void mouseExited(MouseEvent e){
		super.mouseExited(e);
		setBackgroundColor(notClickColor);
		repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e){
		setBackgroundColor(clickColor);
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
		setBackgroundColor(notClickColor);
		if(mouseEntered)
			notifyListeners(e);
		repaint();
	}
	
	public void addActionListener(ActionListener listener){
		listeners.add(listener);
	}
	
	private void notifyListeners(MouseEvent e) {
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, symbol, e.getWhen(), e.getModifiers());
		synchronized(listeners){
			for (int i = 0; i < listeners.size(); i++){
				ActionListener tmp = listeners.get(i);
				tmp.actionPerformed(evt);
			}
		}
	}

}
