/**
 * Monster.java
 * Represents any monster in the game.
 * Monsters have HP, damage, defense, and dodge chance.
 * Specific monster types (Dragon, Spirit, Exoskeleton) extend this class.
 */

package legends.characters;

public abstract class Monster extends Entity {

    // Monster attributes
    protected double damage;
    protected double defense;
    protected double dodgeChance;   // stored as 0.0 – 1.0

    // Basic setters
    public void setDamage(double d) { this.damage = d; }
    public void setDefense(double def) { this.defense = def; }
    public void setDodgeChance(double dodge) { this.dodgeChance = dodge; }

    // Basic getters
    public double getDamage() { return damage; }
    public double getDefense() { return defense; }
    public double getDodgeChance() { return dodgeChance; }

    /**
     * Reduces the monster's HP when it takes damage.
     * HP can never go below 0.
     */
    public void takeDamage(double amount) {
        hp = Math.max(0, hp - amount);
    }

    /**
     * Display monster stats in a readable format.
     */
    @Override
    public String toString() {
        return name + " (Lvl " + level + ", HP: " + hp +
                ", DMG: " + damage + ", DEF: " + defense +
                ", Dodge: " + (int)(dodgeChance * 100) + "%)";
    }

    /**
     * Used in battles to create a separate monster copy
     * so original stats are not overwritten.
     */
    public abstract Monster copy();

    /**
     * Fire spells lower monster defense.
     */
    public void applyFireDebuff(double amount) {
        this.defense = Math.max(0, this.defense - amount);
    }

    /**
     * Ice spells lower monster base damage.
     */
    public void applyIceDebuff(double amount) {
        this.damage = Math.max(0, this.damage - amount);
    }

    /**
     * Lightning spells lower monster dodge chance.
     */
    public void applyLightningDebuff(double amount) {
        this.dodgeChance = Math.max(0, this.dodgeChance - amount);
    }
}