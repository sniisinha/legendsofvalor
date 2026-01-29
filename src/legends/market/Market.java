/**
 * Market.java
 * 
 * Represents an in-game market that stores and organizes all purchasable items.
 * The market may be constructed either using:
 *   A direct constructor that receives four categorized lists.
 *   A convenience constructor that accepts a mixed list of {@link Item}
 *       and automatically sorts them into the correct categories.  
 * Players can buy or sell items from these lists depending on availability.
 */

package legends.market;

import legends.items.*;
import java.util.*;

public class Market {

    /** List of all weapons available in the market. */
    private List<Weapon> weapons;

    /** List of all armor pieces available in the market. */
    private List<Armor> armor;

    /** List of all potions available in the market. */
    private List<Potion> potions;

    /** List of all spells available in the market. */
    private List<Spell> spells;

    /**
     * Constructs a Market using pre-separated lists for each item category.
     *
     * @param weapons list of available weapons
     * @param armor list of available armor
     * @param potions list of available potions
     * @param spells list of available spells
     */
    public Market(List<Weapon> weapons,
                  List<Armor> armor,
                  List<Potion> potions,
                  List<Spell> spells) {

        this.weapons = weapons;
        this.armor = armor;
        this.potions = potions;
        this.spells = spells;
    }

    /**
     * Constructs a Market from a single mixed list of {@link Item}.
     * <p>
     * The constructor identifies each item's type and distributes them
     * into the correct category lists (weapons, armor, potions, spells).
     *
     * @param items a mixed list of Item objects
     */
    public Market(List<Item> items) {
        this.weapons = new ArrayList<>();
        this.armor = new ArrayList<>();
        this.potions = new ArrayList<>();
        this.spells = new ArrayList<>();

        for (Item i : items) {
            if (i instanceof Weapon) {
                weapons.add((Weapon) i);
            } else if (i instanceof Armor) {
                armor.add((Armor) i);
            } else if (i instanceof Potion) {
                potions.add((Potion) i);
            } else if (i instanceof Spell) {
                spells.add((Spell) i);
            }
        }
    }

    /**
     * @return list of all weapons available for purchase.
     */
    public List<Weapon> getWeapons() {
        return weapons;
    }

    /**
     * @return list of all armor pieces available for purchase.
     */
    public List<Armor> getArmor() {
        return armor;
    }

    /**
     * @return list of all potions available for purchase.
     */
    public List<Potion> getPotions() {
        return potions;
    }

    /**
     * @return list of all spells available for purchase.
     */
    public List<Spell> getSpells() {
        return spells;
    }
}