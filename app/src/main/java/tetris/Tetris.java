package tetris;// Tetris.java

import ch.aplu.jgamegrid.*;
import tetris.utility.Logger;
import tetris.utility.TetrisConstants;

import java.util.ArrayList;
import java.util.Properties;
import java.awt.event.KeyEvent;
import java.awt.*;
import javax.swing.*;

public class Tetris extends JFrame implements GGActListener {
  public static final String statisticsFilePath = "../Statistics.txt";
  private TetroPiece currentBlock = null;
  private int score = 0;

  private StatisticsRecorder statisticsRecorder;

  private boolean isAuto = false;
  private final boolean isFeatOneActive;
  private final boolean isFeatTwoActive;

  protected PieceManager pieceManager;

  Logger logger = new Logger();

  public Tetris(Properties properties) {
    this.isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
    this.isFeatOneActive = toFeatureFlag(properties.getProperty("features.1"));
    this.isFeatTwoActive = toFeatureFlag(properties.getProperty("features.2"));
    this.pieceManager = new PieceManager(this, properties, isAuto, isFeatOneActive, isFeatTwoActive);

    // Set up the UI components. No need to modify the UI Components
    tetrisComponents = new TetrisComponents();
    tetrisComponents.initComponents(this);

    statisticsRecorder = new StatisticsRecorder(statisticsFilePath);
    gameGrid1.addActListener(this);

    gameGrid1.setSimulationPeriod(defaultSimulationPeriod());

    // Add the first block to start
    currentBlock = pieceManager.nextPiece();
    gameGrid1.addActor(currentBlock, pieceManager.randSpawnLoc(currentBlock));
    gameGrid1.doRun();

    // Do not lose keyboard focus when clicking this window
    gameGrid2.setFocusable(false);
    setTitle("SWEN30006 Tetris Madness");
    score = 0;
    showScore(score);
  }

  private boolean toFeatureFlag(String value) {
    return value != null
            && !value.trim().isEmpty()
            && !value.equalsIgnoreCase("inactive");
  }

