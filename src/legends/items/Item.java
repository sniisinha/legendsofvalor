/**
 * Item.java
 * Represents a generic item that can appear in the game.
 * All game items (Weapons, Armor, Potions, Spells) must implement this interface.
 *
 * Each item provides:
 * - A display name
 * - A gold price
 * - A minimum level required to use or equip it
 */

package legends.items;

public interface Item {

    /**
     * @return the name of the item as shown to the player
     */
    String getName();

    /**
     * @return the purchase price of the item in gold
     */
    int getPrice();

    /**
     * @return the minimum hero level required to use or equip the item
     */
    int getRequiredLevel();
}