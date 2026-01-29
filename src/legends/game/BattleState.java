/**
 * File: BattleState.java
 * Package: legends.game
 *
 * Description:
 * Represents the active "battle mode" of the game using the GameState interface.
 * BattleState is responsible for orchestrating turn-based combat by:
 * - Tracking whose turn it is (hero turn order and current index)
 * - Delegating UI rendering to BattleView
 * - Delegating user selection/menus to BattleInput
 * - Delegating combat mechanics to BattleActions
 * - Delegating end-of-battle rules and rewards to BattleRoundService
 */

package legends.game;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.game.battle.BattleActions;
import legends.game.battle.BattleInput;
import legends.game.battle.BattleRoundService;
import legends.game.battle.BattleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleState implements GameState {

    /**
     * Reference to the main game controller used for state transitions.
     */
    private final LegendsGame game;

    /**
     * The player's party participating in this battle.
     */
    private final Party party;

    /**
     * Monsters participating in this battle instance.
     */
    private final List<Monster> monsters;

    /**
     * Shared input source for battle menus and selections.
     * Injected into BattleInput to avoid input logic inside BattleState.
     */
    private final Scanner in;

    /**
     * Collaborators that separate battle responsibilities into cohesive modules.
     */
    private final BattleView view;
    private final BattleInput input;
    private final BattleActions actions;
    private final BattleRoundService roundService;

    /**
     * Turn order of heroes for the current round (only alive heroes are included).
     */
    private List<Hero> turnOrder;

    /**
     * Index of the currently acting hero within the turn order.
     */
    private int turnIndex;

    /**
     * Constructs a new BattleState for a given encounter.
     *
     * @param game     main game controller
     * @param monsters monsters involved in the encounter
     */
    public BattleState(LegendsGame game, List<Monster> monsters) {
        this.game = game;
        this.party = game.getParty();
        this.monsters = monsters;

        this.in = new Scanner(System.in);

        // Compose battle subsystem modules.
        this.view = new BattleView();
        this.input = new BattleInput(in);
        this.actions = new BattleActions(input);
        this.roundService = new BattleRoundService();

        this.turnOrder = new ArrayList<Hero>();
        startNewRound(); // initialize round 1
    }

    /**
     * Initializes turn order for a new round.
     * Only living heroes are eligible to act.
     */
    private void startNewRound() {
        turnOrder = party.getAliveHeroes();
        turnIndex = 0;

        if (turnOrder != null && !turnOrder.isEmpty()) {
            System.out.println("\n\u001B[94m--- NEW ROUND ---\u001B[0m");
        }
    }

    /**
     * Returns the hero who is currently acting based on the turn order and index.
     *
     * @return current acting Hero, or null if no valid hero exists
     */
    private Hero currentHero() {
        if (turnOrder == null || turnOrder.isEmpty()) return null;
        if (turnIndex < 0 || turnIndex >= turnOrder.size()) return null;
        return turnOrder.get(turnIndex);
    }

    /**
     * Renders the battle UI by delegating to BattleView.
     * This keeps UI formatting out of the state/controller layer.
     */
    @Override
    public void render() {
        view.render(party, monsters, currentHero());
    }

    /**
     * Handles a single user command for the current acting hero.
     * BattleState decides when a turn is consumed and when to advance turn/round.
     *
     * @param raw raw user input command
     */
    @Override
    public void handleInput(String raw) {
        if (raw == null) return;

        String inputStr = raw.trim().toUpperCase();

        Hero actingHero = currentHero();
        if (actingHero == null) {
            System.out.println("No available hero to act.");
            return;
        }

        boolean turnConsumed = false;

        // Dispatch user choice to the appropriate action handler.
        if ("1".equals(inputStr)) {
            turnConsumed = actions.doAttack(actingHero, monsters);

        } else if ("2".equals(inputStr)) {
            turnConsumed = actions.doCastSpell(actingHero, monsters);

        } else if ("3".equals(inputStr)) {
            turnConsumed = actions.doUsePotion(actingHero);

        } else if ("4".equals(inputStr)) {
            turnConsumed = actions.doChangeEquipment(actingHero);

        } else if ("5".equals(inputStr)) {
            // Viewing stats does not consume a turn (non-mutating action).
            party.printStats();
            return;

        } else if ("Q".equals(inputStr)) {
            // Flee ends the battle immediately and returns to exploration.
            System.out.println("You fled the battle!");
            game.setState(new ExplorationState(game.getParty(), game.getMap(), game));
            return;

        } else {
            System.out.println("Invalid input!");
            return;
        }

        // If the action was canceled or invalid, the player retries the same turn.
        if (!turnConsumed) return;

        // Check for immediate battle end after hero action.
        if (roundService.areAllMonstersDead(monsters) || party.allDead()) {
            roundService.checkBattleEndAndTransition(game, party, monsters);
            return;
        }

        // Advance to next hero if there are heroes remaining in the turn order.
        if (turnIndex < turnOrder.size() - 1) {
            turnIndex++;
            return;
        }

        // All heroes have acted -> execute monsters' turn.
        actions.monsterTurn(party, monsters);

        // Check for battle end after monsters act.
        if (roundService.checkBattleEndAndTransition(game, party, monsters)) return;

        // If battle continues, regenerate resources and begin the next round.
        roundService.regenerateBetweenRounds(party);
        startNewRound();
    }

    /**
     * BattleState is entirely command-driven; there is no continuous ticking logic.
     */
    @Override
    public void update(LegendsGame game) {
        // no continuous update
    }

    /**
     * BattleState remains active until it transitions to a different GameState.
     *
     * @return false because BattleState exits via state transition rather than a finished flag
     */
    @Override
    public boolean isFinished() {
        return false;
    }
}