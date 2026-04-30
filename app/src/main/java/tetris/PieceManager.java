package tetris;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import tetris.utility.TetrisConstants;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

/**
 * Pure Fabrication: manages the full lifecycle of piece sequencing for one
 * game round.
 *
 * Responsibilities:
 * - Determines which piece type comes next (from the properties sequence in
 * auto/test mode, or randomly in manual mode).
 * - Delegates piece construction to PieceFactory.
 * - Manages the preview piece shown in gameGrid2 (removes the old one,
 * creates and displays the new one).
 * - Assigns automated move sequences to pieces in auto/test mode.
 *
 * Tetris.java calls nextPiece() to obtain the next ready-to-place TetroPiece,
 * and reset() when a new game round starts.
 */
public class PieceManager {

  private final Tetris tetris;
  private final boolean isAuto;
  private final boolean isFeatOneActive;
  private final boolean isFeatTwoActive;
  private final Random random;

  private final BlockPieces[] blockPieces;
  private final String[] blockActions;

  private final Integer[] blockSpeeds;
  private int blockSpeedIndex = 0;

  private int blockPieceIndex = 0;
  private int blockActionIndex = 0;
  private TetroPiece blockPreview = null;

  public PieceManager(Tetris tetris, Properties properties, boolean isAuto, boolean isFeatOneActive, boolean isFeatTwoActive) {
    this.tetris = tetris;
    this.isAuto = isAuto;
    this.isFeatOneActive = isFeatOneActive;
    this.isFeatTwoActive = isFeatTwoActive;


    this.random = new Random(30006);

    String[] rawPieces = properties.getProperty("pieces", "").split(",");
    this.blockPieces = Arrays.stream(rawPieces)
        .map(BlockPieces::getBlockPiece)
        .toArray(BlockPieces[]::new);

    String[] rawSpeeds = properties.getProperty("speed", "").split(",");
    this.blockSpeeds = Arrays.stream(rawSpeeds).map(Integer::parseInt).toArray(Integer[]::new);

    this.blockActions = properties.getProperty("actions", "").split(",");
  }

  /**
   * Produces the next TetroPiece ready to be added to the game grid.
   * Also removes the previous preview and displays a new one in gameGrid2.
   */
  public void createNextPiece(GameGrid gameGrid) {
    TetroPiece piece = nextPiece();
    gameGrid.addActor(piece, randSpawnLoc(piece));
  }

  public TetroPiece nextPiece() {
    if (blockPreview != null)
      blockPreview.removeSelf();

    BlockPieces type = getNextBlockType();
    String moves = getNextAction();

    TetroPiece piece = PieceFactory.create(type, tetris);
    TetroPiece preview = PieceFactory.create(type, tetris);

    if (isAuto)
      piece.setAutoBlockMove(moves);

    // TODO: Check the feature flag as well
    // if (isFeatOneActive)

    if (isFeatTwoActive) piece.setFallSpeed(getNextSpeed());

    preview.display(tetris.gameGrid2, new Location(2, 1));
    blockPreview = preview;

    return piece;
  }

  public Location randSpawnLoc(TetroPiece piece) {
    int width = piece.relativeHighlightLocations.length; // x-span of bounding box
    Location loc;
    int maxAttempts = 200;

    do {
      int col = random.nextInt(TetrisConstants.GRID_WIDTH - width);
      int row = random.nextInt(TetrisConstants.SPAWN_ROWS);
      loc = new Location(col, row);
      maxAttempts--;
    } while (maxAttempts > 0 && isSpawnBlocked(piece, loc));

    return loc;
  }

  public void reset() {
    blockPieceIndex = 0;
    blockActionIndex = 0;
    blockSpeedIndex = 0;
  }

  // Private helpers

  private BlockPieces getNextBlockType() {
    if (blockPieceIndex < blockPieces.length)
      return blockPieces[blockPieceIndex++];
    return BlockPieces.values()[random.nextInt(7)];
  }

  private String getNextAction() {
    if (blockActionIndex < blockActions.length)
      return blockActions[blockActionIndex++];
    return "";
  }

  private int getNextSpeed() {
    if (blockSpeedIndex < blockSpeeds.length) {
      int s = blockSpeeds[blockSpeedIndex++];
      if (s >= 1 && s <= 3) return s;
    }

    return random.nextInt(3) + 1; // random: 1,2,3
  }

  private boolean isSpawnBlocked(TetroPiece piece, Location anchor) {
    // Check that each block's position one row below the spawn anchor is free
    for (TetroBlock block : piece.blocks) {
      Location nextLoc = new Location(
          anchor.x + block.getRelativeLocation(0).x,
          anchor.y + block.getRelativeLocation(0).y + 1);

      if (!tetris.gameGrid1.isInGrid(nextLoc))
        return true;

      if (tetris.gameGrid1.getOneActorAt(nextLoc, TetroBlock.class) != null)
        return true;

    }

    return false;
  }
}
