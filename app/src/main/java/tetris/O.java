// O.java
package tetris;

import ch.aplu.jgamegrid.*;

class O extends TetroPiece {
  private final BlockPieces blockPiece = BlockPieces.O;

  O(Tetris tetris) {
    super(tetris);
    relativeHighlightLocations = new Location[2][2];
    absoluteHighlightLocations = new Location[2][2];

    Location[][] rotationLocation = blockPiece.getRotationLocations();
    for (int i = 0; i < rotationLocation.length; i++)
      blocks.add(new TetroBlock(blockPiece.getBlockIndex(), rotationLocation[i]));

    relativeHighlightLocations[0][0] = new Location(0, 0);
    relativeHighlightLocations[1][0] = new Location(1, 0);
    relativeHighlightLocations[0][1] = new Location(0, 1);
    relativeHighlightLocations[1][1] = new Location(1, 1);
  }

  public String toString() {
    return "Block: " + blockPiece.getBlockName() + ". Location: " + getX() + "-" + getY() + ". Rotation: " + rotationId;
  }

}
