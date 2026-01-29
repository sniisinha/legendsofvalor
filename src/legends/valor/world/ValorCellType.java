/**
 * ValorCellType.java
 *
 * Enumerates all possible cell types on the Legends of Valor board.
 * Each type defines its display symbol and basic accessibility rules.
 */
package legends.valor.world;

public enum ValorCellType {

    // Special board cells
    NEXUS('N'),
    INACCESSIBLE('X'),
    OBSTACLE('O'),

    // Lane terrain cells
    PLAIN(' '),
    BUSH('B'),
    CAVE('C'),
    KOULOU('K');

    // Single-character symbol used when rendering the board
    private final String symbol;

    // Store symbol as a String for easier ANSI/color formatting later
    ValorCellType(char symbol) {
        this.symbol = String.valueOf(symbol);
    }

    // Returns the printable symbol for this cell type
    public String getSymbol() {
        return symbol;
    }

    // Determines whether entities are allowed to move onto this cell
    public boolean isAccessible() {
        return this != INACCESSIBLE && this != OBSTACLE;
    }

    // Indicates whether this cell provides a terrain-based stat bonus
    public boolean hasTerrainBonus() {
        return this == BUSH || this == CAVE || this == KOULOU;
    }
}
