/**
 * ValorDirection.java
 *
 * Represents the four cardinal movement directions used on the
 * Legends of Valor board. Each direction defines how it changes
 * a unit’s row and column position.
 *
 * Coordinate system:
 *  - Rows increase downward
 *  - Columns increase to the right
 *
 * North  → toward monsters' Nexus (row - 1)
 * South  → toward heroes' Nexus   (row + 1)
 */
package legends.valor.world;

public enum ValorDirection {

    // Move one cell upward (toward monsters' side)
    NORTH(-1, 0),

    // Move one cell downward (toward heroes' side)
    SOUTH(1, 0),

    // Move one cell to the left
    WEST(0, -1),

    // Move one cell to the right
    EAST(0, 1);

    // Row delta applied when moving in this direction
    private final int dRow;

    // Column delta applied when moving in this direction
    private final int dCol;

    // Store the movement offsets for this direction
    ValorDirection(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    // Returns how this direction affects the row index
    public int deltaRow() {
        return dRow;
    }

    // Returns how this direction affects the column index
    public int deltaCol() {
        return dCol;
    }
}
