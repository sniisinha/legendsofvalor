/**
 * File: ValorTurnManager.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Coordinates turn execution for a Legends of Valor round.
 *
 * Responsibilities:
 *   - Run the hero phase and monster phase in correct order each round
 *   - Detect win/loss conditions based on Nexus reach checks
 *   - Maintain home-lane bindings for recall/respawn behavior
 *   - Handle end-of-round cleanup and respawn of defeated heroes
 *   - Track the current round number for UI views (status screen)
 */
package legends.valor.turn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.market.Market;
import legends.valor.combat.ValorCombat;
import legends.valor.game.ValorMatch;
import legends.valor.game.ValorMonsterAI;
import legends.valor.ui.ValorCombatLogView;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;
import legends.valor.world.ValorTile;

public class ValorTurnManager {

    // Default lane used when a hero has no known home lane binding
    private static final int DEFAULT_LANE = 1; // MID

    // Core match systems required for turn execution
    private final ValorBoard board;
    private final ValorMovement movement;
    private final ValorCombat combat;

    // Match participants for this round loop
    private final Party party;
    private final List<Monster> laneMonsters;

    // Controllers for hero and monster phases
    private final HeroTurnController heroTurnController;
    private final MonsterTurnController monsterTurnController;

    // Stores each hero's home lane used for recall and respawn placement
    private final Map<Hero, Integer> homeLane = new HashMap<Hero, Integer>();

    // UI logger used for end-of-round messages such as respawns
    private final ValorCombatLogView log = new ValorCombatLogView();

    // Tracks how many rounds have been played (1-based for UI readability)
    // NOTE: This is intentionally managed here so the round count remains consistent across controllers.
    private int roundCounter = 0;

    public ValorTurnManager(ValorBoard board,
                            ValorMovement movement,
                            ValorCombat combat,
                            Party party,
                            List<Monster> laneMonsters,
                            ValorInput input,
                            Market market,
                            Scanner scanner) {
        this.board = board;
        this.movement = movement;
        this.combat = combat;
        this.party = party;
        this.laneMonsters = laneMonsters;

        // RoundProvider allows the HeroTurnController (and its UI) to query the current round number
        // without needing direct access to this manager or its internal state.
        HeroTurnController.RoundProvider roundProvider = new HeroTurnController.RoundProvider() {
            @Override
            public int currentRound() {
                return roundCounter;
            }
        };

        // Hero controller receives shared lane map for recall/teleport rules + round provider for status UI
        this.heroTurnController = new HeroTurnController(
                board, movement, combat, laneMonsters, input, homeLane, market, scanner, roundProvider
        );

        // Monster controller uses combat for attacks and AI for advancement
        this.monsterTurnController =
                new MonsterTurnController(combat, new ValorMonsterAI(board, movement));
    }

    /**
     * Plays one full round.
     *
     * @return Outcome if the match ends this round, or null if play should continue
     */
    public ValorMatch.Outcome playOneRound() {
        // Increment at the start so UIs can display "Round 1" on the first round.
        roundCounter++; // advance round number at the start of each round

        if (party == null) return ValorMatch.Outcome.QUIT;

        List<Hero> heroes = party.getHeroes();
        if (heroes == null || heroes.isEmpty()) return ValorMatch.Outcome.QUIT;

        // Hero phase: each living hero takes one action
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h == null) continue;

            bindHomeLaneIfMissing(h);

            if (h.getHP() <= 0) continue;

            boolean ok = heroTurnController.handleHeroTurn(h, i + 1);
            if (!ok) return ValorMatch.Outcome.QUIT;

