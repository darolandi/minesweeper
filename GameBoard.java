import javax.swing.*;
import java.awt.*;
import java.util.*; // List, Random
import java.util.List;
import java.util.ArrayList;

/**
 * Keeps and updates data of the game.
 * 
 * @author Daniel Rolandi
 * @version 5/17/2013
 */
public class GameBoard{
  private static final int ROW_MIN = 9;
  private static final int ROW_MAX = 30;
  private static final int COL_MIN = 9;
  private static final int COL_MAX = 30;
  private static final int MINES_MIN = 5;
  private static final int MINES_MAX = 200;
  
  // beginner difficulty
  private static final int ROW_0 = ROW_MIN;
  private static final int COL_0 = COL_MIN;
  private static final int MINES_0 = 10;
  // intermediate difficulty
  private static final int ROW_1 = 16;
  private static final int COL_1 = 16;
  private static final int MINES_1 = 40;
  // expert difficulty
  private static final int ROW_2 = 16;
  private static final int COL_2 = 30;
  private static final int MINES_2 = 99;
  // default difficulty to start the game
  private static final Difficulty DEFAULT_DIFFICULTY = Difficulty.BEGINNER;
  
  private JFrame frame;
  private int row;
  private int col;
  private int totalMinesCount;  
  private int remainingCells;
  private Cell[][] gameCell;
  private JPanel minefield;
  private boolean isGameOver;
  private Difficulty difficulty;
  private List<GameViewer> viewers;
  
  public boolean isPlaying;
  public int totalFlagsCount;
  public int flaggedMinesCount;
  public double timeElapsed;
  
  /**
   * Starts the GameBoard (underlying model, data center).
   * @param minefield JPanel containing the JButtons.
   * @throws IllegalArgumentException If supplied JFrame is null.
   * @throws IllegalArgumentException If supplied JPanel is null.
   */
  public GameBoard(JFrame frame, JPanel minefield){ 
    if(frame == null){
      throw new IllegalArgumentException("Expected parent JFrame.");
    }
    if(minefield == null){
      throw new IllegalArgumentException("Expected minefield.");
    }
    this.frame = frame;
    this.minefield = minefield;
    viewers = new ArrayList<GameViewer>();
    
    // for now, default with Beginner difficulty
    // in the future, make it possible to load previous settings
    difficulty = DEFAULT_DIFFICULTY;
    newGame();
  }
  
  /**
   * Registers the viewer with the GameBoard.
   * @throws IllegalArgumentException If given null argument.
   */
  public void addViewer(GameViewer v){
    if(v == null){
      throw new IllegalArgumentException("Expected non-null viewer.");
    }
    viewers.add(v);
  }
  
  /** Notifies all viewers to update themselves. */
  public void notifyViewers(){
    for(GameViewer v : viewers){
      v.update();
    }
  }
  
  /**
   * Sets difficulty of the game.
   * @param d Desired difficulty.
   */
  public void setDifficulty(Difficulty d){
    difficulty = d;
  }
  
  /**
   * Returns total mines count.
   * @return Total mines count.
   */
  public int getTotalMinesCount(){
    return totalMinesCount;
  }
  
  /**
   * Returns total flags count.
   * @return Total flags count.
   */
  public int getTotalFlagsCount(){
    return totalFlagsCount;
  }
  
  /* Refills and sets the array of JButtons inside the minefield. */
  private void generateMinefield(){
    for(int r = 0; r < row; r++){
      for(int c = 0; c < col; c++){
        Cell tempCell = new Cell(this, r, c);        
        tempCell.setFocusable(false);
        gameCell[r][c] = tempCell;        
        minefield.add(tempCell);
      }
    }    
    frame.pack();
    fillMinefield();
  }
  
  /* Removes the minefield and regenerates it. */
  private void resetMinefield(){
    minefield.removeAll();    
    minefield.revalidate();    
    generateMinefield();
  }
  
  /** Fills the minefield with randomized mines, also assigns numbers. */
  private void fillMinefield(){
    int[] numberTable = new int[row * col];
    for(int i = 0; i < numberTable.length; i++){
      numberTable[i] = i;
    }
    // pick random number
    Random random = new Random();
    int upperBound = numberTable.length;
    for(int m = 0; m < totalMinesCount; m++){
      int pickIndex = random.nextInt(upperBound);
      int pick = numberTable[pickIndex];
      upperBound--;
      // swap to unused index
      int temp = numberTable[pickIndex];
      numberTable[pickIndex] = numberTable[upperBound];
      numberTable[upperBound] = temp;      
      // mine the picked location      
      setCellMined(pick / col, pick % col);
    }
  }
  