  /**
   * The game is called in a run loop, this method sleeps for 500 milliseconds
   * and only check if the game is over after 500 milliseconds
   */
  public String runApp() {
    setVisible(true);
    while (gameGrid1.isRunning()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return logger.getAllLog();
  }

  /**
   * Delegates to PieceManager to obtain the next ready-to-place TetroPiece.
   * Called by TetroPiece.act() when it needs to queue up the following piece.
   *
   * @return the next TetroPiece, fully configured and previewed
   */
  public TetroPiece nextPiece() {
    return pieceManager.nextPiece();
  }

  /**
   * highlight for a specific location. Check and use the appropriate color
   * 
   * @param location
   * @param highlight
   */
  void highlightLocations(Location location, boolean highlight) {
    if (highlight) {
      gameGrid1.getBg().fillCell(location, Color.YELLOW);
    } else {
      gameGrid1.setGridColor(new java.awt.Color(255, 3, 0));
    }
  }

  private int defaultSimulationPeriod() {
    return isAuto ? TetrisConstants.AUTO_SIMULATION_PERIOD : TetrisConstants.MANUAL_SIMULATION_PERIOD;
  }

  public void moveToNextTetris(TetroPiece t) {
    statisticsRecorder.recordPiece(currentBlock.getBlockType());
    currentBlock = t;
    gameGrid1.setSimulationPeriod(defaultSimulationPeriod());
  }

  public void speedup() {
    gameGrid1.setSimulationPeriod(TetrisConstants.MANUAL_DROP_SIMULATION_PERIOD);
  }

  /**
   * Check if a specific location is within the game grid
   * 
   * @param location
   * @return
   */
  public boolean isInsideBoundary(Location location) {
    if (location.getX() < 0) {
      return false;
    }

    if (location.getX() >= gameGrid1.getNbHorzCells()) {
      return false;
    }
    return true;
  }

  /**
   * Handle user input to move block. Arrow left to move left,
   * Arrow right to move right, Arrow up to rotate and
   * Arrow down for going down
   */
  private void moveBlock(int keyEvent) {
    switch (keyEvent) {
      case KeyEvent.VK_UP:
        currentBlock.rotate();
        break;
      case KeyEvent.VK_LEFT:
        currentBlock.left();
        break;
      case KeyEvent.VK_RIGHT:
        currentBlock.right();
        break;
      case KeyEvent.VK_DOWN:
        currentBlock.drop();
        break;
    }
  }

  /**
   * The game is called in a run loop, this method for a tetris is called every
   * 1/30 seconds as the starting point
   */
  public void act() {
    removeFilledLine();
    moveBlock(gameGrid1.getKeyCode());
    logger.logEvent(currentBlock.toString());
  }

  /**
   * Check if a line is completely filled and clear the line and update the score
   */
  private void removeFilledLine() {
    for (int y = 0; y < gameGrid1.nbVertCells; y++) {
      boolean isLineComplete = true;
      TetroBlock[] blocks = new TetroBlock[gameGrid1.nbHorzCells]; // One line
      // Calculate if a line is complete
      for (int x = 0; x < gameGrid1.nbHorzCells; x++) {
        blocks[x] = (TetroBlock) gameGrid1.getOneActorAt(new Location(x, y), TetroBlock.class);
        if (blocks[x] == null) {
          isLineComplete = false;
          break;
        }
      }
      if (isLineComplete) {
        // If a line is complete, we remove the component block of the shape that
        // belongs to that line
        for (int x = 0; x < gameGrid1.nbHorzCells; x++)
          gameGrid1.removeActor(blocks[x]);
        ArrayList<Actor> allBlocks = gameGrid1.getActors(TetroBlock.class);
        for (Actor a : allBlocks) {
          int z = a.getY();
          if (z < y)
            a.setY(z + 1);
        }
        gameGrid1.refresh();
        score++;
        showScore(score);
        logger.logEvent("Score: " + score);
      }
    }
  }

  /**
   * Show Score
   */
  private void showScore(final int score) {
    scoreText.setText(score + " points");
  }

  /**
   * Display the game over
   */
  void gameOver() {
    statisticsRecorder.recordPiece(currentBlock.getBlockType());
    statisticsRecorder.finaliseRound(score);
    gameGrid1.addActor(new Actor("sprites/gameover.gif"), new Location(5, 5));
    gameGrid1.doPause();
    if (isAuto) {
      gameGrid1.doPause();
    }
  }

  /**
   * Start a new game
   */
  public void startBtnActionPerformed(java.awt.event.ActionEvent evt) {
    statisticsRecorder.startNewRound();
    gameGrid1.doPause();
    gameGrid1.removeAllActors();
    gameGrid2.removeAllActors();
    gameGrid1.refresh();
    gameGrid2.refresh();
    gameGrid2.delay(getDelayTime());
    pieceManager.reset();
    currentBlock = pieceManager.nextPiece();
    gameGrid1.addActor(currentBlock, pieceManager.randSpawnLoc(currentBlock));
    gameGrid1.doRun();
    gameGrid1.requestFocus();
    score = 0;
    showScore(score);
  }

  private int getDelayTime() {
    if (isAuto) {
      return 500;
    } else {
      return 2000;
    }
  }

  // AUTO GENERATED - do not modify//GEN-BEGIN:variables
  public ch.aplu.jgamegrid.GameGrid gameGrid1;
  public ch.aplu.jgamegrid.GameGrid gameGrid2;
  public javax.swing.JPanel jPanel1;
  public javax.swing.JPanel jPanel2;
  public javax.swing.JPanel jPanel3;
  public javax.swing.JPanel jPanel4;
  public javax.swing.JScrollPane jScrollPane1;
  public javax.swing.JTextArea jTextArea1;
  public javax.swing.JTextField scoreText;
  public javax.swing.JButton startBtn;
  private TetrisComponents tetrisComponents;
  // End of variables declaration//GEN-END:variables

}