            // Check hero victory after each hero action
            if (board != null && board.heroesReachedEnemyNexus()) {
                flushAllLogs();
                return ValorMatch.Outcome.HERO_WIN;
            }
        }

        // Monster phase: monsters either attack or advance
        monsterTurnController.monstersPhase(laneMonsters);

        // Check monster victory after the monster phase completes
        if (board != null && board.monstersReachedHeroesNexus()) {
            flushAllLogs();
            return ValorMatch.Outcome.MONSTER_WIN;
        }

        // End-of-round housekeeping includes log flushing and hero respawn
        flushAllLogs();
        respawnDeadHeroes(heroes);
        flushAllLogs();

        return null;
    }

    /**
     * Flushes buffered combat and UI logs defensively.
     */
    private void flushAllLogs() {
        try {
            if (combat != null) combat.flushLogs();
        } catch (Exception ignored) {}

        try {
            if (log != null) log.flush();
        } catch (Exception ignored) {}
    }

    /**
     * Records a hero's home lane based on current position if not already bound.
     */
    private void bindHomeLaneIfMissing(Hero h) {
        if (h == null) return;
        if (homeLane.containsKey(h)) return;
        if (movement == null || board == null) return;

        int[] pos = movement.findHero(h);
        if (pos == null || pos.length < 2) return;

        int lane = board.getLane(pos[1]);
        if (lane >= 0) homeLane.put(h, Integer.valueOf(lane));
    }

    /**
     * Respawns heroes that have fainted by restoring HP/MP and placing them on their home Nexus.
     */
    private void respawnDeadHeroes(List<Hero> heroes) {
        if (heroes == null) return;

        for (Hero h : heroes) {
            if (h == null) continue;
            if (h.getHP() > 0) continue;

            int lvl = safeLevel(h);
            int maxHP = (int) Math.round(lvl * 100.0);
            int maxMP = (int) Math.round(lvl * 50.0);

            h.setHP(maxHP);
            h.setMP(maxMP);

            // Teleport back to home nexus and capture the placement location for logging
            int[] placed = teleportHeroToHomeNexus(h);

            int lane = DEFAULT_LANE;
            Integer laneObj = homeLane.get(h);
            if (laneObj != null) lane = laneObj.intValue();

            String laneName = laneName(lane);
            if (placed != null && placed.length >= 2) {
                log.respawned(h.getName(), laneName, placed[0], placed[1], maxHP, maxMP);
            } else {
                log.info("RESPAWN", h.getName() + " respawned at Nexus (" + laneName + ")");
            }
        }
    }

    /**
     * Safely reads the hero level and falls back to 1 if unavailable.
     */
    private int safeLevel(Hero h) {
        try {
            int lvl = h.getLevel();
            return (lvl <= 0) ? 1 : lvl;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Teleports the hero to an available heroes' Nexus cell in their home lane.
     *
     * @return the actual (row,col) where the hero was placed, or null if placement failed
     */
    private int[] teleportHeroToHomeNexus(Hero hero) {
        if (hero == null) return null;
        if (board == null || movement == null) return null;

        // Remove hero from current tile if present
        int[] cur = movement.findHero(hero);
        if (cur != null && cur.length >= 2) {
            ValorTile t = board.getTile(cur[0], cur[1]);
            if (t != null) t.removeHero();
        }

        Integer laneObj = homeLane.get(hero);
        int lane = (laneObj == null) ? DEFAULT_LANE : laneObj.intValue();

        int[] spawn = board.getHeroSpawnCell(lane);
        if (spawn == null || spawn.length < 2) return null;

        int r = spawn[0];
        int c = spawn[1];

        // If primary spawn is occupied, try the alternate nexus column
        if (!board.canHeroEnter(r, c)) {
            int[] cols = board.getNexusColumnsForLane(lane);
            if (cols != null && cols.length >= 2 && board.canHeroEnter(r, cols[1])) {
                c = cols[1];
            } else if (cols != null && cols.length == 1 && board.canHeroEnter(r, cols[0])) {
                c = cols[0];
            }
        }

        if (board.canHeroEnter(r, c)) {
            ValorTile dest = board.getTile(r, c);
            if (dest != null) {
                dest.placeHero(hero);
                return new int[]{r, c};
            }
        }

        return null;
    }

    /**
     * Converts lane index into a short label for UI logging.
     */
    private String laneName(int lane) {
        switch (lane) {
            case 0: return "TOP";
            case 1: return "MID";
            case 2: return "BOT";
            default: return "-";
        }
    }
}