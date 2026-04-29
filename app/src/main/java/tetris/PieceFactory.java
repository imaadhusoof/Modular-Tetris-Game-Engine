
package tetris;

class PieceFactory {
  public static TetroPiece create(BlockPieces piece, Tetris tetris) {

    return switch (piece) {
      case I -> new I(tetris);
      case J -> new J(tetris);
      case L -> new L(tetris);
      case O -> new O(tetris);
      case S -> new S(tetris);
      case T -> new T(tetris);
      case Z -> new Z(tetris);
      case CROSS -> new Cross(tetris);
      case PLUS -> new Plus(tetris);
      case SLASH -> new Slash(tetris);
    };
  }

}
