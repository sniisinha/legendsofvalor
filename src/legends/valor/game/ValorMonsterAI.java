/**
 * File: ValorMonsterAI.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Determines movement decisions for monsters during Legends of Valor gameplay.
 *
 * Responsibilities:
 *   - Choose movement directions for monsters based on lane-based rules
 *   - Prefer advancing toward the heroes' Nexus with fallback sidestep behavior
 *   - Delegate legality and execution of movement to ValorMovement
 *   - Add small randomness to reduce predictable sidestep patterns
 */
package legends.valor.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import legends.characters.Monster;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorDirection;
import legends.valor.world.ValorMovement;

public class ValorMonsterAI {

    // Board context is used to identify lane boundaries for sidesteps
    private final ValorBoard board;

    // Movement engine enforces legality and performs actual relocation
    private final ValorMovement movement;

    public ValorMonsterAI(ValorBoard board, ValorMovement movement) {
        this.board = board;
        this.movement = movement;
    }

    /**
     * Advances a monster by one step following the AI movement priorities.
     */
    public void advanceMonster(Monster monster) {
        if (monster == null) return;
        if (board == null || movement == null) return;

        // Primary objective: move toward the heroes' Nexus
        if (movement.moveMonster(monster, ValorDirection.SOUTH)) {
            return;
        }

        // If blocked, determine current lane so sidesteps remain within lane boundaries
        int[] pos = movement.findMonster(monster);
        if (pos == null || pos.length < 2) return;

        int col = pos[1];
        int lane = board.getLane(col);
        if (lane == -1) return;

        // Randomize sidestep order to reduce identical movement patterns across monsters
        List<ValorDirection> sides = new ArrayList<ValorDirection>();
        sides.add(ValorDirection.WEST);
        sides.add(ValorDirection.EAST);
        Collections.shuffle(sides);

        for (int i = 0; i < sides.size(); i++) {
            ValorDirection dir = sides.get(i);
            if (dir == null) continue;

            // Enforce lane constraint before attempting movement
            int toCol = col + dir.deltaCol();
            if (board.getLane(toCol) != lane) continue;

            if (movement.moveMonster(monster, dir)) {
                return;
            }
        }

        // Final fallback: attempt a small backward move if no forward/side move is possible
        movement.moveMonster(monster, ValorDirection.NORTH);
    }
}
