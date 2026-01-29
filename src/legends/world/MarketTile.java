/**
 * MarketTile.java
 * Represents a market tile on the map.
 * Market tiles are accessible and marked with the symbol 'M'.
 * Landing on this tile allows the player to enter the Market.
 */

package legends.world;

public class MarketTile extends Tile {

    /**
     * Market tiles are always accessible.
     * @return true because the player can step onto this tile.
     */
    @Override
    public boolean isAccessible() { 
        return true; 
    }

    /**
     * Returns the map symbol representing a market.
     * @return "M" as the market tile symbol.
     */
    @Override
    public String getSymbol() { 
        return "M"; 
    }
}