package front_end;

import java.awt.BorderLayout;
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

	private GamePanel gamePanel;
	private JComponent preferencesPanel;
	private JComponent splashScreen;
	private MainPanelMode mode;
	
	public MathPuzzleSwing(){
		initUI();
	}
	
	public void initUI(){
		//initialize main window
		setTitle("Q-Dice");
		setSize(700,550); //TODO: see if this is reasonable
		setResizable(true);
		setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
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
        
        //TODO: TEST BUTTON PLEASE IGNORE
        //JButton testButton = new JButton("TEST BUTTON");
        //testButton.addActionListener(this);
        //buttonPanel.add(testButton);
        
        //create game panel
        gamePanel = new GamePanel();
        gamePanel.newGame();
        
        //create preferences panel
        preferencesPanel = new PreferencesPanel();
        
        //create (and show) splash screen
        splashScreen = new SplashPanel();
        add(splashScreen, BorderLayout.CENTER);
        
        mode = MainPanelMode.SPLASH;
	}
	
	
	public void switchMainPanel(MainPanelMode newMode){
		if(mode == newMode){ //do nothing if switching to mode we're already in
			System.out.println("No panel swap needed!");
			return;
		}
		
		switch(mode){ //remove old panel
		case SPLASH: remove(splashScreen); break;
		case PREFERENCES: remove(preferencesPanel); break;
		case GAME: remove(gamePanel); break;
		}
		
		switch(newMode){ //add new panel
		case SPLASH: add(splashScreen, BorderLayout.CENTER); break;
		case PREFERENCES: add(preferencesPanel, BorderLayout.CENTER); break;
		case GAME: add(gamePanel, BorderLayout.CENTER); break;
		}
		
		mode = newMode;
		validate();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Roll Again")){
			switchMainPanel(MainPanelMode.GAME);
			gamePanel.newGame();
			repaint();
		}
		else if(command.equals("Preferences")){
			switchMainPanel(MainPanelMode.PREFERENCES);
			repaint();
		}
		else if(command.equals("Quit")){
			System.exit(0);
		}
		else if(command.equals("TEST BUTTON")){ //TODO: TEST BUTTON PLEASE IGNORE
			System.out.println("beep boop");
			gamePanel.testMethod();
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
	
	private enum MainPanelMode{
		SPLASH, GAME, PREFERENCES
	}
}
