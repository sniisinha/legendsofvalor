/**
 * ValorTile.java
 *
 * Represents a single cell on the Legends of Valor board.
 * A tile has a fixed cell type, optional terrain behavior,
 * and may contain at most one Hero and one Monster.
 *
 * This class is responsible for:
 * - tracking tile type and accessibility
 * - applying and reverting terrain bonuses
 * - managing hero and monster occupancy
 */
package legends.valor.world;

import legends.characters.Entity;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.world.terrain.Terrain;
import legends.valor.world.terrain.TerrainFactory;
import legends.world.Tile;

public class ValorTile extends Tile {

    private ValorCellType type;     // Logical type of the tile (plain, bush, nexus, etc.)
    private Terrain terrain;        // Terrain behavior derived from the cell type

    private Hero hero;              // Hero currently on this tile
    private Monster monster;        // Monster currently on this tile

    public ValorTile(ValorCellType type) {
        setType(type);
    }

    public ValorCellType getType() {
        return type;
    }

    /**
     * Updates the tile's type and refreshes its terrain behavior.
     * If the new type does not support terrain bonuses, terrain is set to null.
     */
    public void setType(ValorCellType newType) {
        if (newType == null) return;
        this.type = newType;
        this.terrain = TerrainFactory.create(newType);
    }

    @Override
    public boolean isAccessible() {
        return type != null && type.isAccessible();
    }

    @Override
    public String getSymbol() {
        return (type == null) ? "?" : type.getSymbol();
    }

    /**
     * Applies terrain effects when an entity enters this tile.
     */
    public void onEnter(Entity entity) {
        if (terrain != null && entity != null) {
            terrain.onEnter(entity);
        }
    }

    /**
     * Reverts terrain effects when an entity leaves this tile.
     */
    public void onExit(Entity entity) {
        if (terrain != null && entity != null) {
            terrain.onExit(entity);
        }
    }

    public boolean hasHero() {
        return hero != null;
    }

    public boolean hasMonster() {
        return monster != null;
    }

    public Hero getHero() {
        return hero;
    }

    public Monster getMonster() {
        return monster;
    }

    /**
     * Places a hero on this tile.
     * Throws an exception if a hero is already present.
     */
    public void placeHero(Hero h) {
        if (h == null) return;
        if (hero != null) {
            throw new IllegalStateException("Tile already contains a hero.");
        }
        this.hero = h;
    }

    /**
     * Removes the hero from this tile.
     */
    public void removeHero() {
        this.hero = null;
    }

    /**
     * Places a monster on this tile.
     * Throws an exception if a monster is already present.
     */
    public void placeMonster(Monster m) {
        if (m == null) return;
        if (monster != null) {
            throw new IllegalStateException("Tile already contains a monster.");
        }
        this.monster = m;
    }

    /**
     * Removes the monster from this tile.
     */
    public void removeMonster() {
        this.monster = null;
    }

    /**
     * Returns true if a hero can legally occupy this tile.
     */
    public boolean isEmptyForHero() {
        return isAccessible() && hero == null;
    }

    /**
     * Returns true if a monster can legally occupy this tile.
     */
    public boolean isEmptyForMonster() {
        return isAccessible() && monster == null;
    }
}
