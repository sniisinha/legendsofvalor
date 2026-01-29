/**
 * Warrior.java
 * Represents a Warrior hero class.
 * Warriors favor Strength and Agility when leveling up.
 */

package legends.characters;

public class Warrior extends Hero {

    /**
     * Creates a Warrior with all base attributes.
     */
    public Warrior(String name, int level, double hp, double mp,
                   double strength, double dexterity, double agility) {
        this.name = name;
        this.level = level;
        this.hp = hp;          // initial HP loaded from file
        this.mp = mp;          // initial mana loaded from file
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.gold = 100;       // starting gold
        this.experience = 0;   // starting XP
    }

    /**
     * Handles Warrior level-up logic based on PDF rules.
     * - HP resets to level × 100
     * - MP increases by 10%
     * - Strength & Agility gain extra boost (favored stats)
     */
    @Override
    public void levelUp() {
        level++;

        // Reset HP according to PDF formula
        this.hp = level * 100;

        // Mana increases by 10%
        this.mp *= 1.1;

        // Warriors favor Strength & Agility
        strength *= 1.10;   // +10%
        agility  *= 1.10;   // +10%
        dexterity*= 1.05;   // +5% normal increase

        System.out.println("\u001B[92m" + name +
                " is now Level " + level + "! (Warrior)\u001B[0m");
    }
}