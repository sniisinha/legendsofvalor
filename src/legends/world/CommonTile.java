/**
 * CommonTile.java
 * Represents a basic walkable tile on the map.
 * CommonTiles are accessible and do not block movement.
 * This tile type is used for ordinary ground that the player
 * and monsters can freely move across.
 */

package legends.world;

public class CommonTile extends Tile {

    /**
     * Indicates that this tile is accessible.
     * Common tiles always return true.
     *
     * @return true because the tile is walkable.
     */
    @Override
    public boolean isAccessible() { 
        return true; 
    }

    /**
     * Returns the map symbol used to render this tile.
     * Common tiles are displayed as a blank space.
     *
     * @return " " (a single space character)
     */
    @Override
    public String getSymbol() { 
        return " "; 
    }
}