import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Displays the number of Mines in the game.
 * 
 * @author Daniel Rolandi
 * @version 5/2/2013
 */
public class LabelTimer extends JLabel implements GameViewer, ActionListener{
  private static final int timerDelay = 20;
  private GameBoard gameBoard;
  private Timer gameTimer;  
  
  /**
   * Connects the Label with the GameBoard.
   * @param gb Data center.
   * @throws IllegalArgumentException If supplied GameBoard is null.
   */
  public LabelTimer(GameBoard gb){ 
    if(gb == null){
      throw new IllegalArgumentException("Expected game board.");
    }
    gameBoard = gb;
    gameTimer = new Timer(timerDelay, this);      
    
    setText("<html>Timer<br />" + (int)(gameBoard.timeElapsed) + "</html>");
  }
  
  /** Updates label. */
  @Override
  public void update(){
    if(gameBoard.isPlaying){
      if(! gameTimer.isRunning()){
        gameTimer.start();
      }
    }else{
      if(gameTimer.isRunning()){
        gameTimer.stop();        
      }
    }
    setText("<html>Timer<br />" + (int)(gameBoard.timeElapsed) + "</html>");
  }
  
  /**
   * Called by the Timer to keep track of time.
   * @param e Timer-generated event.
   */
  @Override
  public void actionPerformed(ActionEvent e){
    if(gameBoard.isPlaying){
      gameBoard.timeElapsed += timerDelay / 1000.0;
    }
    update();
  }
  
}
