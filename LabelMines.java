import java.awt.*;
import javax.swing.*;

/**
 * Displays the number of Mines in the game.
 * 
 * @author Daniel Rolandi
 * @version 4/25/2013
 */
public class LabelMines extends JLabel implements GameViewer{
  private GameBoard gameBoard;  
  
  /**
   * Connects the Label with the GameBoard.
   * @param gb Data center.
   * @throws IllegalArgumentException If supplied GameBoard is null.
   */
  public LabelMines(GameBoard gb){ 
    if(gb == null){
      throw new IllegalArgumentException("Expected game board.");
    }
    gameBoard = gb;
    
    setText("<html>Mines<br />" + (gameBoard.getTotalMinesCount() - gameBoard.getTotalFlagsCount()) + "</html>");
  }
  
  /** Updates label. */
  @Override
  public void update(){
    setText("<html>Mines<br />" + (gameBoard.getTotalMinesCount() - gameBoard.getTotalFlagsCount()) + "</html>");
  }
  
}
