/**
 * AttackStrategy defines a common interface for all attack types.
 * Each attack (weapon, spell, etc.) must compute how much damage
 * a hero deals to a specific monster.
 */

package legends.battle;

import legends.characters.Hero;
import legends.characters.Monster;

public interface AttackStrategy {

    /**
     * Computes the damage dealt from a hero to a monster
     * based on the specific type of attack.
     */
    double computeDamage(Hero hero, Monster monster);
}
