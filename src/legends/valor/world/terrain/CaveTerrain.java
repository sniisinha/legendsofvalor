/**
 * File: CaveTerrain.java
 * Package: legends.valor.world.terrain
 *
 * Purpose:
 *   Represents cave terrain tiles that grant a temporary agility bonus to heroes.
 *
 * Responsibilities:
 *   - Allow entities to traverse cave terrain freely
 *   - Increase a hero’s agility while they remain on the cave tile
 *   - Revert the agility bonus once the hero leaves the cave
 *   - Provide a symbolic representation for board rendering
 */
package legends.valor.world.terrain;

import legends.characters.Entity;
import legends.characters.Hero;

public class CaveTerrain implements Terrain {

    // Percentage-based agility bonus applied while inside cave terrain
    private static final double BONUS = 0.10;

    /**
     * Cave terrain is always accessible to entities.
     */
    @Override
    public boolean isAccessible() {
        return true;
    }

    /**
     * Applies an agility increase when a hero enters the cave.
     * Non-hero entities are unaffected.
     */
    @Override
    public void onEnter(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getAgility();
            double after  = before * (1.0 + BONUS);
            double gained = after - before;

            hero.setAgility(after);

            System.out.printf(
                "[Terrain Bonus] %s entered Cave: Agility +%.2f (%.2f → %.2f)%n",
                hero.getName(), gained, before, after);
        }
    }

    /**
     * Reverts the agility bonus when a hero exits the cave terrain.
     */
    @Override
    public void onExit(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getAgility();
            double after  = before / (1.0 + BONUS);

            hero.setAgility(after);

            System.out.printf(
                "[Terrain Bonus] %s left Cave: Agility reverted (%.2f → %.2f)%n",
                hero.getName(), before, after);
        }
    }

    /**
     * Returns the character used to represent cave terrain on the board.
     */
    @Override
    public char getSymbol() {
        return 'C';
    }
}
