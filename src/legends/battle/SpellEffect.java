/**
 * SpellEffect defines the interface for all special spell side-effects.
 * Each spell effect modifies one of the monster's stats.
 */

package legends.battle;

import legends.characters.Monster;

public interface SpellEffect {

    /**
     * Applies the effect to the given monster.
     */
    void apply(Monster monster);
}