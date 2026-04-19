package tetris;

import ch.aplu.jgamegrid.Location;

/**
 * The enum to represent different block pieces and have different attributes for
 * each block piece. blockCoordinates[blockIndex][rotationId] = {x, y}.
 */
public enum BlockPieces {

    I(0, "I", new int[][][] {
        {{0,0},{0,0},{0,0},{0,0}},
        {{1,0},{0,1},{1,0},{0,1}},
        {{2,0},{0,2},{2,0},{0,2}},
        {{3,0},{0,3},{3,0},{0,3}}
    }),
    J(1, "J", new int[][][] {
        {{0,0},{0,-1},{1,0},{0,1}},
        {{1,0},{0,0},{0,0},{0,0}},
        {{2,0},{0,1},{-1,0},{0,-1}},
        {{2,1},{-1,1},{-1,-1},{1,-1}}
    }),
    L(2, "L", new int[][][] {
        {{2,0},{0,1},{-1,0},{0,-1}},
        {{1,0},{0,0},{0,0},{0,0}},
        {{0,0},{0,-1},{1,0},{0,1}},
        {{0,1},{-1,-1},{1,-1},{1,1}}
    }),
    O(3, "O", new int[][][] {
        {{0,0},{0,0},{0,0},{0,0}},
        {{1,0},{1,0},{1,0},{1,0}},
        {{1,1},{1,1},{1,1},{1,1}},
        {{0,1},{0,1},{0,1},{0,1}}
    }),
    S(4, "S", new int[][][] {
        {{2,0},{0,1},{-1,0},{0,-1}},
        {{1,0},{0,0},{0,0},{0,0}},
        {{1,1},{-1,0},{0,-1},{1,0}},
        {{0,1},{-1,-1},{1,-1},{1,1}}
    }),
    T(5, "T", new int[][][] {
        {{0,0},{0,-1},{1,0},{0,1}},
        {{1,0},{0,0},{0,0},{0,0}},
        {{2,0},{0,1},{-1,0},{0,-1}},
        {{1,1},{-1,0},{0,-1},{1,0}}
    }),
    Z(6, "Z", new int[][][] {
        {{0,0},{0,-1},{1,0},{0,1}},
        {{1,0},{0,0},{0,0},{0,0}},
        {{1,1},{-1,0},{0,-1},{1,0}},
        {{2,1},{-1,1},{-1,-1},{1,-1}}
    }),
    CROSS(7, "X", new int[][][] {
        {{0,0}},
        {{2,0}},
        {{1,1}},
        {{0,2}},
        {{2,2}}
    }),
    PLUS(8, "+", new int[][][] {
        {{1,0}},
        {{0,1}},
        {{1,1}},
        {{2,1}},
        {{1,2}}
    }),
    SLASH(9, "/", new int[][][] {
        {{2,0}},
        {{1,1}},
        {{0,2}}
    });

    private final int blockIndex;
    private final String blockName;
    private final int[][][] blockCoordinates;

    BlockPieces(int blockIndex, String blockName, int[][][] blockCoordinates) {
        this.blockIndex = blockIndex;
        this.blockName = blockName;
        this.blockCoordinates = blockCoordinates;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public String getBlockName() {
        return blockName;
    }

    /**
     * Builds the rotation location table for this piece.
     * Returns Location[numBlocks][numRotations].
     */
    public Location[][] getRotationLocations() {
        int numBlocks = blockCoordinates.length;
        int numRotations = blockCoordinates[0].length;
        Location[][] locs = new Location[numBlocks][numRotations];
        for (int b = 0; b < numBlocks; b++)
            for (int r = 0; r < numRotations; r++)
                locs[b][r] = new Location(blockCoordinates[b][r][0], blockCoordinates[b][r][1]);
        return locs;
    }

    public static BlockPieces getBlockPiece(String blockName) {
        for (BlockPieces blockPiece : BlockPieces.values()) {
            if (blockPiece.blockName.equals(blockName)) {
                return blockPiece;
            }
        }
        return BlockPieces.I;
    }

    @Override
    public String toString() {
        return blockName;
    }
}
