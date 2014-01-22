import javax.swing.*;
import java.awt.*;

/**
 * Launcher for the Minesweeper Game.
 * 
 * @author Daniel Rolandi
 * @version 5/2/2013
 */
public class MinesweeperApp{
  
  /** Starts the application. */
  public static void main(String[] args){
    JFrame frame = new JFrame("Mines");
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setResizable(false);
    
    // add stuff
    Container cp = frame.getContentPane();
    
    JPanel mainPanel = new JPanel(new BorderLayout());
    
    JPanel minefield = new JPanel();
    GameBoard gameBoard = new GameBoard(frame, minefield);
    mainPanel.add(minefield, BorderLayout.CENTER);
    
    JPanel statusPanel = new JPanel(new BorderLayout());
    LabelMines mines = new LabelMines(gameBoard);
    gameBoard.addViewer(mines);
    statusPanel.add(mines, BorderLayout.WEST);
    
    LabelFlags flags = new LabelFlags(gameBoard);
    gameBoard.addViewer(flags);
    statusPanel.add(flags, BorderLayout.CENTER);        
    
    LabelTimer timer = new LabelTimer(gameBoard);
    gameBoard.addViewer(timer);
    statusPanel.add(timer, BorderLayout.EAST);
    
    mainPanel.add(statusPanel, BorderLayout.NORTH);
    
    cp.add(mainPanel);
    MenuManager menuManager = new MenuManager(gameBoard);
    
    frame.pack();
    frame.setJMenuBar(menuManager);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();      
    frame.setLocation( (dim.width - frame.getWidth())/2, (dim.height - frame.getHeight())/2);
    frame.setVisible(true);
  }
  
}
  