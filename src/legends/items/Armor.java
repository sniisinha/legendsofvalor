/**
 * Armor.java
 * Represents an Armor item that can be equipped by a hero.
 * Armor reduces incoming damage by a fixed reduction value.
 *
 * Each armor piece has:
 *  - a name
 *  - a price in gold
 *  - a required level to use
 *  - a reduction value (amount of damage blocked)
 *
 * Implements {@link Item}, so it can be stored in hero inventories and used in markets.
 */

package legends.items;

public class Armor implements Item {

    private String name;
    private int price;
    private int requiredLevel;
    private int reduction;

    /**
     * Creates a new Armor instance.
     *
     * @param name           the display name of the armor
     * @param price          the cost in gold
     * @param level          required hero level to equip
     * @param reduction      amount of damage this armor reduces
     */
    public Armor(String name, int price, int level, int reduction) {
        this.name = name;
        this.price = price;
        this.requiredLevel = level;
        this.reduction = reduction;
    }

    /** @return the name of the armor */
    public String getName() { return name; }

    /** @return the price of the armor in gold */
    public int getPrice() { return price; }

    /** @return the minimum hero level required to equip this armor */
    public int getRequiredLevel() { return requiredLevel; }

    /** 
     * @return the damage reduction value 
     */
    public int getReduction() {
        return reduction;
    }

    /**
     * Provides a readable string representation for UI display.
     *
     * @return formatted armor name and reduction value
     */
    @Override
    public String toString() {
        return name + " (Reduction: " + reduction + ")";
    }
}