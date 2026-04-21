package tetris;

import ch.aplu.jgamegrid.Location;
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
  private final Random random;

  private final BlockPieces[] blockPieces;
  private final String[] blockActions;

  private int blockPieceIndex = 0;
  private int blockActionIndex = 0;
  private TetroPiece blockPreview = null;

  public PieceManager(Tetris tetris, Properties properties, boolean isAuto) {
    this.tetris = tetris;
    this.isAuto = isAuto;
    this.random = new Random(30006);

    String[] rawPieces = properties.getProperty("pieces", "").split(",");
    this.blockPieces = Arrays.stream(rawPieces)
        .map(BlockPieces::getBlockPiece)
        .toArray(BlockPieces[]::new);

    this.blockActions = properties.getProperty("actions", "").split(",");
  }

  /**
   * Produces the next TetroPiece ready to be added to the game grid.
   * Also removes the previous preview and displays a new one in gameGrid2.
   */
  public TetroPiece nextPiece() {
    if (blockPreview != null)
      blockPreview.removeSelf();

    BlockPieces type = getNextBlockType();
    String moves = getNextAction();

    TetroPiece piece = PieceFactory.create(type, tetris);
    TetroPiece preview = PieceFactory.create(type, tetris);

    if (isAuto)
      piece.setAutoBlockMove(moves);

    preview.display(tetris.gameGrid2, new Location(2, 1));
    blockPreview = preview;

    return piece;
  }

  public void reset() {
    blockPieceIndex = 0;
    blockActionIndex = 0;
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
}
