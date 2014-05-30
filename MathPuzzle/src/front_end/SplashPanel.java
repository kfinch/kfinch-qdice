package front_end;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class SplashPanel extends JPanel {

	private static final String SPLASH_MESSAGE = "Welcome to Q-Dice!";
	
	private void doDrawing(Graphics g){
		int width = getSize().width;
		int height = getSize().height;
		int textSize = width/12;
		
		g.setFont(new Font("Sans_Serif", Font.BOLD, textSize));
		Rectangle2D symbolDims = g.getFontMetrics().getStringBounds(SPLASH_MESSAGE, g);
		//int textHeight = (int) symbolDims.getHeight();
		int textWidth = (int) symbolDims.getWidth();
		int sx = (int) ((width - textWidth)/2);
		
		g.setColor(Color.black);
		g.drawString(SPLASH_MESSAGE,sx,height/2);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}
	
}