  /**
   * Returns Cell at that position.
   * @param row Row position to retrieve Cell.
   * @param col Col position to retrieve Cell.
   * @throws IllegalArgumentException If not valid position.
   * @return Cell at that position.
   */
  public Cell getCell(int row, int col){
    if(! isValidCell(row, col) ){
      throw new IllegalArgumentException("Out of game bounds.");
    }
    return gameCell[row][col];
  }
  
  /**
   * Returns a List of Cells that surround this row,col position.
   */
  private List<Cell> getNeighborCells(int row, int col){
    List<Cell> neighborList = new ArrayList<Cell>(8);
    
    if( isValidCell(row-1, col-1) ) // top-left
      neighborList.add(gameCell[row-1][col-1]);
    if( isValidCell(row-1, col  ) ) // top
      neighborList.add(gameCell[row-1][col  ]);
    if( isValidCell(row-1, col+1) ) // top-right
      neighborList.add(gameCell[row-1][col+1]);
    if( isValidCell(row  , col-1) ) // left
      neighborList.add(gameCell[row  ][col-1]);
    if( isValidCell(row  , col+1) ) // right
      neighborList.add(gameCell[row  ][col+1]);
    if( isValidCell(row+1, col-1) ) // bot-left
      neighborList.add(gameCell[row+1][col-1]);
    if( isValidCell(row+1, col  ) ) // bot
      neighborList.add(gameCell[row+1][col  ]);
    if( isValidCell(row+1, col+1) ) // bot-right
      neighborList.add(gameCell[row+1][col+1]);
    
    return neighborList;
  }
  
  /** Lays down a mine and increment neighbor Cells. */
  private void setCellMined(int row, int col){
    gameCell[row][col].setMined();
    
    List<Cell> neighborList = getNeighborCells(row, col);
    for(Cell neighbor : neighborList){
      if(! neighbor.isMined()){
        neighbor.addMineCount();
      }
    }
  }
  
  /* Checks if Cell is within game bounds. */
  private boolean isValidCell(int row, int col){    
    return (0 <= row) && (row < getRow()) && (0 <= col) && (col < getCol());
  }
   
  /**
   * Returns the number of rows.
   * @return The number of rows.
   */
  public int getRow(){
    /* This is used to distinguish the "row" in parameters. */
    return row;
  }
  
  /**
   * Returns the number of cols.
   * @return The number of cols.
   */
  public int getCol(){
    /* This is used to distinguish the "col" in parameters. */
    return col;
  }
  
  /**
   * Returns true if game is over.
   * @return True if game is over.
   */
  public boolean isGameOver(){
    return isGameOver;
  }
  
  /**
   * Reduces remaining Cells by 1.   
   * @throws IllegalStateException If this causes remainingCells to be < 0.
   */
  public void reduceRemainingCells(){    
    if(remainingCells - 1 < 0){
      throw new IllegalStateException("Too many reductions.");
    }
    remainingCells -= 1;
  }    
  
  /**
   * Special algorithm in favor of player to expand free Cells.
   * @param row Row position to start expand.
   * @param col Col position to start expand.
   * @throws IllegalArgumentException If not valid position.
   */
  public void expand(int row, int col){
    if(! isValidCell(row, col) ){
      throw new IllegalArgumentException("Out of game bounds.");
    }
    
    List<Cell> neighborList = getNeighborCells(row, col);
    for(Cell neighbor : neighborList){
      if(!neighbor.isRevealed() && !neighbor.isFlagged()){
        neighbor.reveal();
      }
    }
    
  }
  
  /**
   * Special move triggered by L+R click; attempts to reveal 3x3 grid.
   * @param row Row position to start expand.
   * @param col Col position to start expand.
   * @throws IllegalArgumentException If not valid position.
   */
  public void wideReveal(int row, int col){
    if(! isValidCell(row, col) ){
      throw new IllegalArgumentException("Out of game bounds.");
    }
    if(! gameCell[row][col].isRevealed()){
      return;
    }
    // wideReveal only executes if there's exactly N flags in 3x3 grid
    // when it executes,
    // reveal any unflagged Cell (doing the guess)
    // ignore the flagged Cell (will be counted as false)
    
    // counting number of flags
    // assumption here is any revealed Cell is never flagged
    int countFlag = 0;    
    List<Cell> neighborList = getNeighborCells(row, col);
    for(Cell neighbor : neighborList){
      if(neighbor.isFlagged()){
        countFlag++;
      }
    }      
    if(countFlag == gameCell[row][col].getMinesCount()){
      for(Cell neighbor : neighborList){
        if(!neighbor.isRevealed() && !neighbor.isFlagged()){
          neighbor.reveal();
        }
      }
    }
  }
  
