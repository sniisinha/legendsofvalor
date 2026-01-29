/**
 * InaccessibleTile.java
 * Represents a tile on the map that cannot be entered by the player.
 * This is used to create blocked or forbidden regions.
 *
 * Inaccessible tiles:
 * - Always return false for isAccessible()
 * - Display an 'X' symbol when the map is printed
 */

package legends.world;

public class InaccessibleTile extends Tile {

    /**
     * Indicates whether this tile can be stepped on.
     * @return false because inaccessible tiles cannot be entered.
     */
    @Override
    public boolean isAccessible() {
        return false;
    }

    /**
     * The map symbol used to represent this tile visually.
     * @return "X" as the marker for inaccessible spaces.
     */
    @Override
    public String getSymbol() {
        return "X";
    }
}