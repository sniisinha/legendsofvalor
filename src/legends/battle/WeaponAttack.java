/**
 * WeaponAttack represents a normal physical attack using
 * the hero’s equipped weapon. The damage formula will be
 * implemented later based on hero strength and weapon stats.
 */

package legends.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Weapon;

public class WeaponAttack implements AttackStrategy {

    private Weapon weapon; // weapon used for this attack

    public WeaponAttack(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * Computes physical attack damage. Placeholder until
     * full damage formula is implemented.
     */
    @Override
    public double computeDamage(Hero hero, Monster monster) {
        return 0; // placeholder
    }
}