/**
 * File: LegendsApp.java
 * Package: legends.game
 *
 * Purpose:
 *   Serves as the main entry point for the Legends application.
 *
 * Responsibilities:
 *   - Display the top-level game selection menu
 *   - Accept and validate user input for game choice
 *   - Launch the selected game mode
 *   - Manage application-level control flow and exit behavior
 */
package legends.game;

import java.util.Scanner;
import legends.valor.game.ValorGame;

public class LegendsApp implements Game {

    // ANSI color codes for console styling
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String CYAN    = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String YELLOW  = "\u001B[33m";

    @Override
    public void run() {
        // Shared scanner for reading user input
        Scanner in = new Scanner(System.in);

        boolean running = true;
        while (running) {

            // Render application header
            System.out.println();
            System.out.println(MAGENTA + "══════════════════════════════════════════════" + RESET);
            System.out.println("          " + BOLD + "WELCOME TO MONSTERS & HEROES" + RESET);
            System.out.println(MAGENTA + "══════════════════════════════════════════════" + RESET);
            System.out.println();

            // Brief description of available choices
            System.out.println("Choose which adventure you want to play:");
            System.out.println();

            // Display game selection menu
            System.out.println(CYAN + "  [1] Legends: Monsters & Heroes" + RESET);
            System.out.println("      Classic exploration with random battles,");
            System.out.println("      markets, and turn-based combat.");
            System.out.println();
            System.out.println(CYAN + "  [2] Legends of Valor" + RESET);
            System.out.println("      3-lane tactical battle for control of the Nexuses.");
            System.out.println();
            System.out.println(YELLOW + "  [Q] Quit" + RESET);
            System.out.println();

            // Read and normalize user input
            System.out.print("Enter your choice (1 / 2 / Q): ");
            String input = in.nextLine().trim().toUpperCase();

            Game selectedGame = null;

            // Determine which game to launch based on input
            switch (input) {
                case "1":
                    selectedGame = new MonstersAndHeroesGame();
                    break;
                case "2":
                    selectedGame = new ValorGame();
                    break;
                case "Q":
                    running = false;
                    continue;   // skip launching a game and exit loop
                default:
                    System.out.println("\nInvalid choice. Please type 1, 2, or Q.\n");
                    continue;
            }

            // Launch the chosen game instance
            System.out.println();
            System.out.println(MAGENTA + "Launching " + gameName(selectedGame) + "..." + RESET);
            System.out.println();

            selectedGame.run();

            // Pause before returning to the main menu
            System.out.println();
            System.out.print("Press ENTER to return to the main menu...");
            in.nextLine();
        }

        // Final exit message
        System.out.println();
        System.out.println("Thanks for playing " + BOLD + "Legends" + RESET + "!");
    }

    /**
     * Resolves a user-friendly name for the selected game.
     */
    private String gameName(Game game) {
        if (game instanceof ValorGame) {
            return "Legends of Valor";
        } else if (game instanceof MonstersAndHeroesGame) {
            return "Legends: Monsters & Heroes";
        }
        return "the game";
    }
}
