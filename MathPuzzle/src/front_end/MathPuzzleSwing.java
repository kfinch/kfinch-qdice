package front_end;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MathPuzzleSwing extends JFrame implements ActionListener {

	private static final String GAME_CARDNAME = "game";
	private static final String SPLASH_CARDNAME = "splash";
	private static final String PREFERENCES_CARDNAME = "prefs";
	
	private JPanel containerPanel;
	
	private GamePanel gamePanel;
	private PreferencesPanel preferencesPanel;
	private SplashPanel splashPanel;
	
	public MathPuzzleSwing(){
		initUI();
	}
	
	public void initUI(){
		//initialize main window
		setTitle("Q-Dice");
		setSize(700,550); //TODO: see if this is reasonable
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //add button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(Color.gray, Color.gray));
        add(buttonPanel, BorderLayout.SOUTH);
        
        //add buttons
        JButton rollAgainButton = new JButton("Roll Again");
        JButton preferencesButton = new JButton("Preferences");
        JButton quitButton = new JButton("Quit");
        
        rollAgainButton.addActionListener(this);
        preferencesButton.addActionListener(this);
        quitButton.addActionListener(this);
        
        buttonPanel.add(rollAgainButton);
        buttonPanel.add(preferencesButton);
        buttonPanel.add(quitButton);
        
        //initialize various panels
        gamePanel = new GamePanel();
        //gamePanel.newGame();
        preferencesPanel = new PreferencesPanel();
        splashPanel = new SplashPanel();
        
        //initialize and add the container panel and its cards
    	containerPanel = new JPanel(new CardLayout());
    	containerPanel.add(gamePanel, GAME_CARDNAME);
    	containerPanel.add(splashPanel, SPLASH_CARDNAME);
    	containerPanel.add(preferencesPanel, PREFERENCES_CARDNAME);
    	add(containerPanel, BorderLayout.CENTER);
    	
    	swapToSplashPanel();
	}
	
	public void swapToGamePanel(){
		((CardLayout)containerPanel.getLayout()).show(containerPanel, GAME_CARDNAME);
		gamePanel.requestFocus();
	}
	
	public void swapToSplashPanel(){
		((CardLayout)containerPanel.getLayout()).show(containerPanel, SPLASH_CARDNAME);
		splashPanel.requestFocus();
	}
	
	public void swapToPreferencesPanel(){
		((CardLayout)containerPanel.getLayout()).show(containerPanel, PREFERENCES_CARDNAME);
		preferencesPanel.requestFocus();
	}
	
	public void newGame(){
		gamePanel.newGame();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Roll Again")){
			newGame();
			swapToGamePanel();
			repaint();
		}
		else if(command.equals("Preferences")){
			swapToPreferencesPanel();
			repaint();
		}
		else if(command.equals("Quit")){
			System.exit(0);
		}
	}

	public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MathPuzzleSwing game = new MathPuzzleSwing();
                game.setVisible(true);
            }
        });
    }
}
