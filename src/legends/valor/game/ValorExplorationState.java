/**
 * File: ValorExplorationState.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Handles player-controlled exploration input for a single active hero in Legends of Valor.
 *
 * Responsibilities:
 *   - Display the current Valor board state during exploration
 *   - Read and interpret movement commands from the user
 *   - Delegate legal movement checks and execution to ValorMovement
 *   - Provide feedback for invalid commands and illegal moves
 */
package legends.valor.game;

import java.util.Scanner;

import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;
import legends.valor.world.ValorDirection;
import legends.characters.Hero;

public class ValorExplorationState {

    // Board rendering and spatial context for exploration
    private final ValorBoard board;

    // Movement helper that enforces movement rules on the board
    private final ValorMovement movement;

    // Console input source for exploration commands
    private final Scanner in;

    // The hero currently being controlled in this exploration state
    private final Hero activeHero;

    public ValorExplorationState(ValorBoard board, Hero activeHero) {
        this.board = board;
        this.movement = new ValorMovement(board);
        this.activeHero = activeHero;
        this.in = new Scanner(System.in);
    }

    public void run() {

        while (true) {

            // Render the current board before accepting the next command
            board.print();

            // Read a single-character command and normalize to uppercase
            System.out.println("Enter command (W/A/S/D to move, Q to quit): ");
            char cmd = in.nextLine().trim().toUpperCase().charAt(0);

            // Exit exploration loop on user quit
            if (cmd == 'Q') {
                break;
            }

            boolean moved = false;

            // Delegate movement execution to ValorMovement with a direction mapping
            switch (cmd) {
                case 'W': moved = movement.moveHero(activeHero, ValorDirection.NORTH); break;
                case 'S': moved = movement.moveHero(activeHero, ValorDirection.SOUTH); break;
                case 'A': moved = movement.moveHero(activeHero, ValorDirection.WEST); break;
                case 'D': moved = movement.moveHero(activeHero, ValorDirection.EAST); break;
                default:
                    System.out.println("Invalid command.");
            }

            // If command was valid but movement failed, report an illegal move
            if (!moved) {
                System.out.println("Illegal move!");
            }
        }
    }
}
