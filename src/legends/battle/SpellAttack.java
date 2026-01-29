/**
 * SpellAttack represents an attack where a hero casts a spell
 * on a monster. It uses the spell’s damage and applies any
 * additional spell effect.
 */

package legends.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Spell;

public class SpellAttack implements AttackStrategy {

    private Spell spell; // spell being used in this attack

    public SpellAttack(Spell spell) {
        this.spell = spell;
    }

    /**
     * Computes the damage from a spell attack. Spell effects
     * will be applied in the full implementation.
     */
    @Override
    public double computeDamage(Hero hero, Monster monster) {
        return 0; // placeholder until full damage logic is added
    }
}