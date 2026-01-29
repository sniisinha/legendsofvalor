/**
 * File: ValorLaneState.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Implements the interactive lane gameplay state for Legends of Valor.
 *
 * Responsibilities:
 *   - Render the Valor board and prompt the active hero for input
 *   - Interpret player commands for movement, hero cycling, and quitting
 *   - Delegate hero movement rules to ValorMovement
 *   - Trigger monster advancement after successful hero actions
 */
package legends.valor.game;

import java.util.List;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorDirection;
import legends.valor.world.ValorMovement;

public class ValorLaneState implements ValorState {

    // Parent game/controller reference for lane state orchestration
    private final ValorGame game;

    // Board used for rendering and movement constraints
    private final ValorBoard board;

    // Movement helper that validates and executes hero moves
    private final ValorMovement movement;

    // Party containing the heroes participating in this lane
    private final Party party;

    // Monsters assigned to this lane (advanced by AI each turn)
    private final List<Monster> laneMonsters;

    // AI responsible for monster advancement behavior
    private final ValorMonsterAI monsterAI;

    // Tracks which hero is currently active for player commands
    private int heroIndex = 0;

    // Marks whether this state should exit
    private boolean finished = false;

    public ValorLaneState(ValorGame game,
                          ValorBoard board,
                          ValorMovement movement,
                          Party party,
                          List<Monster> laneMonsters,
                          ValorMonsterAI monsterAI) {
        this.game = game;
        this.board = board;
        this.movement = movement;
        this.party = party;
        this.laneMonsters = laneMonsters;
        this.monsterAI = monsterAI;
    }

    /**
     * Returns the currently active hero for this lane turn.
     * Ensures the index is valid before accessing the party list.
     */
    private Hero getActiveHero() {
        List<Hero> heroes = party.getHeroes();
        if (heroes.isEmpty())
            throw new IllegalStateException("No heroes in party.");
        if (heroIndex < 0 || heroIndex >= heroes.size())
            heroIndex = 0;
        return heroes.get(heroIndex);
    }

    @Override
    public void render() {
        // Display the current state of the lane board
        board.print();

        // Prompt for the active hero's command
        Hero active = getActiveHero();
        System.out.println("Controls: W = up | A = left | S = down | D = right |");
        System.out.println("          N = next hero | Q = quit LoV");
        System.out.print("Enter command for " + active.getName() + ": ");
    }

    @Override
    public void handleInput(String input) {
        if (input == null) return;
        input = input.trim().toUpperCase();
        if (input.isEmpty()) return;

        Hero activeHero = getActiveHero();
        char cmd = input.charAt(0);

        switch (cmd) {
            case 'Q':
                finished = true;
                System.out.println("Exiting Legends of Valor...");
                return;

            case 'N':
                heroIndex = (heroIndex + 1) % party.getHeroes().size();
                return;

            case 'W': case 'A': case 'S': case 'D':
                boolean moved = handleHeroMovement(activeHero, cmd);
                if (!moved) {
                    System.out.println("Cannot move there!");
                } else {
                    // Monsters advance only after a successful hero action
                    doMonstersTurn();
                }
                return;

            default:
                System.out.println("Invalid command.");
        }
    }

    /**
     * Maps a movement command to a direction and delegates execution to ValorMovement.
     */
    private boolean handleHeroMovement(Hero hero, char cmd) {
        return switch (cmd) {
            case 'W' -> movement.moveHero(hero, ValorDirection.NORTH);
            case 'S' -> movement.moveHero(hero, ValorDirection.SOUTH);
            case 'A' -> movement.moveHero(hero, ValorDirection.WEST);
            case 'D' -> movement.moveHero(hero, ValorDirection.EAST);
            default -> false;
        };
    }

    /**
     * Advances living monsters in this lane using the provided AI controller.
     */
    private void doMonstersTurn() {
        if (laneMonsters == null || laneMonsters.isEmpty()) return;

        System.out.println("\n\u001B[31mMonsters advance toward your Nexus...\u001B[0m\n");

        for (Monster m : laneMonsters) {
            if (m.getHP() <= 0) continue;
            monsterAI.advanceMonster(m);
        }
    }

    @Override
    public void update(ValorGame game) {
        // Future: check for win/lose, respawn, combat triggers, etc.
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
