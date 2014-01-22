import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Unit Cell of the Minesweeper game, also a JButton.
 * 
 * @author Daniel Rolandi
 * @version 5/17/2013
 */
public class Cell extends JButton implements ActionListener, MouseListener{
  private static final int CELL_SIZE = 22;
  private static final String CELL_PADDING = "     "; // 5 space characters
  private static final Insets CELL_MARGIN_0 = new Insets(0, 1, 0, 0); // Cell without icon
  private static final Insets CELL_MARGIN_1 = new Insets(2, 2, 2, 2); // Cell with icon
  private static final int MINED = -1;
  private static final ImageIcon FLAG_ICON = new ImageIcon("images/flag.png");
  private static final ImageIcon MINE_ICON = new ImageIcon("images/mine.png");
  private static final ImageIcon FALSE_ICON = new ImageIcon("images/false.png");
  private static final int SIMULCLICK_DELAY = 50; // in milliseconds
  
  private static final Color[] NUMBER_COLOR = {
    /*0*/ Color.BLACK,
    /*1*/ Color.BLUE,
    /*2*/ Color.GREEN,
    /*3*/ Color.RED,
    /*4*/ Color.ORANGE,
    /*5*/ Color.CYAN,
    /*6*/ Color.YELLOW,
    /*7*/ Color.DARK_GRAY,
    /*8*/ Color.MAGENTA
  };
  
  private GameBoard gameBoard;
  private boolean isRevealed;
  private boolean isFlagged;
  private int minesCount;
  private int row;
  private int col;  
  // to detect simultaneous clicks
  private Timer leftReleased;
  private Timer rightReleased;
  private boolean leftWasReleased;
  // to control depressed buttons
  private static boolean leftPressed;
  private static boolean rightPressed;
  // to position actual cursor release
  private static int cursorRow;
  private static int cursorCol;
  
  /**
   * Starts a default Cell.
   * @param gb Data center.
   * @param row Row position of this Cell.
   * @param col Col position of this Cell.
   * @throws IllegalArgumentException If supplied GameBoard is null.
   */
  public Cell(GameBoard gb, int row, int col){
    super(CELL_PADDING);
    if(gb == null){
      throw new IllegalArgumentException("Expected game board.");
    }
    gameBoard = gb;
    isRevealed = false;
    isFlagged = false;
    minesCount = 0;
    this.row = row;
    this.col = col;
    leftReleased = new Timer(SIMULCLICK_DELAY, this);
    leftReleased.setRepeats(false);
    rightReleased = new Timer(SIMULCLICK_DELAY, this);
    rightReleased.setRepeats(false);
    leftWasReleased = true; // doesn't matter much
    leftPressed = false;
    rightPressed = false;
    
    setSize(CELL_SIZE, CELL_SIZE);
    setMargin(CELL_MARGIN_0);    
    addMouseListener(this);
  }  
  
  /**
   * Returns true if this Cell is revealed.
   * @return Rrue if this Cell is revealed.
   */
  public boolean isRevealed(){
    return isRevealed;
  }
  
  /**
   * Returns true if this Cell is flagged.
   * @return Rrue if this Cell is flagged.
   */
  public boolean isFlagged(){
    return isFlagged;
  }
  
  /**
   * Returns true if this Cell is mined.
   * @return True if this Cell is mined.
   */
  public boolean isMined(){
    return minesCount == MINED;
  }
  
  /**
   * Sets this Cell to mined.
   * @throws IllegalStateException If it is mined or assigned a number.
   */
  public void setMined(){
    if(minesCount == MINED){
      throw new IllegalStateException("Cell already mined.");
    }
    minesCount = MINED;
  }    
  
  /**
   * Adds one to mines count of this Cell.
   * @throws IllegalStateException If trying to add mine to a mined Cell.
   */
  public void addMineCount(){
    if( isMined() ){
      throw new IllegalStateException("Cell already mined.");
    }
    minesCount++;
  }
  
  /**
   * Returns the count of mines in the neighbors.
   * @return The count of mines in the neighbors.
   */
  public int getMinesCount(){
    return minesCount;
  }
  
  /**
   * Deals with setting icons, including margin.
   * @param icon ImageIcon to use.
   */
  public void putIcon(ImageIcon icon){
    setText("");
    setIcon(icon);
    setMargin(CELL_MARGIN_1);
  }
  
  /** Removes ImageIcon and blanks this Cell, including margin. */
  public void putBlankIcon(){
    setText(CELL_PADDING);
    setIcon(null);
    setMargin(CELL_MARGIN_0);
  }
  
  /**
   * Sets flag to this Cell.
   */
  public void flag(){
    isFlagged = true;
    putIcon(FLAG_ICON); 
    gameBoard.totalFlagsCount++;
    if( isMined() ){
      gameBoard.flaggedMinesCount++;
    }
    gameBoard.notifyViewers();
    gameBoard.judge();
  }
  
