/**
 * MarketState.java
 * Represents the in-game Market screen/state for Monsters & Heroes.
 *
 * This state is responsible for coordinating the buy/sell interaction loop:
 * - Rendering market menus
 * - Reading player choices
 * - Delegating display concerns to MarketView
 * - Delegating input parsing to MarketInput
 * - Delegating transaction logic to MarketService
 *
 * This design supports separation of concerns by keeping UI, input handling,
 * and business rules in dedicated collaborators.
 */

package legends.game;

import legends.characters.Hero;
import legends.characters.Party;
import legends.items.*;
import legends.market.Market;

import legends.game.market.MarketInput;
import legends.game.market.MarketService;
import legends.game.market.MarketView;

import java.util.List;
import java.util.Scanner;

/**
 * GameState implementation for when the player is inside a Market.
 * Uses composition to collaborate with MarketView/Input/Service rather than
 * mixing UI and business logic directly into the state.
 */
public class MarketState implements GameState {

    /** Reference to the main game context for state transitions and shared data. */
    private final LegendsGame game;

    /** The market inventory available for transactions. */
    private final Market market;

    /** Scanner used for reading console input (kept local to this state). */
    private final Scanner in = new Scanner(System.in);

    // Collaborators (separation of concerns)

    /** Responsible for rendering all market-related UI (menus, tables, messages). */
    private final MarketView view;

    /** Responsible for reading/parsing/validating market-related input from the console. */
    private final MarketInput input;

    /** Responsible for executing buy/sell business rules and inventory changes. */
    private final MarketService service;

    /** Tracks whether this state should continue accepting player input. */
    private boolean waitingForInput = true;

    /**
     * Constructs a MarketState with the required game context and market inventory.
     *
     * @param game   the main game instance used to transition back to other states
     * @param market the market inventory for buying and selling items
     */
    public MarketState(LegendsGame game, Market market) {
        this.game = game;
        this.market = market;

        // Initialize collaborators to keep responsibilities separated
        this.view = new MarketView();
        this.input = new MarketInput(in);
        this.service = new MarketService();
    }

    /**
     * Indicates whether the MarketState is finished and the game should transition out.
     *
     * @return true if the market has been exited, false otherwise
     */
    @Override
    public boolean isFinished() {
        return !waitingForInput;
    }

    /**
     * Renders the current market UI.
     * The market is menu-driven, so this prints the main market menu when active.
     */
    @Override
    public void render() {
        // Defensive guard: avoid rendering if we already exited the market
        if (!waitingForInput) return;

        // Print the main market menu (Buy/Sell/Back)
        view.printMainMenu();
    }

    /**
     * Handles a single player input command from the market main menu.
     * Commands:
     *  - "1" = Buy flow
     *  - "2" = Sell flow
     *  - "B" = Back to exploration
     *
     * @param raw raw user input string
     */
    @Override
    public void handleInput(String raw) {
        if (raw == null) return;

        // Normalize input for case-insensitive matching
        String inputStr = raw.trim().toUpperCase();

        // Main menu dispatch
        if ("1".equals(inputStr)) {
            handleBuyFlow();
            return;
        }

        if ("2".equals(inputStr)) {
            handleSellFlow();
            return;
        }

        // Exit market and return to exploration state
        if ("B".equals(inputStr)) {
            waitingForInput = false;

            // Transition back to exploration with existing party/map (preserves game context)
            game.setState(new ExplorationState(game.getParty(), game.getMap(), game));
            return;
        }

        // If none matched, input is invalid
        System.out.println("Invalid option.");
    }

    /**
     * Updates this state (called by the game loop).
     * Market is purely input-driven, so it performs no time-based updates.
     */
    @Override
    public void update(LegendsGame game) {
        // Market is input-driven; no continuous updates.
    }

    // Buy Flow

