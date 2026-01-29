/**
 * ExplorationState handles overworld movement, map interactions, and
 * transitions into other game states such as Market or Battle.
 *
 * Responsibilities:
 * - Render the world map and party position
 * - Process movement input (W/A/S/D)
 * - Prevent invalid movement (walls, out-of-bounds)
 * - Trigger inventory menu on 'I'
 * - Trigger market when stepping on 'M'
 * - Randomly trigger battles when walking on normal tiles
 *
 * This class does NOT handle combat or inventory logic directly;
 * it only delegates to other states when appropriate.
 */

package legends.game;

import legends.world.WorldMap;
import legends.world.Position;
import legends.characters.Monster;
import legends.characters.Party;
import legends.data.MonsterFactory;

import java.util.List;
import java.util.Scanner;

public class ExplorationState implements GameState {

    private WorldMap map;       // Reference to the world map
    private Party party;        // Player's hero party
    private LegendsGame game;   // Main game controller
    private Scanner in = new Scanner(System.in);

    public ExplorationState(Party party, WorldMap map, LegendsGame game) {
        this.party = party;
        this.map = map;
        this.game = game;
    }

    @Override
    public void render() {
        // Display the map and current party position
        map.print(party.getPosition());

        // Display available controls
        System.out.println("Controls: W/A/S/D to move, 'I' to open Inventory, 'Q' to quit");
    }

    // Simple random chance for battles on normal tiles
    private boolean shouldStartBattle() {
        return Math.random() < 0.30;
    }

    @Override
    public void handleInput(String input) {

        if (input == null) return;
        input = input.trim();
        if (input.isEmpty()) return;

        Position current = party.getPosition();
        Position next = new Position(current.row, current.col);  // copy position for movement calculation

        // Handle command input
        switch (input.toUpperCase()) {

            case "I":
                // Player opens inventory screen
                System.out.println("\n\u001B[96mOpening Inventory...\u001B[0m");
                game.setState(new InventoryState(game));
                return;

            // Movement controls
            case "W": next.row -= 1; break;
            case "A": next.col -= 1; break;
            case "S": next.row += 1; break;
            case "D": next.col += 1; break;

            // Quit game
            case "Q":
                System.out.println("Quitting game...");
                System.exit(0);

            default:
                System.out.println("Invalid input!");
                return;
        }

        // Boundary check
        if (!map.inBounds(next)) {
            System.out.println("You can't move outside the map!");
            return;
        }

        // Tile accessibility check (e.g., cannot enter bushes/caves if the rules say so)
        if (!map.canMove(next)) {
            System.out.println("That tile is inaccessible!");
            return;
        }

        // Move party to the new tile
        party.moveTo(next);
        String symbol = map.getTile(next).getSymbol();

        // If player steps on a Market tile
        if (symbol.equals("M")) {
            System.out.println("You entered a Market!");
            game.setState(new MarketState(game, game.getMarket()));
            return;
        }

        // Determine if a battle should occur (only on non-market tiles)
        if (!symbol.equals("M") && Math.random() < 0.25) {
            showBattlePrompt();
            return;
        }
    }

    // Prompt player before beginning battle
    private void showBattlePrompt() {
        System.out.println("\n\u001B[91mA wild group of monsters appears!\u001B[0m");
        System.out.println("\nPress ENTER to begin the battle");
        System.out.println("Press Q to flee back to safety");

        while (true) {
            String choice = in.nextLine().trim().toUpperCase();

            if (choice.isEmpty()) {
                // Generate enemies based on party strength
                List<Monster> enemies = MonsterFactory.generateMonstersForParty(game.getParty());

                // Transition to Battle State
                game.setState(new BattleState(game, enemies));
                return;
            }

            if (choice.equals("Q")) {
                System.out.println("You fled safely!");
                return;
            }

            System.out.println("Invalid input. Press ENTER to fight or Q to flee.");
        }
    }

    @Override
    public void update(LegendsGame game) {
        // Exploration has no recurring update logic
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}