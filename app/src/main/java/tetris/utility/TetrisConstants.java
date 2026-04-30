package tetris.utility;

public class TetrisConstants {
  // Spawn position
  // TODO: Remove this later, as the spawn location will be randomised
  public static final int SPAWN_COLUMNS = 15;
  public static final int SPAWN_ROWS = 15;

  // Default (non-Feature-2) fixed spawn position: centre of the top row
  public static final int DEFAULT_SPAWN_COL = 6;
  public static final int DEFAULT_SPAWN_ROW = 0;

  // Rotation
  public static final int NUM_ROTATIONS = 4;

  // Simulation timing (milliseconds)
  public static final int MANUAL_SIMULATION_PERIOD = 300;
  public static final int MANUAL_DROP_SIMULATION_PERIOD = 50;
  public static final int AUTO_SIMULATION_PERIOD = 50;

  // Grid dimensions
  public static final int GRID_WIDTH = 15;
  public static final int GRID_HEIGHT = 30;
  public static final int CELL_SIZE = 20;
}
