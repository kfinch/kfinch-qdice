package front_end;

import java.awt.Graphics;

import javax.swing.JPanel;

public class PreferencesPanel extends JPanel{

	
	private void doDrawing(Graphics g){
		g.drawString("THIS IS PREFS LOL", 10, 50);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}
	
}
