package tetris;

import ch.aplu.jgamegrid.*;
import java.util.ArrayList;

public abstract class TetroPiece extends Actor {

  protected Tetris tetris;

  // Shared state across all piece types
  protected boolean isStarting = true;
  protected int rotationId = 0;
  protected int nb;
  protected ArrayList<TetroBlock> blocks = new ArrayList<>();
  protected Actor nextTetrisBlock = null;
  protected String autoBlockMove = "";
  protected int autoBlockIndex = 0;

  // Highlight arrays: dimensions vary per piece, so subclass constructors
  // must initialise both before the first act() call.
  protected Location[][] relativeHighlightLocations;
  protected Location[][] absoluteHighlightLocations;

  protected TetroPiece(Tetris tetris) {
    super();
    this.tetris = tetris;
  }

  /** Each concrete piece returns a descriptive string for logging/testing. */
  public abstract String toString();

  public void setAutoBlockMove(String autoBlockMove) {
    this.autoBlockMove = autoBlockMove;
  }

  /**
   * Check if the block can be played automatically based on the properties file
   */
  private boolean canAutoPlay() {
    return autoBlockMove != null
        && !autoBlockMove.equals("")
        && autoBlockMove.length() > autoBlockIndex;
  }

  /**
   * Based on the input in the properties file, the block can move automatically
   */
  private void autoMove() {
    String moveString = autoBlockMove.substring(autoBlockIndex, autoBlockIndex + 1);
    switch (moveString) {
      case "L":
        left();
        break;
      case "R":
        right();
        break;
      case "T":
        rotate();
        break;
      default:
        break;
    }
    autoBlockIndex++;
  }

  // ====== Main game loop entry point =====
  /**
   * The game is called in a run loop, this method for a block is called every
   * 1/30 seconds as the starting point
   */

  public void act() {
    if (isStarting) {
      // Place each TetroBlock onto the grid at its initial relative position
      for (TetroBlock a : blocks) {
        Location loc = new Location(
            getX() + a.getRelativeLocation(0).x,
            getY() + a.getRelativeLocation(0).y);
        gameGrid.addActor(a, loc);
      }
      hightlightLocation(true);
      isStarting = false;
      nb = 0;
    } else if (canAutoPlay()) {
      autoMove();
    } else {
      setDirection(90); // fall downward
      if (nb == 1)
        nextTetrisBlock = tetris.createRandomTetrisBlock();

      if (!advance()) {
        if (nb == 0) // piece couldn't move at all → game over
          tetris.gameOver();
        else {
          setActEnabled(false);
          gameGrid.addActor(nextTetrisBlock, new Location(6, 0));
          tetris.moveToNextTetris(nextTetrisBlock);
        }
      }
      nb++;
    }

    if (nb == 4) {
      hightlightLocation(false);
    }
  }

  // ====== Player action =====

  void left() {
    if (isStarting)
      return;
    setDirection(180);
    advance();
  }

  void right() {
    if (isStarting)
      return;
    setDirection(0);
    advance();
  }

  // Rotate 90 deg clockwise. Subclasses that cannot rotate should override this
  // as a no-op.
  void rotate() {
    if (isStarting)
      return;

    int oldRotationId = rotationId;
    rotationId++;
    if (rotationId == 4)
      rotationId = 0;

    if (canRotate(rotationId)) {
      for (TetroBlock a : blocks) {
        Location loc = new Location(
            getX() + a.getRelativeLocation(rotationId).x,
            getY() + a.getRelativeLocation(rotationId).y);
        a.setLocation(loc);
      }
    } else {
      rotationId = oldRotationId; // restore if rotation is blocked
    }
  }

  void drop() {
    if (isStarting)
      return;
    tetris.speedup();
  }

  // ====== Diplay helper ======

  void display(GameGrid gg, Location location) {
    for (TetroBlock a : blocks) {
      Location loc = new Location(
          location.x + a.getRelativeLocation(0).x,
          location.y + a.getRelativeLocation(0).y);
      gg.addActor(a, loc);
    }
  }

  // ====== Internal movement ======

  private boolean canRotate(int rotId) {
    for (TetroBlock a : blocks) {
      int locationX = getX() + a.getRelativeLocation(rotId).x;
      int locationY = getY() + a.getRelativeLocation(rotId).y;
      Location loc = new Location(locationX, locationY);
      TetroBlock block = (TetroBlock) gameGrid.getOneActorAt(loc, TetroBlock.class);

      if (!tetris.isInsideBoundary(loc))
        return false; // outside grid
      if (blocks.contains(block))
        continue; // own block — skip
      if (block != null)
        return false; // occupied by another piece
    }
    return true;
  }

  private boolean advance() {
    boolean canMove = false;
    for (TetroBlock a : blocks) {
      if (!a.isRemoved())
        canMove = true;
    }
    for (TetroBlock a : blocks) {
      if (a.isRemoved())
        continue;
      if (!gameGrid.isInGrid(a.getNextMoveLocation())) {
        canMove = false;
        break;
      }
    }
    for (TetroBlock a : blocks) {
      if (a.isRemoved())
        continue;
      TetroBlock block = (TetroBlock) gameGrid.getOneActorAt(
          a.getNextMoveLocation(), TetroBlock.class);
      if (block != null && !blocks.contains(block)) {
        canMove = false;
        break;
      }
    }

    if (canMove) {
      move();
      return true;
    }
    return false;
  }

  /**
   * Turn on and off highlight to show the surrounding rectangle of a block
   * 
   * @param isHighlight turn on or off the highlight
   */
  protected void hightlightLocation(boolean isHighlight) {
    for (int i = 0; i < relativeHighlightLocations.length; i++) {
      for (int j = 0; j < relativeHighlightLocations[i].length; j++) {
        if (isHighlight) {
          Location hl = relativeHighlightLocations[i][j];
          absoluteHighlightLocations[i][j] = new Location(getX() + hl.getX(), getY() + hl.getY());
        }
        tetris.highlightLocations(absoluteHighlightLocations[i][j], isHighlight);
      }
    }
  }

  // ====== Actor overrides - propagate movement/removal to child TetroBlocks
  // ======

  @Override
  public void setDirection(double dir) {
    super.setDirection(dir);
    for (TetroBlock a : blocks)
      a.setDirection(dir);
  }

  @Override
  public void move() {
    if (isRemoved())
      return;
    super.move();
    for (TetroBlock a : blocks) {
      if (a.isRemoved())
        break;
      a.move();
    }
  }

  @Override
  public void removeSelf() {
    super.removeSelf();
    for (TetroBlock a : blocks)
      a.removeSelf();
  }
}
