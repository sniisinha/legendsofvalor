package legends.characters;

/**
 * File: Exoskeleton.java
 * Description: Represents an Exoskeleton monster. 
 *              Exoskeletons specialize in high defense.
 * 
 * This class stores the monster's base stats and
 * initializes HP using the formula: HP = level × 100.
 */
public class Exoskeleton extends Monster {

    /**
     * Creates a new Exoskeleton monster with stats loaded from file.
     *
     * @param name         Monster's name
     * @param level        Monster's level
     * @param damage       Base damage value
     * @param defense      Defense value (favored stat)
     * @param dodgeChance  Dodge chance (0.0–1.0)
     */
    public Exoskeleton(String name, int level, double damage, double defense, double dodgeChance) {
        this.name = name;
        this.level = level;
        this.damage = damage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;

        // PDF rule: Monster HP = level × 100
        this.hp = level * 100;
    }

    /**
     * Creates an identical copy of the monster.
     * Used so battle instances do not modify the original.
     */
    @Override
    public Monster copy() {
        Exoskeleton e = new Exoskeleton(name, level, damage, defense, dodgeChance);
        e.setHP(this.hp);   // preserve current HP state
        return e;
    }
}