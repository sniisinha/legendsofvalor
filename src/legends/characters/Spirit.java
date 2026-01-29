/**
 * File: Spirit.java
 * Description: Monster subclass representing a Spirit.
 * Spirits have increased dodge ability and use standard Monster behavior.
 * Provides a copy() method so battles use cloned instances.
 */

package legends.characters;

public class Spirit extends Monster {

    /**
     * Creates a new Spirit monster with its base stats.
     * HP is initialized from the PDF rule: HP = level × 100.
     */
    public Spirit(String name, int level, double damage, double defense, double dodgeChance) {
        this.name = name;
        this.level = level;
        this.damage = damage;       // base attack damage
        this.defense = defense;     // damage reduction
        this.dodgeChance = dodgeChance; // dodge % as 0–1 value
        this.hp = level * 100;      // starting HP based on level
    }

    /**
     * Returns an identical copy of this monster.
     * Used so each battle gets a fresh monster instance.
     */
    @Override
    public Monster copy() {
        Spirit s = new Spirit(name, level, damage, defense, dodgeChance);
        s.setHP(this.hp); // preserve current HP value
        return s;
    }
}