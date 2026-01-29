/**
 * Represents a Sorcerer hero.
 * Sorcerers favor Dexterity and Agility and gain extra bonuses to these stats when leveling up.
 * This class defines how Sorcerers initialize their stats and how they level up.
 */

package legends.characters;

public class Sorcerer extends Hero {

    /**
     * Constructor that sets all starting attributes for the Sorcerer.
     */
    public Sorcerer(String name, int level, double hp, double mp,
                    double strength, double dexterity, double agility) {
        this.name = name;
        this.level = level;
        this.hp = hp;               // initial health
        this.mp = mp;               // initial mana
        this.strength = strength;   // physical power
        this.dexterity = dexterity; // affects spell damage
        this.agility = agility;     // affects dodge chance
        this.gold = 100;            // starting gold
        this.experience = 0;        // starting XP
    }

    /**
     * Levels up the Sorcerer according to the assignment rules.
     * - HP resets to level × 100
     * - MP increases by 10%
     * - Strength increases by 5%
     * - Dexterity increases by 10% (favored stat)
     * - Agility increases by 10% (favored stat)
     */
    @Override
    public void levelUp() {
        level++;

        this.hp = level * 100;  // reset HP based on new level
        this.mp *= 1.1;         // MP grows by 10%

        strength *= 1.05;       // small boost
        dexterity *= 1.10;      // favored boost
        agility *= 1.10;        // favored boost

        System.out.println("\u001B[92m" + name +
                " is now Level " + level + "! (Sorcerer)\u001B[0m");
    }
}