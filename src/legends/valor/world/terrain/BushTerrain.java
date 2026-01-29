/**
 * File: BushTerrain.java
 * Package: legends.valor.world.terrain
 *
 * Purpose:
 *   Represents bush terrain tiles that grant a temporary dexterity bonus to heroes.
 *
 * Responsibilities:
 *   - Allow entities to enter the terrain without restriction
 *   - Apply a dexterity bonus to heroes when they enter the bush
 *   - Revert the dexterity bonus when heroes leave the bush
 *   - Provide a symbolic representation for board rendering
 */
package legends.valor.world.terrain;

import legends.characters.Entity;
import legends.characters.Hero;

public class BushTerrain implements Terrain {

    // Percentage-based dexterity bonus applied while inside bush terrain
    private static final double BONUS = 0.10;

    /**
     * Bush terrain is always traversable.
     */
    @Override
    public boolean isAccessible() {
        return true;
    }

    /**
     * Applies a dexterity increase when a hero enters the bush.
     * Non-hero entities are unaffected.
     */
    @Override
    public void onEnter(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getDexterity();
            double after  = before * (1.0 + BONUS);
            double gained = after - before;

            hero.setDexterity(after);

            System.out.printf(
                "[Terrain Bonus] %s entered Bush: Dexterity +%.2f (%.2f → %.2f)%n",
                hero.getName(), gained, before, after);
        }
    }

    /**
     * Reverts the previously applied dexterity bonus when a hero exits the bush.
     */
    @Override
    public void onExit(Entity entity) {
        if (entity instanceof Hero) {
            Hero hero = (Hero) entity;

            double before = hero.getDexterity();
            double after  = before / (1.0 + BONUS);

            hero.setDexterity(after);

            System.out.printf(
                "[Terrain Bonus] %s left Bush: Dexterity reverted (%.2f → %.2f)%n",
                hero.getName(), before, after
            );
        }
    }

    /**
     * Returns the character used to represent bush terrain on the board.
     */
    @Override
    public char getSymbol() {
        return 'B';
    }
}
