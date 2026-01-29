/**
 * File: KoulouTerrain.java
 * Package: legends.valor.world.terrain
 *
 * Purpose:
 *   Represents Koulou terrain tiles that grant a temporary strength bonus to heroes.
 *
 * Responsibilities:
 *   - Allow entities to traverse Koulou terrain freely
 *   - Increase a hero’s strength while they remain on the tile
 *   - Revert the strength bonus once the hero leaves the terrain
 *   - Provide a symbolic representation for board rendering
 */
package legends.valor.world.terrain;

import legends.characters.Entity;
import legends.characters.Hero;

public class KoulouTerrain implements Terrain {

    // Percentage-based strength bonus applied while on Koulou terrain
    private static final double BONUS = 0.10;

    /**
     * Koulou terrain is always accessible.
     */
    @Override
    public boolean isAccessible() {
        return true;
    }

    /**
     * Applies a strength increase when a hero enters the Koulou tile.
     * Non-hero entities are unaffected.
     */
    @Override
    public void onEnter(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getStrength();
            double after  = before * (1.0 + BONUS);
            double gained = after - before;

            hero.setStrength(after);

            System.out.printf(
                "[Terrain Bonus] %s entered Koulou: Strength +%.2f (%.2f → %.2f)%n",
                hero.getName(), gained, before, after);
        }
    }

    /**
     * Reverts the strength bonus when a hero exits the Koulou terrain.
     */
    @Override
    public void onExit(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getStrength();
            double after  = before / (1.0 + BONUS);

            hero.setStrength(after);

            System.out.printf(
                "[Terrain Bonus] %s left Koulou: Strength reverted (%.2f → %.2f)%n",
                hero.getName(), before, after);
        }
    }

    /**
     * Returns the character used to represent Koulou terrain on the board.
     */
    @Override
    public char getSymbol() {
        return 'K';
    }
}
