package front_end;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PreferencesPanel extends JPanel{

	
	private void doDrawing(Graphics g){
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		g.drawString("PREFERENCES NOT YET IMPLEMENTED, SORRY )=", 10, 30);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}
	
}
