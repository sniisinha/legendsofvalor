/**
 * File: MarketService.java
 * Package: legends.game.market
 *
 * Purpose:
 *   Encapsulates all business logic related to market transactions.
 *
 * Responsibilities:
 *   - Execute buy and sell operations for heroes
 *   - Manage gold exchange during transactions
 *   - Categorize inventory items by concrete item type
 *   - Provide transaction-related data to higher-level controllers
 */
package legends.game.market;

import legends.characters.Hero;
import legends.items.*;

import java.util.ArrayList;
import java.util.List;

public class MarketService {

    /**
     * Executes a purchase transaction.
     *
     * Deducts the item's price from the buyer's gold
     * and adds the item to the buyer's inventory.
     */
    public void buy(Hero buyer, Item item) {
        // Deduct gold from the buyer based on item price
        buyer.spendGold(item.getPrice());

        // Add the purchased item to the buyer's inventory
        buyer.addItem(item);
    }

    /**
     * Executes a sell transaction.
     *
     * The seller receives 50% of the item's original price.
     * The item is removed from the seller's inventory and
     * the earned gold value is returned to the caller.
     */
    public int sell(Hero seller, Item item) {
        // Calculate resale value (half of original price)
        int value = item.getPrice() / 2;

        // Award gold to the seller
        seller.earnGold(value);

        // Remove the sold item from the seller's inventory
        seller.getInventory().removeItem(item);

        // Return earned value for display or logging
        return value;
    }

    /**
     * Categorizes inventory items by their concrete item type.
     *
     * This method does not modify the inventory; it only
     * groups items to simplify UI rendering and selection.
     */
    public InventoryCategories categorizeInventory(List<Item> inventoryItems) {
        // Container object holding categorized item lists
        InventoryCategories cats = new InventoryCategories();

        // Classify each item based on its runtime type
        for (Item it : inventoryItems) {
            if (it instanceof Weapon) {
                cats.weapons.add((Weapon) it);
            } else if (it instanceof Armor) {
                cats.armors.add((Armor) it);
            } else if (it instanceof Potion) {
                cats.potions.add((Potion) it);
            } else if (it instanceof Spell) {
                cats.spells.add((Spell) it);
            }
        }

        return cats;
    }

    /**
     * Groups inventory items by concrete type.
     *
     * Acts as a simple data-transfer object between
     * the market service layer and the UI layer.
     */
    public static class InventoryCategories {

        // Weapons owned by the hero
        public final List<Weapon> weapons = new ArrayList<Weapon>();

        // Armor pieces owned by the hero
        public final List<Armor> armors = new ArrayList<Armor>();

        // Potions owned by the hero
        public final List<Potion> potions = new ArrayList<Potion>();

        // Spells owned by the hero
        public final List<Spell> spells = new ArrayList<Spell>();
    }
}
