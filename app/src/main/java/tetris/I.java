// I.java
package tetris;

import ch.aplu.jgamegrid.*;

class I extends TetroPiece {
  private final BlockPieces blockPiece = BlockPieces.I;

  I(Tetris tetris) {
    super(tetris);
    relativeHighlightLocations = new Location[4][1];
    absoluteHighlightLocations = new Location[4][1];

    Location[][] rotationLocation = blockPiece.getRotationLocations();
    for (int i = 0; i < rotationLocation.length; i++)
      blocks.add(new TetroBlock(blockPiece.getBlockIndex(), rotationLocation[i]));

    relativeHighlightLocations[0][0] = new Location(0, 0);
    relativeHighlightLocations[1][0] = new Location(1, 0);
    relativeHighlightLocations[2][0] = new Location(2, 0);
    relativeHighlightLocations[3][0] = new Location(3, 0);
  }

  public String toString() {
    return "Block: " + blockPiece.getBlockName() + ". Location: " + getX() + "-" + getY() + ". Rotation: " + rotationId;
  }

  @Override
  public BlockPieces getBlockType() {
    return blockPiece;
  }

}
