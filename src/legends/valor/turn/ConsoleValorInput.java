/**
 * File: ConsoleValorInput.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Provides console-based input handling for Legends of Valor turn actions.
 *
 * Responsibilities:
 *   - Read raw input lines from the player through a shared Scanner
 *   - Interpret hero action commands (move, wait, attack, quit)
 *   - Delegate movement and combat execution to ValorMovement and ValorCombat
 *   - Support target selection when multiple monsters are in range
 */
package legends.valor.turn;

import java.util.List;
import java.util.Scanner;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.combat.ValorCombat;
import legends.valor.world.ValorDirection;
import legends.valor.world.ValorMovement;

public class ConsoleValorInput implements ValorInput {

    // Turn outcome used by command loops to decide whether to continue prompting
    public enum ActionResult { TURN_TAKEN, INVALID, QUIT }

    // Shared scanner used to read console input
    private final Scanner in;

    public ConsoleValorInput(Scanner in) {
        this.in = in;
    }

    /**
     * Reads a single input line after printing the given prompt.
     */
    @Override
    public String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    /**
     * Reads and applies a single hero turn command.
     * Delegates movement and attack resolution to the provided helpers.
     */
    public ActionResult takeHeroTurn(Hero hero, ValorMovement movement, ValorCombat combat, List<Monster> laneMonsters) {
        String line = readLine("Enter command: ").trim().toUpperCase();
        if (line.isEmpty()) return ActionResult.INVALID;

        char cmd = line.charAt(0);
        if (cmd == 'Q') return ActionResult.QUIT;

        switch (cmd) {
            case 'W': return movement.moveHero(hero, ValorDirection.NORTH) ? ActionResult.TURN_TAKEN : ActionResult.INVALID;
            case 'S': return movement.moveHero(hero, ValorDirection.SOUTH) ? ActionResult.TURN_TAKEN : ActionResult.INVALID;
            case 'A': return movement.moveHero(hero, ValorDirection.WEST)  ? ActionResult.TURN_TAKEN : ActionResult.INVALID;
            case 'D': return movement.moveHero(hero, ValorDirection.EAST)  ? ActionResult.TURN_TAKEN : ActionResult.INVALID;
            case 'N':
                System.out.println(hero.getName() + " waits this turn.");
                return ActionResult.TURN_TAKEN;
            case 'F':
                return handleAttack(hero, combat, laneMonsters) ? ActionResult.TURN_TAKEN : ActionResult.INVALID;
            default:
                return ActionResult.INVALID;
        }
    }

    /**
     * Handles target selection and performs a basic attack if possible.
     */
    private boolean handleAttack(Hero hero, ValorCombat combat, List<Monster> laneMonsters) {
        // Determine which monsters are valid attack targets for this hero
        List<Monster> inRange = combat.getMonstersInRange(hero);
        if (inRange.isEmpty()) {
            System.out.println("No monsters in attack range!");
            return false;
        }

        Monster target;
        if (inRange.size() == 1) {
            // Single target case: no selection needed
            target = inRange.get(0);
        } else {
            // Multiple targets: prompt the player to choose an index
            System.out.println("Monsters in range:");
            for (int i = 0; i < inRange.size(); i++) {
                Monster m = inRange.get(i);
                System.out.println((i + 1) + ". " + m.getName() + " (HP: " + (int) m.getHP() + ")");
            }
            try {
                int choice = Integer.parseInt(readLine("Choose target (number): ").trim());
                if (choice < 1 || choice > inRange.size()) return false;
                target = inRange.get(choice - 1);
            } catch (Exception e) {
                return false;
            }
        }

        // Execute attack and remove monster from the lane list if defeated
        boolean killed = combat.heroAttack(hero, target);
        if (killed) laneMonsters.remove(target);
        return true;
    }

}
