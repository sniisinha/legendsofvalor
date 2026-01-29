/**
 * WorldMap.java
 * Represents the game's world map as a 2D grid of Tile objects.
 * The map tracks tile accessibility, movement checks, and rendering.
 * Rendering includes colored symbols and borders for visual clarity.
 */
package legends.world;

public class WorldMap {

    private Tile[][] grid;
    private int size;

    /**
     * Creates a new square world map of the given size.
     *
     * @param size number of rows/columns in the map
     */
    public WorldMap(int size) {
        this.size = size;
        this.grid = new Tile[size][size];
    }

    /**
     * Places a tile at a specific grid location.
     *
     * @param row tile row
     * @param col tile column
     * @param tile the Tile to place
     */
    public void setTile(int row, int col, Tile tile) {
        grid[row][col] = tile;
    }

    /**
     * Retrieves the tile at a given position.
     *
     * @param p a Position object containing row/col
     * @return the Tile at that position
     */
    public Tile getTile(Position p) {
        return grid[p.row][p.col];
    }

    /**
     * Checks if the given position is inside map bounds.
     *
     * @param p position to check
     * @return true if inside bounds, false otherwise
     */
    public boolean inBounds(Position p) {
        return p.row >= 0 && p.row < size && p.col >= 0 && p.col < size;
    }

    /**
     * Checks whether the player can move onto the given tile.
     * A tile is valid if it's within bounds and accessible.
     *
     * @param p position to check
     * @return true if movement is allowed
     */
    public boolean canMove(Position p) {
        return inBounds(p) && grid[p.row][p.col].isAccessible();
    }

    /**
     * Prints the world map to the screen.
     * Highlights:
     * - P for party position (yellow)
     * - X for inaccessible (red)
     * - M for market (green)
     * - · for common tiles (grey)
     *
     * @param partyPos the current position of the player's party
     */
    public void print(Position partyPos) {

        final String RED    = "\u001B[91m";
        final String GREEN  = "\u001B[92m";
        final String YELLOW = "\u001B[93m";
        final String GREY   = "\u001B[90m";
        final String RESET  = "\u001B[0m";

        int n = size;

        System.out.println("\n\u001B[95m=== WORLD MAP ===\u001B[0m");

        // Top border
        System.out.print("┏");
        for (int col = 0; col < n; col++) {
            System.out.print("━━━");
            if (col < n - 1) System.out.print("┳");
        }
        System.out.println("┓");

        for (int row = 0; row < n; row++) {

            // Row content
            System.out.print("┃");

            for (int col = 0; col < n; col++) {

                Tile t = grid[row][col];

                String symbol;

                if (partyPos.row == row && partyPos.col == col) {
                    symbol = YELLOW + "P" + RESET;
                } else {
                    switch (t.getSymbol()) {
                        case "X": symbol = RED   + "X" + RESET; break;
                        case "M": symbol = GREEN + "M" + RESET; break;
                        default:  symbol = GREY  + "·" + RESET; break;
                    }
                }

                System.out.print(" " + symbol + " ");
                System.out.print("┃");
            }

            System.out.println();

            // Middle separators
            if (row < n - 1) {
                System.out.print("┣");
                for (int col = 0; col < n; col++) {
                    System.out.print("━━━");
                    if (col < n - 1) System.out.print("╋");
                }
                System.out.println("┫");
            }
        }

        // Bottom border
        System.out.print("┗");
        for (int col = 0; col < n; col++) {
            System.out.print("━━━");
            if (col < n - 1) System.out.print("┻");
        }
        System.out.println("┛");
    }

}