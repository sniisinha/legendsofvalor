/**
 * File: Dragon.java
 * Description: Represents a Dragon monster. Dragons favor higher base damage.
 * Part of: legends.characters package
 *
 * This class extends Monster and initializes a Dragon's attributes using
 * values loaded from the data files. HP is always calculated as level × 100.
 * A copy() method is implemented to safely duplicate monsters for battles.
 */

package legends.characters;

public class Dragon extends Monster {

    /**
     * Constructor for creating a Dragon with all stats loaded from file.
     *
     * @param name         Name of the dragon
     * @param level        Monster level
     * @param damage       Base damage value
     * @param defense      Defense value
     * @param dodgeChance  Dodge probability (0–1)
     */
    public Dragon(String name, int level, double damage, double defense, double dodgeChance) {
        this.name = name;
        this.level = level;
        this.damage = damage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;

        // HP formula from PDF: HP = level × 100
        this.hp = level * 100;
    }

    /**
     * Creates a deep copy of this Dragon so battles do not mutate the original.
     *
     * @return a new Dragon with identical stats
     */
    @Override
    public Monster copy() {
        Dragon d = new Dragon(name, level, damage, defense, dodgeChance);

        // Copy current HP (because battles modify it)
        d.setHP(this.hp);

        return d;
    }
}