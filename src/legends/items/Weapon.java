/**
 * Weapon.java
 * Represents a weapon item that heroes can purchase, equip, and use in battle.
 * Weapons provide a fixed damage value and may require one or two hands.
 * Implements the {@link Item} interface so it can be handled generically in inventories and markets.
 */

package legends.items;

public class Weapon implements Item {

    private String name;
    private int price;
    private int requiredLevel;
    private int damage;
    private int handsRequired;

    /**
     * Creates a new Weapon.
     *
     * @param name           the name of the weapon
     * @param price          the cost to buy the weapon
     * @param level          minimum hero level required to equip
     * @param damage         base damage the weapon adds to attacks
     * @param handsRequired  number of hands needed (1 or 2)
     */
    public Weapon(String name, int price, int level, int damage, int handsRequired) {
        this.name = name;
        this.price = price;
        this.requiredLevel = level;
        this.damage = damage;
        this.handsRequired = handsRequired;
    }

    /** @return the weapon name */
    @Override
    public String getName() {
        return name;
    }

    /** @return the price of the weapon */
    @Override
    public int getPrice() {
        return price;
    }

    /** @return required level needed to equip the weapon */
    @Override
    public int getRequiredLevel() {
        return requiredLevel;
    }

    /** @return the base damage value provided by this weapon */
    public int getDamage() {
        return damage;
    }

    /** @return how many hands the weapon occupies (1 or 2) */
    public int getHandsRequired() {
        return handsRequired;
    }

    /** 
     * Duplicate accessor used elsewhere in the codebase.
     * @return number of hands required 
     */
    public int getHands() {     
        return handsRequired;
    }

    /**
     * @return a readable formatted string containing weapon stats
     */
    @Override
    public String toString() {
        return name + " (Lvl " + requiredLevel
                + ", DMG: " + damage
                + ", Hands: " + handsRequired + ")";
    }
}