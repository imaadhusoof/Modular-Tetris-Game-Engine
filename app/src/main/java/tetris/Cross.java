// Cross.java
package tetris;

import ch.aplu.jgamegrid.*;

class Cross extends TetroPiece {
  private final BlockPieces blockPiece = BlockPieces.CROSS;

  Cross(Tetris tetris) {
    super(tetris);
    // allocate highlight arrays
    relativeHighlightLocations = new Location[3][3];
    absoluteHighlightLocations = new Location[3][3];

    Location[][] rotationLocation = blockPiece.getRotationLocations();

    for (int i = 0; i < rotationLocation.length; i++)
      // TODO: chagne to their respective index later with `.getBlockIndex()`. Right
      // now: no sprite for the
      // new pieces yet
      blocks.add(new TetroBlock(3, rotationLocation[i]));

    // full 3x3 bounding box
    relativeHighlightLocations[0][0] = new Location(0, 0);
    relativeHighlightLocations[1][0] = new Location(1, 0);
    relativeHighlightLocations[2][0] = new Location(2, 0);
    relativeHighlightLocations[0][1] = new Location(0, 1);
    relativeHighlightLocations[1][1] = new Location(1, 1);
    relativeHighlightLocations[2][1] = new Location(2, 1);
    relativeHighlightLocations[0][2] = new Location(0, 2);
    relativeHighlightLocations[1][2] = new Location(1, 2);
    relativeHighlightLocations[2][2] = new Location(2, 2);
  }

  public String toString() {
    return "Block: " + blockPiece.getBlockName() + ". Location: " + getX() + "-" + getY() + ". Rotation: " + rotationId;
  }

  @Override
  void rotate() {
    // cannot rotate, do nothing
  }

}