  /**
   * Depresses the surrounding 3x3 grid.
   * @param row Row position to start depress.
   * @param col Col position to start depress.
   * @throws IllegalArgumentException If not valid position.
   */
  public void depress(int row, int col){
    if(! isValidCell(row, col) ){
      throw new IllegalArgumentException("Out of game bounds.");
    }
    List<Cell> neighborList = getNeighborCells(row, col);
    // special, because it must depress itself
    neighborList.add(gameCell[row][col]);
    for(Cell neighbor : neighborList){
        if(!neighbor.isRevealed() && !neighbor.isFlagged()){
          neighbor.setContentAreaFilled(false);
        }
      }
  }
  
  /**
   * Undepresses the surrounding 3x3 grid.
   * @param row Row position to start undepress.
   * @param col Col position to start undepress.
   * @throws IllegalArgumentException If not valid position.
   */
  public void undepress(int row, int col){
    if(! isValidCell(row, col) ){
      throw new IllegalArgumentException("Out of game bounds.");
    }
    List<Cell> neighborList = getNeighborCells(row, col);
    // special, because it must depress itself
    neighborList.add(gameCell[row][col]);
    for(Cell neighbor : neighborList){
        if(!neighbor.isRevealed() && !neighbor.isFlagged()){
          neighbor.setContentAreaFilled(true);
        }
      }
  }
  
  /** Handles creating a new game, also resets. */
  public void newGame(){
    switch(difficulty){
      case BEGINNER:
        row = ROW_0;
        col = COL_0;
        totalMinesCount = MINES_0;
        break;
      case INTERMEDIATE:
        row = ROW_1;
        col = COL_1;
        totalMinesCount = MINES_1;
        break;
      case EXPERT:
        row = ROW_2;
        col = COL_2;
        totalMinesCount = MINES_2;
        break;
    }    
    remainingCells = row * col;    
    
    isPlaying = false;
    isGameOver = false;
    totalFlagsCount = 0;
    flaggedMinesCount = 0;
    timeElapsed = 0.0;
    gameCell = new Cell[row][col];
        
    minefield.setLayout(new GridLayout(row, col));
    notifyViewers();
    frame.revalidate();    
    resetMinefield();
  }
  
  /** Checks if victory condition is met. */
  public void judge(){
    if(isGameOver) return;
    if(remainingCells == totalMinesCount){
      victory();
      return;
    }
    if(flaggedMinesCount == totalMinesCount     // must put flags on mines
         && totalFlagsCount == totalMinesCount  // prevents players from guessing
         && remainingCells == totalMinesCount){ // must reveal all remaining Cells
      victory();
      return;
    }    
  }
  
  /** Handles victorious game. */
  public void victory(){
    isGameOver = true;
    isPlaying = false;
    notifyViewers();
    for(int r = 0; r < row; r++){
      for(int c = 0; c < col; c++){
        if(gameCell[r][c].isMined() && !gameCell[r][c].isRevealed() && !gameCell[r][c].isFlagged() ){
          gameCell[r][c].flag();
        }
      }
    }
    String timeElapsedFormatted = String.format("%.2f", timeElapsed);
    int answer = JOptionPane.showConfirmDialog(frame, "You won in " + timeElapsedFormatted + " seconds!\nPlay again?",
                                               "Victory!", JOptionPane.YES_NO_OPTION);
    if(answer == JOptionPane.YES_OPTION){
      newGame();
    }else{
      System.exit(0);
    }
  }
  
  /** Handles defeated game. */
  public void defeat(){
    isGameOver = true;
    isPlaying = false;
    notifyViewers();
    for(int r = 0; r < row; r++){
      for(int c = 0; c < col; c++){
        if((gameCell[r][c].isFlagged() || gameCell[r][c].isMined()) && !gameCell[r][c].isRevealed() ){
          gameCell[r][c].reveal();
        }
      }
    }
    int answer = JOptionPane.showConfirmDialog(frame, "You lost!\nPlay again?", "Defeat!", JOptionPane.YES_NO_OPTION);
    if(answer == JOptionPane.YES_OPTION){
      newGame();
    }else{
      System.exit(0);
    }
  }
  
}