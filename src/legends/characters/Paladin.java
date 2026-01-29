/**
 * File: Paladin.java
 * Description: Represents a Paladin hero class.
 * Paladins favor Strength and Dexterity and gain extra bonuses
 * to these stats when leveling up.
 */

package legends.characters;

public class Paladin extends Hero {

    // Constructor sets all initial base attributes for the Paladin
    public Paladin(String name, int level, double hp, double mp,
                   double strength, double dexterity, double agility) {

        this.name = name;
        this.level = level;

        // Base stats loaded from the data file
        this.hp = hp;
        this.mp = mp;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;

        // Starting currency + experience
        this.gold = 100;
        this.experience = 0;
    }

    @Override
    public void levelUp() {
        level++; // Increase hero level

        // HP resets based on PDF rule: HP = level × 100
        this.hp = level * 100;

        // MP increases based on PDF rule: MP = MP × 1.1
        this.mp *= 1.1;

        // Paladin favored stats → Strength & Dexterity get +10%
        strength *= 1.10;
        dexterity *= 1.10;

        // Non-favored stat → Agility gets +5%
        agility *= 1.05;

        System.out.println("\u001B[92m" + name +
                " is now Level " + level + "! (Paladin)\u001B[0m");
    }
}