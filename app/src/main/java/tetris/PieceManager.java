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

  private final Location[] blockLocations;
  private int blockLocationIndex = 0;

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

    String speedProp = properties.getProperty("speed", "").trim();
    this.blockSpeeds = speedProp.isEmpty()
        ? new Integer[0]
        : Arrays.stream(speedProp.split(",")).map(Integer::parseInt).toArray(Integer[]::new);

    String locationsProp = properties.getProperty("locations", "").trim();
    if (locationsProp.isEmpty()) {
      this.blockLocations = new Location[0];
    } else {
      String[] rawLocs = locationsProp.split(";");
      this.blockLocations = Arrays.stream(rawLocs).map(s -> {
        String[] parts = s.split("-");
        return new Location(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
      }).toArray(Location[]::new);
    }

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

    if (isFeatTwoActive) piece.setFallSpeed(getNextSpeed());

    preview.display(tetris.gameGrid2, new Location(2, 1));
    blockPreview = preview;

    return piece;
  }

  public Location randSpawnLoc(TetroPiece piece) {
    // Feature 2 inactive: always spawn at the fixed centre-top position
    if (!isFeatTwoActive) {
      return new Location(TetrisConstants.DEFAULT_SPAWN_COL, TetrisConstants.DEFAULT_SPAWN_ROW);
    }

    // Feature 2 active, auto/test mode: use the predefined locations list
    if (isAuto && blockLocationIndex < blockLocations.length) {
      return blockLocations[blockLocationIndex++];
    }

    // Feature 2 active, manual mode (or locations exhausted): random spawn
    int width = piece.relativeHighlightLocations.length;
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
    blockLocationIndex = 0;
  }

  // Private helpers

  private static final BlockPieces[] EXTENDED_PIECES = { BlockPieces.CROSS, BlockPieces.PLUS, BlockPieces.SLASH };

  private boolean isExtendedPiece(BlockPieces type) {
    for (BlockPieces ep : EXTENDED_PIECES)
      if (ep == type) return true;
    return false;
  }

  private BlockPieces getNextBlockType() {
    while (blockPieceIndex < blockPieces.length) {
      BlockPieces type = blockPieces[blockPieceIndex++];
      if (!isFeatOneActive && isExtendedPiece(type))
        continue;
      return type;
    }
    return BlockPieces.values()[random.nextInt(isFeatOneActive ? 10 : 7)];
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
