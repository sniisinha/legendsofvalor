/**
 * File: Terrain.java
 * Package: legends.valor.world.terrain
 *
 * Purpose:
 *   Defines the contract for all terrain types used in Legends of Valor.
 *
 * Responsibilities:
 *   - Specify whether a terrain tile can be entered
 *   - Apply terrain-specific effects when an entity enters the tile
 *   - Revert or clean up effects when an entity leaves the tile
 *   - Provide a symbolic representation for board rendering
 *
 */
package legends.valor.world.terrain;

import legends.characters.Entity;

public interface Terrain {

    /**
     * Indicates whether entities are allowed to enter this terrain.
     * Blocking terrains (e.g., walls) should return false.
     */
    boolean isAccessible();

    /**
     * Triggered when an entity enters this terrain tile.
     * Used to apply terrain-specific effects or bonuses.
     */
    void onEnter(Entity entity);

    /**
     * Triggered when an entity exits this terrain tile.
     * Used to revert or clean up any effects applied on entry.
     */
    void onExit(Entity entity);

    /**
     * Returns the character used to represent this terrain
     * when rendering the board in the console.
     */
    char getSymbol();
}
