/**
 * Tile.java
 * Abstract base class representing a tile on the game map.
 * Each tile determines:
 *  - Whether it can be walked on (accessible or blocked)
 *  - What symbol should be displayed on the map
 *
 * Subclasses implement specific tile behaviors such as:
 *  - CommonTile (walkable)
 *  - MarketTile (walkable, opens shop)
 *  - InaccessibleTile (blocked)
 */

package legends.world;

public abstract class Tile {

    /**
     * Indicates whether the tile can be stepped on by the player.
     *
     * @return true if accessible, false if inaccessible.
     */
    public abstract boolean isAccessible();

    /**
     * Returns a short symbol representing this tile on the map.
     * Examples:
     *  - " " for common tiles
     *  - "M" for market tiles
     *  - "X" for inaccessible tiles
     *
     * @return a one-character string indicating tile type.
     */
    public abstract String getSymbol();
}