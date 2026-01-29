package legends.characters;

import legends.items.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory.java
 * Stores and manages all items owned by a hero.
 *
 * The inventory supports:
 *  - Adding and removing items
 *  - Retrieving the full item list
 *  - Printing items in simple or formatted UI-friendly layouts
 *
 * This class is intentionally lightweight and reusable across
 * different game states (Market, Battle, Inventory views).
 */
public class Inventory {

    /** List of all items currently owned by the hero */
    private List<Item> items;

    /**
     * Creates an empty inventory.
     */
    public Inventory() {
        items = new ArrayList<>();
    }

    /**
     * Adds an item to the inventory.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Backward-compatible alias for addItem().
     *
     * @param item the item to add
     */
    public void add(Item item) {
        addItem(item);
    }

    /**
     * Removes a specific item from the inventory.
     *
     * @param item the item to remove
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Backward-compatible alias for removeItem().
     *
     * @param item the item to remove
     */
    public void remove(Item item) {
        removeItem(item);
    }

    /**
     * Returns the list of items in the inventory.
     *
     * @return list of Item objects
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Prints the inventory in a simple numbered list.
     * Useful for debugging or minimal UI displays.
     */
    public void print() {
        if (items.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        int index = 1;
        for (Item item : items) {
            System.out.println(index++ + ". " + item.getName() +
                               " (Lvl " + item.getRequiredLevel() + ")");
        }
    }

    /**
     * Prints the inventory inside a fixed-width box.
     * Used by menus such as Market and Inventory views.
     *
     * @param width total width of the printed box
     */
    public void printFormatted(int width) {

        if (items.isEmpty()) {
            String line = "(empty)";
            int spaces = width - line.length();
            System.out.println("║ " + line + repeat(" ", spaces - 2) + " ║");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            String text = (i + 1) + ". " + item.getName()
                    + "  (Lvl " + item.getRequiredLevel() + ")";

            int insideWidth = width - 2;
            int padding = insideWidth - text.length();
            if (padding < 0) padding = 0;

            String padded = text + repeat(" ", padding);

            System.out.println("║ " + padded + " ║");
        }
    }

    /**
     * Helper method to repeat a string multiple times.
     * This replaces String.repeat(), which is unavailable in Java 8.
     *
     * @param s the string to repeat
     * @param count number of times to repeat
     * @return repeated string
     */
    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }
}