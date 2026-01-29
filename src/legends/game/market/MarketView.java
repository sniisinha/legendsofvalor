/**
 * File: MarketView.java
 * Package: legends.game.market
 *
 * Purpose:
 *   Handles all console-based output related to the market.
 *
 * Responsibilities:
 *   - Render market menus and prompts
 *   - Display hero, item, and transaction information
 *   - Format tabular item listings for user selection
 *   - Keep all presentation logic separate from input and business logic
 */
package legends.game.market;

import legends.characters.Hero;
import legends.items.*;

import java.util.List;

public class MarketView {

    // ANSI color codes used to enhance console readability
    private static final String RESET   = "\u001B[0m";
    private static final String GREEN   = "\u001B[92m";
    private static final String YELLOW  = "\u001B[93m";
    private static final String CYAN    = "\u001B[96m";
    private static final String MAGENTA = "\u001B[95m";

    // Fixed width used for menu box rendering
    private static final int MENU_WIDTH = 72;

    /**
     * Displays the main market menu.
     */
    public void printMainMenu() {
        System.out.println();
        printMenuBox(
                "MARKET",
                new String[]{
                        "  1. Buy Items",
                        "  2. Sell Items",
                        "  B. Back to Map"
                },
                YELLOW
        );
        System.out.print("Choose option: ");
    }

    /**
     * Displays the item category menu for buying.
     */
    public void printBuyMenu() {
        System.out.println();
        printMenuBox(
                "BUY MENU",
                new String[]{
                        "  1. Weapons",
                        "  2. Armor",
                        "  3. Potions",
                        "  4. Spells",
                        "  B. Back"
                },
                CYAN
        );
        System.out.print("Choose category: ");
    }

    /**
     * Displays the item category menu for selling.
     */
    public void printSellMenu() {
        System.out.println();
        printMenuBox(
                "SELL MENU",
                new String[]{
                        "  1. Weapons",
                        "  2. Armor",
                        "  3. Potions",
                        "  4. Spells",
                        "  B. Back"
                },
                CYAN
        );
        System.out.print("Choose category to sell from: ");
    }

    /**
     * Prints a header prompting the user to choose a hero
     * for a market transaction.
     */
    public void printHeroTransactionHeader(String verb) {
        System.out.println("\n" + MAGENTA + "Choose a hero to " + verb + " with:" + RESET);
    }

    /**
     * Displays a single hero line with index and gold amount.
     */
    public void printHeroLine(int indexOneBased, Hero hero) {
        System.out.printf("%d. %s (Gold: %d)%n", indexOneBased, hero.getName(), hero.getGold());
    }

    /**
     * Displays confirmation output after a successful purchase.
     */
    public void printBuySuccess(Hero buyer, Item item) {
        System.out.println(GREEN + "✔ " + buyer.getName() + " bought " + item.getName() + RESET);
        System.out.println("Remaining gold: " + buyer.getGold());
    }

    /**
     * Displays confirmation output after a successful sale.
     */
    public void printSellSuccess(Hero seller, Item item, int value) {
        System.out.println(GREEN + "✔ Sold " + item.getName() + " for " + value + " gold." + RESET);
        System.out.println("New gold total: " + seller.getGold());
    }

    /**
     * Prints a formatted table of items for selection.
     */
    public <T extends Item> void printItemTable(List<T> items, String title) {
        final int WIDTH = 61;

        System.out.println("╔" + repeat("═", WIDTH) + "╗");
        System.out.println("║" + center(title, WIDTH) + "║");
        System.out.println("╠" + repeat("═", WIDTH) + "╣");

        // Table header
        System.out.printf(
                "║ %-3s %-20s %-12s %-12s %-8s ║%n",
                "No", "Name", "Type", "Price", "Lvl"
        );

        System.out.println("╠" + repeat("─", WIDTH) + "╣");

        // Render each item row with a 1-based index
        for (int i = 0; i < items.size(); i++) {
            T it = items.get(i);

            System.out.printf(
                    "║ %-3d %-20s %-12s %-12d %-8d ║%n",
                    i + 1,
                    trimTo(it.getName(), 20),
                    trimTo(typeName(it), 12),
                    it.getPrice(),
                    it.getRequiredLevel()
            );
        }

        System.out.println("╚" + repeat("═", WIDTH) + "╝");
    }

    /**
     * Resolves a human-readable item type name.
     */
    private String typeName(Item it) {
        if (it instanceof Weapon) return "Weapon";
        if (it instanceof Armor)  return "Armor";
        if (it instanceof Potion) return "Potion";
        if (it instanceof Spell)  return "Spell";
        return "Item";
    }

    /**
     * Centers text within a fixed width.
     */
    private String center(String text, int width) {
        if (text.length() >= width) return text;
        int pad = (width - text.length()) / 2;
        return repeat(" ", pad) + text + repeat(" ", width - pad - text.length());
    }

    /**
     * Pads text on the right to match a fixed width.
     */
    private String pad(String text, int width) {
        if (text.length() >= width) return text;
        return text + repeat(" ", width - text.length());
    }

    /**
     * Trims text to fit within a column width.
     */
    private String trimTo(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 1);
    }

    /**
     * Renders a boxed menu with a colored title.
     */
    private void printMenuBox(String title, String[] lines, String titleColor) {
        System.out.println("╔" + repeat("═", MENU_WIDTH) + "╗");
        String centered = center(title, MENU_WIDTH);
        System.out.println("║" + titleColor + centered + RESET + "║");
        System.out.println("╠" + repeat("═", MENU_WIDTH) + "╣");

        for (String line : lines) {
            String padded = pad(line, MENU_WIDTH);
            System.out.println("║" + GREEN + padded + RESET + "║");
        }

        System.out.println("╚" + repeat("═", MENU_WIDTH) + "╝");
    }

    /**
     * Utility method for repeating strings (Java 8 compatible).
     */
    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }
}