    /**
     * Blocking loop for the buy flow.
     * Displays category options and routes to purchase selection lists.
     */
    private void handleBuyFlow() {
        while (true) {
            // Show buy categories (Weapons/Armor/Potions/Spells/Back)
            view.printBuyMenu();

            // Read a normalized choice via MarketInput
            String choice = input.readUpperTrimmedLine();

            // Category routing (each category uses the shared Market inventory)
            if ("1".equals(choice)) {
                buyFromList(market.getWeapons(), "BUY WEAPONS");
            } else if ("2".equals(choice)) {
                buyFromList(market.getArmor(), "BUY ARMOR");
            } else if ("3".equals(choice)) {
                buyFromList(market.getPotions(), "BUY POTIONS");
            } else if ("4".equals(choice)) {
                buyFromList(market.getSpells(), "BUY SPELLS");
            } else if ("B".equals(choice)) {
                // Return to the market main menu
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Handles purchasing one item from a specific category list.
     * Uses generics to support any Item subtype without duplicating logic.
     *
     * Responsibilities:
     * - Validate market list availability
     * - Select the hero who will buy
     * - Select item index
     * - Enforce level + gold checks
     * - Delegate purchase execution to MarketService
     *
     * @param items category list from the market inventory
     * @param title UI title for the table header
     * @param <T>   concrete Item subtype (Weapon, Armor, Potion, Spell, ...)
     */
    private <T extends Item> void buyFromList(List<T> items, String title) {
        // No inventory in this category
        if (items == null || items.isEmpty()) {
            System.out.println("No items available in this category.");
            return;
        }

        // Choose which hero is making the transaction (keeps Party logic outside this class)
        Hero buyer = input.chooseHeroForTransaction(game.getParty(), "buy", view);
        if (buyer == null) return;

        // Display category inventory table
        view.printItemTable(items, title);

        // Choose item index
        System.out.print("Select item number (0 = back): ");
        int choice = input.readIntOrCancel();
        if (choice == 0) return;

        // Range validation
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid item.");
            return;
        }

        // Convert menu selection to list index
        T item = items.get(choice - 1);

        // Level check enforces item requirements
        if (buyer.getLevel() < item.getRequiredLevel()) {
            System.out.println("\u001B[91mLevel too low to use this item!\u001B[0m");
            return;
        }

        // Gold check enforces affordability
        if (!buyer.canAfford(item)) {
            System.out.println("\u001B[91mNot enough gold!\u001B[0m");
            return;
        }

        // Execute purchase (business logic lives in MarketService)
        service.buy(buyer, item);

        // Confirm purchase to the player
        view.printBuySuccess(buyer, item);

        // Pause so the player can read the result
        System.out.print("Press ENTER to continue...");
        in.nextLine();
    }

    // Sell Flow

    /**
     * Blocking loop for the sell flow.
     * Selects a hero seller first, then allows selling by category.
     */
    private void handleSellFlow() {
        // Choose which hero is selling (single seller for this sell session)
        Hero seller = input.chooseHeroForTransaction(game.getParty(), "sell", view);
        if (seller == null) return;

        // Categorize the seller inventory once to simplify menu-driven selling
        MarketService.InventoryCategories cats = service.categorizeInventory(seller.getInventory().getItems());

        while (true) {
            // Show sell categories (Weapons/Armor/Potions/Spells/Back)
            view.printSellMenu();

            String choice = input.readUpperTrimmedLine();

            // Category routing using categorized inventory views
            if ("1".equals(choice)) {
                sellFromList(seller, cats.weapons, "SELL WEAPONS");
            } else if ("2".equals(choice)) {
                sellFromList(seller, cats.armors, "SELL ARMOR");
            } else if ("3".equals(choice)) {
                sellFromList(seller, cats.potions, "SELL POTIONS");
            } else if ("4".equals(choice)) {
                sellFromList(seller, cats.spells, "SELL SPELLS");
            } else if ("B".equals(choice)) {
                // Return to the market main menu
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Handles selling a single item from the seller's inventory within a category list.
     * Uses generics to avoid duplicating category-specific sell logic.
     *
     * @param seller the hero whose inventory is being sold from
     * @param items  the category list representing a view into the inventory
     * @param title  UI title for the sell table
     * @param <T>    concrete Item subtype
     */
    private <T extends Item> void sellFromList(Hero seller, List<T> items, String title) {
        // Nothing to sell in the selected category
        if (items == null || items.isEmpty()) {
            System.out.println("No " + title.toLowerCase().replace("sell ", "") + " in inventory.");
            return;
        }

        // Display items with sell-value note
        view.printItemTable(items, title + " (Value = 50% Price)");

        System.out.print("Select an item number to sell (or 0 to cancel): ");
        int choice = input.readIntOrCancel();
        if (choice == 0) return;

        // Range validation
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid item.");
            return;
        }

        // Convert menu selection to list index
        T item = items.get(choice - 1);

        // Execute sale via service (handles gold + inventory updates)
        int value = service.sell(seller, item);

        // Keep the local category list consistent with what was sold
        items.remove(choice - 1);

        // Confirm the sale to the player
        view.printSellSuccess(seller, item, value);

        // Pause so the player can read the result
        System.out.print("Press ENTER to continue...");
        in.nextLine();
    }
}