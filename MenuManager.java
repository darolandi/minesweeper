import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Creates and handles menu and its events.
 * 
 * @author Daniel Rolandi
 * @version 5/9/2013
 */
public class MenuManager extends JMenuBar implements ActionListener{
  private static final String NEW_GAME = "new";
  private static final String EXIT_GAME = "exit";
  private static final String ABOUT_GAME = "about";
  private static final String DIFFICULTY_0 = "beginner";
  private static final String DIFFICULTY_1 = "intermediate";
  private static final String DIFFICULTY_2 = "expert";
  
  private static final String ABOUT_MESSAGE =
    "Author: Daniel Rolandi" +
    "\nVersion: May 2013" +
    "\nhttp://www.linkedin.com/in/danielrolandi";    
  
  private GameBoard gameBoard;  
  
  /**
   * Connects the Menu Bar with the GameBoard.
   * @param gb Data center.
   * @throws IllegalArgumentException If supplied GameBoard is null.
   */
  public MenuManager(GameBoard gb){
    if(gb == null){
      throw new IllegalArgumentException("Expected game board.");
    }
    gameBoard = gb;
    
    // (F)ile
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    add(fileMenu);
    
    JMenuItem newGame = new JMenuItem("New Game", KeyEvent.VK_N);
    newGame.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0) );
    newGame.addActionListener(this);
    newGame.setActionCommand(NEW_GAME);
    fileMenu.add(newGame);
    
    JMenuItem exitGame = new JMenuItem("Exit Game", KeyEvent.VK_X);    
    exitGame.addActionListener(this);
    exitGame.setActionCommand(EXIT_GAME);
    fileMenu.add(exitGame);
    
    // (D)ifficulty
    JMenu difficultyMenu = new JMenu("Difficulty");
    difficultyMenu.setMnemonic(KeyEvent.VK_D);
    add(difficultyMenu);
    
    ButtonGroup buttonGroup = new ButtonGroup();
    JRadioButtonMenuItem beginnerButton = new JRadioButtonMenuItem("Beginner");
    JRadioButtonMenuItem intermediateButton = new JRadioButtonMenuItem("Intermediate");
    JRadioButtonMenuItem expertButton = new JRadioButtonMenuItem("Expert");
    
    beginnerButton.setSelected(true);
    beginnerButton.addActionListener(this);
    beginnerButton.setActionCommand(DIFFICULTY_0);
    buttonGroup.add(beginnerButton);
    difficultyMenu.add(beginnerButton);
    
    intermediateButton.addActionListener(this);
    intermediateButton.setActionCommand(DIFFICULTY_1);
    buttonGroup.add(intermediateButton);
    difficultyMenu.add(intermediateButton);
    
    expertButton.addActionListener(this);
    expertButton.setActionCommand(DIFFICULTY_2);
    buttonGroup.add(expertButton);
    difficultyMenu.add(expertButton);
    
    // (A)bout
    JMenuItem aboutMenu = new JMenuItem("About");    
    aboutMenu.addActionListener(this);
    aboutMenu.setActionCommand(ABOUT_GAME);
    add(aboutMenu);

    // dummy
    JMenuItem dummyMenu = new JMenu(" ");
    dummyMenu.setEnabled(false);
    add(dummyMenu);
  }
  
  /**
   * Handles any events on the button menu.
   * @param e Any click.
   */
  @Override
  public void actionPerformed(ActionEvent e){
    String s = e.getActionCommand();
    switch(s){
      case NEW_GAME:
        gameBoard.newGame();
        break;
      case EXIT_GAME:
        System.exit(0);
        break;
      case DIFFICULTY_0:
        gameBoard.setDifficulty(Difficulty.BEGINNER);
        break;
      case DIFFICULTY_1:
        gameBoard.setDifficulty(Difficulty.INTERMEDIATE);
        break;
      case DIFFICULTY_2:
        gameBoard.setDifficulty(Difficulty.EXPERT);
        break;
      case ABOUT_GAME:
        JOptionPane.showMessageDialog(null, ABOUT_MESSAGE);
        break;
    }    
  }
  
  
}