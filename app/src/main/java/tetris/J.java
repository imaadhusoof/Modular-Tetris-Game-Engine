
// J.java
package tetris;

import ch.aplu.jgamegrid.*;

class J extends TetroPiece {
  private final BlockPieces blockPiece = BlockPieces.J;
  private Location[][] rotationLocation = new Location[4][4];

  J(Tetris tetris) {
    super(tetris);
    // allocate highlight arrays
    relativeHighlightLocations = new Location[3][2];
    absoluteHighlightLocations = new Location[3][2];

    // rotationId 0
    rotationLocation[0][0] = new Location(new Location(0, 0));
    rotationLocation[1][0] = new Location(new Location(1, 0));
    rotationLocation[2][0] = new Location(new Location(2, 0));
    rotationLocation[3][0] = new Location(new Location(2, 1));
    // rotationId 1
    rotationLocation[0][1] = new Location(new Location(0, -1));
    rotationLocation[1][1] = new Location(new Location(0, 0));
    rotationLocation[2][1] = new Location(new Location(0, 1));
    rotationLocation[3][1] = new Location(new Location(-1, 1));
    // rotationId 2
    rotationLocation[0][2] = new Location(new Location(1, 0));
    rotationLocation[1][2] = new Location(new Location(0, 0));
    rotationLocation[2][2] = new Location(new Location(-1, 0));
    rotationLocation[3][2] = new Location(new Location(-1, -1));
    // rotationId 3
    rotationLocation[0][3] = new Location(new Location(0, 1));
    rotationLocation[1][3] = new Location(new Location(0, 0));
    rotationLocation[2][3] = new Location(new Location(0, -1));
    rotationLocation[3][3] = new Location(new Location(1, -1));

    for (int i = 0; i < rotationLocation.length; i++)
      blocks.add(new TetroBlock(blockPiece.getBlockIndex(), rotationLocation[i]));

    relativeHighlightLocations[0][0] = new Location(new Location(0, 0));
    relativeHighlightLocations[1][0] = new Location(new Location(1, 0));
    relativeHighlightLocations[2][0] = new Location(new Location(2, 0));
    relativeHighlightLocations[0][1] = new Location(new Location(0, 1));
    relativeHighlightLocations[1][1] = new Location(new Location(1, 1));
    relativeHighlightLocations[2][1] = new Location(new Location(2, 1));
  }

  public String toString() {
    return "Block: " + blockPiece.getBlockName() + ". Location: " + getX() + "-" + getY() + ". Rotation: " + rotationId;
  }

}
