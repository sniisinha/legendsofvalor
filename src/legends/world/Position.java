/**
 * Position.java
 * Represents a coordinate on the world map.
 * Holds a row and column index indicating the tile position.
 * Used by the party and map for movement and location tracking.
 */

package legends.world;

public class Position {

    /** Row index on the map grid. */
    public int row;

    /** Column index on the map grid. */
    public int col;

    /**
     * Creates a new Position object with the given row and column.
     *
     * @param row the row index
     * @param col the column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}