  /**
   * Remove flag from this Cell.   
   */
  private void deflag(){
    isFlagged = false;
    putBlankIcon();    
    gameBoard.totalFlagsCount--;
    if( isMined() ){
      gameBoard.flaggedMinesCount--;
    }
    gameBoard.notifyViewers();
  }
  
  /** Opens this Cell by disabling the JButton. */
  public void reveal(){
    if(gameBoard.isPlaying && isMined()){
      setContentAreaFilled(true);
      setBackground( Color.RED );
      revalidate();
      repaint();
      gameBoard.defeat();
    }else{
      isRevealed = true;
      // reduce statistics for victory judgment purposes
      gameBoard.reduceRemainingCells();
      // remove color and prevent event calls
      if(! gameBoard.isGameOver() ){
        setContentAreaFilled(false);
        disableEvents(AWTEvent.MOUSE_EVENT_MASK);
      }
      // puts icon for false attempts
      if( gameBoard.isGameOver() && isFlagged && !isMined() ){
        deflag();
        putIcon(FALSE_ICON);
      }else if( isMined() ){
        // show mine if it's a mine not under a flag
        if(!isFlagged){
          putIcon(MINE_ICON);
        }
      }else if( minesCount == 0){
        // expansion if there's no nearby mines
        gameBoard.expand(row, col);
      }else{
        // show number of surrounding mines
        setForeground(NUMBER_COLOR[minesCount]);
        setText("" + minesCount);
      }
      gameBoard.notifyViewers();
      gameBoard.judge();
    }
  }
  
  /**
   * Handles events from Timer of clicks.
   * @param e Events from Timer of clicks.
   */
  @Override
  public void actionPerformed(ActionEvent e){    
    // detecting click from LEFT button    
    if(!leftReleased.isRunning() && !rightReleased.isRunning()){
      // must have been clicked by a Timer
      // use regular lone clicks
      if(leftWasReleased){
        leftReleased();
      }else{
        rightReleased();
      }
    }
  }    
  
  /** Handles a lone left click. */
  public void leftReleased(){
    if(rightPressed){
      return;
    }    
    if(! gameBoard.isPlaying) gameBoard.isPlaying = true;          
    if(!isFlagged && !isRevealed){
      gameBoard.getCell(cursorRow, cursorCol).reveal();     
    }
  }
  
  /** Handles a lone right click. */
  public void rightReleased(){
    if(leftPressed){
      return;
    }    
    if(! isRevealed){
      if(! isFlagged){
        gameBoard.getCell(cursorRow, cursorCol).flag();
      }else{
        gameBoard.getCell(cursorRow, cursorCol).deflag();
      }
    }
  }
  
  /** Handles a simultaneous left click and right click. */
  public void simulReleased(){    
    gameBoard.wideReveal(cursorRow, cursorCol); // precondition in that method
  }
  
  /**
   * Attempts to depress surrounding buttons.
   * @param e Left or right press.
   */
  public void mousePressed(MouseEvent e){
    if(e.getButton() == MouseEvent.BUTTON1){
      leftPressed = true;
    }else if(e.getButton() == MouseEvent.BUTTON3){
      rightPressed = true;
    }
    if(leftPressed && rightPressed){
      gameBoard.depress(cursorRow, cursorCol);
    }
  }
  
  /**
   * Releases button flags and un-depress buttons.
   * @param e Left or right depress.
   */
  public void mouseReleased(MouseEvent e){
    if(e.getButton() == MouseEvent.BUTTON1){
      leftPressed = false;
    }else if(e.getButton() == MouseEvent.BUTTON3){
      rightPressed = false;
    }
    gameBoard.undepress(cursorRow, cursorCol);
    
    // part to handle simultaneous release
    if(e.getButton() == MouseEvent.BUTTON1){
      // detecting click from LEFT button
      if(rightReleased.isRunning()){
        leftReleased.stop();
        rightReleased.stop();
        simulReleased();
      }else{
        leftReleased.start();
      }
      leftWasReleased = true;
    }else if(e.getButton() == MouseEvent.BUTTON3){
      // detecting click from RIGHT button
      if(leftReleased.isRunning()){
        leftReleased.stop();
        rightReleased.stop();
        simulReleased();
      }else{
        rightReleased.start();
      }
      leftWasReleased = false;
    }
  }
  
  /* Attempts to depress upon entry to a Cell. */
  public void mouseEntered(MouseEvent e){
    cursorRow = row;
    cursorCol = col;
    if(leftPressed && rightPressed){
      gameBoard.depress(cursorRow, cursorCol);
    }
  }
  
  /** Undepresses if Cell is exited from. */
  @Override
  public void mouseExited(MouseEvent e){
    gameBoard.undepress(cursorRow, cursorCol);
  }
  
  /** Empty implementation. */
  @Override
  public void mouseClicked(MouseEvent e){    
  }
  
}