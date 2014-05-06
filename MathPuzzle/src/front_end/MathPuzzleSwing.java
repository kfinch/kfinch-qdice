package front_end;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import back_end.GameState;

public class MathPuzzleSwing extends JFrame implements ActionListener {

	private GamePanel gamePanel;
	private Stack<GameState> gameHistory;
	
	public MathPuzzleSwing(){
		gameHistory = new Stack<GameState>();
		initUI();
		
		gameHistory.add(new GameState()); //TODO: REMOVE
	}
	
	public void initUI(){
		//initialize main window
		setTitle("Q-Dice");
		setSize(700,500); //TODO: see if this is reasonable
		setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //add button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
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
        
        //add game panel
        gamePanel = new GamePanel(gameHistory);
        gamePanel.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
        add(gamePanel, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Roll Again")){
			gamePanel.newGame();
		}
		if(command.equals("Preferences")){
			//TODO: Implement
		}
		if(command.equals("Quit")){
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
