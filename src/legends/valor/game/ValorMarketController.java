/**
 * File: ValorMarketController.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Controls market interactions during Legends of Valor gameplay.
 *
 * Responsibilities:
 *   - Open and manage a market session for the active hero
 *   - Delegate UI rendering to MarketView and input parsing to MarketInput
 *   - Execute buy/sell transactions through MarketService
 *   - Enforce turn-based policy that only the active hero may trade
 */
package legends.valor.game;

import legends.characters.Hero;
import legends.items.Item;
import legends.market.Market;

import legends.game.market.MarketInput;
import legends.game.market.MarketService;
import legends.game.market.MarketView;

import java.util.List;
import java.util.Scanner;

public class ValorMarketController {

    // Market inventory source used for buy flows
    private final Market market;

    // Shared market UI renderer (menus, tables, confirmations)
    private final MarketView view;

    // Shared input handler for parsing commands and numeric selections
    private final MarketInput input;

    // Service encapsulating transaction logic and inventory categorization
    private final MarketService service;

    public ValorMarketController(Market market, Scanner scanner) {
        this.market = market;
        this.view = new MarketView();
        this.input = new MarketInput(scanner);
        this.service = new MarketService();
    }

    /**
     * Runs the market loop for the currently active hero.
     * Returns when the user exits back to the game state.
     */
    public void openForHero(Hero hero) {
        if (hero == null) return;
        if (market == null) {
            System.out.println("Market not available.");
            return;
        }

        boolean running = true;
        while (running) {
            // Display top-level market options (buy, sell, back)
            view.printMainMenu();

            String cmd = input.readUpperTrimmedLine();
            if (cmd == null) continue;

            switch (cmd) {
                case "1":
                    buyFlow(hero);
                    break;
                case "2":
                    sellFlow(hero);
                    break;
                case "B":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void buyFlow(Hero hero) {
        while (true) {
            // Prompt for a market category to purchase from
            view.printBuyMenu();
            String choice = input.readUpperTrimmedLine();
            if (choice == null) continue;

            switch (choice) {
                case "1":
                    buyFromList(hero, market.getWeapons(), "BUY WEAPONS");
                    break;
                case "2":
                    buyFromList(hero, market.getArmor(), "BUY ARMOR");
                    break;
                case "3":
                    buyFromList(hero, market.getPotions(), "BUY POTIONS");
                    break;
                case "4":
                    buyFromList(hero, market.getSpells(), "BUY SPELLS");
                    break;
                case "B":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private <T extends Item> void buyFromList(Hero buyer, List<T> items, String title) {
        // Guard against empty categories
        if (items == null || items.isEmpty()) {
            System.out.println("No items available in this category.");
            return;
        }

        // Present available items in a consistent table format
        view.printItemTable(items, title);

        System.out.print("Select item number (0 = back): ");
        int choice = input.readIntOrCancel();

        // Return to category menu without purchasing
        if (choice == 0) return;
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid item.");
            return;
        }

        T item = items.get(choice - 1);

        // Enforce item usability constraint before purchasing
        if (buyer.getLevel() < item.getRequiredLevel()) {
            System.out.println("\u001B[91mLevel too low to use this item!\u001B[0m");
            return;
        }

        // Enforce affordability constraint before purchasing
        if (!buyer.canAfford(item)) {
            System.out.println("\u001B[91mNot enough gold!\u001B[0m");
            return;
        }

        // Execute transaction through service layer and confirm via view
        service.buy(buyer, item);
        view.printBuySuccess(buyer, item);

        System.out.print("Press ENTER to continue...");
        input.readUpperTrimmedLine();
    }

    private void sellFlow(Hero seller) {
        // Snapshot the seller's inventory grouped by item type for category navigation
        MarketService.InventoryCategories cats =
                service.categorizeInventory(seller.getInventory().getItems());

        while (true) {
            // Prompt for a category to sell from
            view.printSellMenu();
            String choice = input.readUpperTrimmedLine();
            if (choice == null) continue;

            switch (choice) {
                case "1":
                    sellFromList(seller, cats.weapons, "SELL WEAPONS");
                    break;
                case "2":
                    sellFromList(seller, cats.armors, "SELL ARMOR");
                    break;
                case "3":
                    sellFromList(seller, cats.potions, "SELL POTIONS");
                    break;
                case "4":
                    sellFromList(seller, cats.spells, "SELL SPELLS");
                    break;
                case "B":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private <T extends Item> void sellFromList(Hero seller, List<T> items, String title) {
        // Guard against empty categories
        if (items == null || items.isEmpty()) {
            System.out.println("No items to sell in this category.");
            return;
        }

        // Present owned items; value is computed by service at sale time
        view.printItemTable(items, title + " (Value = 50% Price)");

        System.out.print("Select an item number to sell (or 0 to cancel): ");
        int choice = input.readIntOrCancel();

        // Allow user to cancel and return to category menu
        if (choice == 0) return;
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid item.");
            return;
        }

        T item = items.get(choice - 1);

        // Execute transaction; remove from the displayed list to reflect updated state
        int value = service.sell(seller, item);
        items.remove(choice - 1);

        view.printSellSuccess(seller, item, value);

        System.out.print("Press ENTER to continue...");
        input.readUpperTrimmedLine();
    }
}
