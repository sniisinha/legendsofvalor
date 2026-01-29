/**
 * File: MarketInput.java
 * Package: legends.game.market
 *
 * Purpose:
 *   Handles all user input related to market interactions.
 *
 * Responsibilities:
 *   - Read and normalize console input
 *   - Parse numeric input with cancellation support
 *   - Validate hero selection for market transactions
 *   - Prevent invalid or out-of-range user selections
 *   - Provide clean, validated input to market controllers
 *   - Maintain separation between input handling and market logic
 */
package legends.game.market;

import legends.characters.Hero;
import legends.characters.Party;

import java.util.List;
import java.util.Scanner;

public class MarketInput {

    // Shared scanner for reading market-related input
    private final Scanner in;

    /**
     * Creates a MarketInput using an externally managed Scanner.
     *
     * @param in shared Scanner for console input
     */
    public MarketInput(Scanner in) {
        this.in = in;
    }

    /**
     * Reads a line of input and normalizes it
     * for consistent command handling.
     */
    public String readUpperTrimmedLine() {
        return in.nextLine().trim().toUpperCase();
    }

    /**
     * Reads an integer from input.
     * Returns 0 if parsing fails, treated as cancellation.
     */
    public int readIntOrCancel() {
        String line = in.nextLine().trim();
        int val;
        try {
            val = Integer.parseInt(line);
        } catch (Exception e) {
            System.out.println("Invalid number.");
            return 0;
        }
        return val;
    }

    /**
     * Prompts the user to select a hero for a market transaction.
     *
     * @return selected Hero, or null if cancelled or invalid
     */
    public Hero chooseHeroForTransaction(Party party, String verb, MarketView view) {
        List<Hero> heroes = party.getHeroes();
        if (heroes.isEmpty()) {
            System.out.println("No heroes in party!");
            return null;
        }

        view.printHeroTransactionHeader(verb);
        for (int i = 0; i < heroes.size(); i++) {
            view.printHeroLine(i + 1, heroes.get(i));
        }
        System.out.print("Enter number (0 = cancel): ");

        int idx = readIntOrCancel();
        if (idx == 0) return null;

        int zeroBased = idx - 1;
        if (zeroBased < 0 || zeroBased >= heroes.size()) {
            System.out.println("Invalid hero.");
            return null;
        }
        return heroes.get(zeroBased);
    }
}
