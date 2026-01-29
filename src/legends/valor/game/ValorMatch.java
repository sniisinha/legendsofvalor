/**
 * File: ValorMatch.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Orchestrates a full Legends of Valor match from setup through repeated rounds.
 *
 * Responsibilities:
 *   - Initialize and coordinate match components (board, party, combat, turn manager)
 *   - Run the round loop until a win/lose/quit outcome occurs
 *   - Apply end-of-round rules (hero regeneration and periodic monster spawns)
 *   - Expose match results and statistics for post-game processing
 */
package legends.valor.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.market.Market;
import legends.stats.GameStats;
import legends.valor.combat.ValorCombat;
import legends.valor.turn.ConsoleValorInput;
import legends.valor.turn.ValorTurnManager;
import legends.valor.ui.ValorRoundStatusView;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;

public class ValorMatch {

    // Possible match outcomes returned to the game mode controller
    public enum Outcome { HERO_WIN, MONSTER_WIN, QUIT }

    // Shared input source passed into match components
    private final Scanner in;

    // Match components created during setup
    private ValorBoard board;
    private ValorMovement movement;
    private ValorCombat combat;

    // Match state: heroes, market access, and lane monsters
    private Party party;
    private Market market;
    private List<Monster> laneMonsters = new ArrayList<Monster>();

    // Runtime statistics used for summaries and leaderboard records
    private GameStats gameStats;
    private int roundsPlayed = 0;

    // Renders per-round status UI before each round begins
    private final ValorRoundStatusView statusView = new ValorRoundStatusView();

    // Monster spawn frequency in rounds
    private static final int SPAWN_INTERVAL = 4;

    // Spawner created once the board exists
    private ValorSpawner spawner;

    public ValorMatch(Scanner in) {
        this.in = in;
    }

    /**
     * Runs the full match loop until an outcome is reached.
     */
    public Outcome play() {
        // Display game rules and controls before starting setup
        new ValorIntroScreen(in).show();

        // Configure match dependencies via setup helper
        ValorMatchSetup setup = new ValorMatchSetup();
        boolean ok = setup.setup(this);
        if (!ok) return Outcome.QUIT;

        // Create spawner after board is initialized
        this.spawner = new ValorSpawner(board);

        // Turn manager owns hero/monster action sequencing for each round
        ValorTurnManager turnManager = new ValorTurnManager(
                board,
                movement,
                combat,
                party,
                laneMonsters,
                new ConsoleValorInput(in),
                market,
                in
        );

        while (true) {
            roundsPlayed++;

            // Display round banner/status prior to actions
            renderRoundStatus();

            // Run one full round; if an outcome is returned, the match ends
            Outcome outcome = turnManager.playOneRound();
            if (outcome != null) return outcome;

            // Apply end-of-round regeneration rules for living heroes
            endOfRound(party.getHeroes());

            // Periodically spawn new monsters and append them to lane state
            if (roundsPlayed % SPAWN_INTERVAL == 0) {
                List<Monster> spawned = spawner.spawnLaneMonsters(party);
                if (spawned != null && !spawned.isEmpty()) {
                    laneMonsters.addAll(spawned);
                }
            }
        }
    }

    /**
     * End-of-round regeneration rule:
     * Only alive heroes recover 10% of their computed max HP/MP, capped at max.
     */
    private void endOfRound(List<Hero> heroes) {
        if (heroes == null) return;

        for (Hero h : heroes) {
            if (h == null) continue;
            if (h.getHP() <= 0) continue;

            double maxHP = h.getLevel() * 100.0;
            double maxMP = h.getLevel() * 50.0;

            h.setHP(Math.min(maxHP, h.getHP() + 0.10 * maxHP));
            h.setMP(Math.min(maxMP, h.getMP() + 0.10 * maxMP));
        }
    }

    /**
     * Returns the GameStats instance associated with this match.
     */
    public GameStats getGameStats() { return gameStats; }

    /**
     * Returns the number of completed rounds played so far.
     */
    public int getRoundsPlayed() { return roundsPlayed; }

    /**
     * Setters used by the match setup component to inject dependencies.
     */
    void setBoard(ValorBoard board) { this.board = board; }
    void setMovement(ValorMovement movement) { this.movement = movement; }
    void setCombat(ValorCombat combat) { this.combat = combat; }
    void setParty(Party party) { this.party = party; }
    void setMarket(Market market) { this.market = market; }
    void setLaneMonsters(List<Monster> laneMonsters) { this.laneMonsters = laneMonsters; }
    void setGameStats(GameStats gameStats) { this.gameStats = gameStats; }

    /**
     * Prints round status UI using the dedicated renderer.
     */
    private void renderRoundStatus() {
        statusView.printRoundStatus(board, roundsPlayed);
    }
}
