/**
 * File: MonsterTurnController.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Controls the monster phase of a Legends of Valor round.
 *
 * Responsibilities:
 *   - Iterate through active lane monsters and determine their action each phase
 *   - Use ValorCombat to attack heroes when targets are in range
 *   - Delegate monster movement decisions to ValorMonsterAI when no attack occurs
 *   - Flush buffered combat logs at safe points to preserve log ordering
 */
package legends.valor.turn;

import java.util.ArrayList;
import java.util.List;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.combat.ValorCombat;
import legends.valor.game.ValorMonsterAI;

import static legends.ui.ConsoleUI.*;

public class MonsterTurnController {

    // Combat engine used for range checks and attack resolution
    private final ValorCombat combat;

    // AI used to advance monsters when they are not attacking
    private final ValorMonsterAI ai;

    public MonsterTurnController(ValorCombat combat, ValorMonsterAI ai) {
        this.combat = combat;
        this.ai = ai;
    }

    /**
     * Executes the monster phase for the provided lane monster list.
     */
    public void monstersPhase(List<Monster> laneMonsters) {
        if (laneMonsters == null || laneMonsters.isEmpty()) return;

        // Combat is required for range/attack; AI is required for movement
        if (combat == null || ai == null) return;

        System.out.println();
        System.out.println(RED + "Monsters advance toward your Nexus..." + RESET);

        // Iterate on a snapshot to avoid concurrent modification while monsters die/remove
        for (Monster m : new ArrayList<Monster>(laneMonsters)) {
            if (m == null || m.getHP() <= 0) continue;

            // Decide between attack and movement based on current targets in range
            List<Hero> heroesInRange = combat.getHeroesInRange(m);

            if (heroesInRange != null && !heroesInRange.isEmpty()) {
                Hero target = heroesInRange.get(0);
                if (target != null && target.getHP() > 0) {
                    combat.monsterAttack(m, target);
                } else {
                    // If the chosen target is invalid, fall back to movement
                    flushCombatLogsSafely();
                    ai.advanceMonster(m);
                }
            } else {
                // Flush grouped logs before movement so output stays in correct phase order
                flushCombatLogsSafely();
                ai.advanceMonster(m);
            }
        }

        // Flush any remaining buffered logs at the end of the monster phase
        flushCombatLogsSafely();
    }

    /**
     * Flushes combat logs defensively to avoid crashes from logging failures.
     */
    private void flushCombatLogsSafely() {
        try {
            combat.flushLogs();
        } catch (Exception ignored) {
            // swallow to avoid crashing the match on logging issues
        }
    }
}
