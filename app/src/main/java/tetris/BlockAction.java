package tetris;

public enum BlockAction {
  L, // Move left
  R, // Move right
  T; // Turn (rotate clockwise)

  // Converts a single character from a move-sequence string into a BlockAction.
  // Returns null for unknown action, allowing unkown characters to be silently
  // skipped at parse time.
  public static BlockAction fromChar(char c) {
    for (BlockAction action : values()) {
      if (action.name().charAt(0) == c)
        return action;
    }
    return null;
  }
}
