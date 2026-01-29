/**
 * LegendsGame
 *
 * Main game controller class responsible for:
 * - Initializing the map, market, hero party, and game states
 * - Running the intro screen and main game loop
 * - Managing transitions between different game states
 *
 * This class acts as the central engine of the game.
 */

package legends.game;

import java.util.Scanner;

import legends.world.WorldMap;
import legends.world.MapGenerator;
import legends.characters.Party;
import legends.data.DataLoader;
import legends.market.Market;
import legends.items.Item;

import java.util.List;

public class LegendsGame implements Game {

    // Current active game state (exploration, battle, inventory, etc.)
    private GameState state;

    // Core game components
    private WorldMap map;
    private Party party;
    private Market market;

    private final Scanner scanner = new Scanner(System.in);

    // ANSI color codes for text styling
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String CYAN  = "\u001B[96m";
    private static final String GREEN = "\u001B[92m";
    private static final String YELL  = "\u001B[93m";

    /**
     * Constructor initializes:
     * - A randomly generated world map
     * - An empty temporary party
     * - A market loaded with all items in the game
     * - Initial game state (exploration)
     */
    public LegendsGame() {

        // Generate a default 8×8 map
        this.map = MapGenerator.generate(8);

        // Initial empty party — actual heroes selected later
        this.party = new Party();

        // Load items and construct the market
        DataLoader loader = new DataLoader();
        List<Item> items = loader.loadAllItems();
        this.market = new Market(items);

        // Set the initial state before hero selection
        this.state = new ExplorationState(party, map, this);
    }

    /**
     * Main entry point for running the entire game.
     * Handles intro, hero selection, and transitions to exploration.
     */
    public void run() {

        showIntroScreen();  // Display the opening screen and instructions

        // Load game data
        DataLoader loader = new DataLoader();
        List<Item> items = loader.loadAllItems();
        DataLoader.globalMonsters = loader.loadAllMonsters();

        // Allow the player to pick heroes
        HeroSelection selection = new HeroSelection(loader);

        System.out.println(GREEN + "Welcome to Legends of Valor!" + RESET);
        this.party = selection.selectHeroes();

        // Set exploration as the starting state after hero selection
        this.state = new ExplorationState(party, map, this);

        // Begin the main gameplay loop
        gameLoop();
    }

    /**
     * Displays introduction, instructions, and waits for user confirmation.
     */
    private void showIntroScreen() {

        System.out.println();
        System.out.println(CYAN + "══════════════════════════════════════════════" + RESET);
        System.out.println("           🏰  " + BOLD + "LEGENDS OF VALOR" + RESET + "  🗡️");
        System.out.println(CYAN + "══════════════════════════════════════════════" + RESET);
        System.out.println();

        System.out.println(BOLD + "Welcome, traveler!" + RESET);
        System.out.println("This is Legends: Monsters and Heroes.");
        System.out.println(
                "Your journey begins in a world filled with danger,\n" +
                "magic, and legendary monsters. Build a powerful team\n" +
                "of heroes and guide them to victory.");
        System.out.println();

        // Gameplay explanation section
        System.out.println(YELL + BOLD + "HOW TO PLAY THE GAME:" + RESET);
        System.out.println();

        // • EXPLORE THE WORLD
        System.out.println(" • " + BOLD + "EXPLORE THE WORLD" + RESET);
        System.out.println("   Move across the map using W / A / S / D.");
        System.out.println("   Markets (M) offer supplies. Inaccessible tiles (X) cannot be crossed.");
        System.out.println("   Your party is shown as the symbol 'P'.");
        System.out.println();

        // • BUILD YOUR PARTY
        System.out.println(" • " + BOLD + "BUILD YOUR PARTY" + RESET);
        System.out.println("   Choose 1–3 heroes from Warriors, Paladins, and Sorcerers.");
        System.out.println("   Each class has unique strengths — mix them wisely.");
        System.out.println();

        // • MARKETS & ITEMS
        System.out.println(" • " + BOLD + "MARKETS & ITEMS" + RESET);
        System.out.println("   Visit markets to buy weapons, armor, spells, and potions.");
        System.out.println("   Open your inventory anytime with 'I'.");
        System.out.println("   Manage equipment per hero and optimize your loadout.");
        System.out.println();

        // • TURN-BASED BATTLES
        System.out.println(" • " + BOLD + "TURN-BASED BATTLES" + RESET);
        System.out.println("   When monsters appear, each hero acts in order:");
        System.out.println("      - Attack with weapons");
        System.out.println("      - Cast spells");
        System.out.println("      - Use potions");
        System.out.println("      - Change equipment");
        System.out.println("   After the heroes finish, monsters take their turn.");
        System.out.println("   Use strategy — dodges, debuffs, and mana matter!");
        System.out.println();

        // • PROGRESSION & RECOVERY
        System.out.println(" • " + BOLD + "PROGRESSION & RECOVERY" + RESET);
        System.out.println("   After each battle:");
        System.out.println("      - Heroes regenerate 10% HP & MP between rounds");
        System.out.println("      - Fallen heroes revive at 50% HP/MP if the party wins");
        System.out.println("      - All heroes gain XP & gold to grow stronger over time");
        System.out.println();

        // TIP section
        System.out.println(GREEN + BOLD + "TIP:" + RESET);
        System.out.println(" • Press 'I' during exploration to open your inventory");
        System.out.println(" • Press 'Q' during battle to flee");
        System.out.println(" • Safe tiles reduce the chance of random encounters");
        System.out.println();

        // Wait for player to start
        System.out.println(CYAN + "Press ENTER when you're ready to begin..." + RESET);
        scanner.nextLine();
    }

    /**
     * Core loop that drives rendering, input handling, and state updates.
     */
    private void gameLoop() {
        while (true) {

            // Draw the current state's interface
            state.render();

            // If state signals completion, exit loop
            if (state.isFinished()) {
                break;
            }

            // Accept player input
            System.out.print("");
            String input = scanner.nextLine();

            // Pass input to the state
            state.handleInput(input);

            // Perform any background updates
            state.update(this);
        }

        System.out.println("Thank you for playing Legends of Valor!");
    }

    // -----------------------------
    // State and component getters
    // -----------------------------
    public void setState(GameState newState) {
        this.state = newState;
    }

    public WorldMap getMap() {
        return map;
    }

    public Party getParty() {
        return party;
    }

    public Market getMarket() {
        return market;
    }
}