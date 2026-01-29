/**
 * File: ValorPostGameController.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Manages end-of-match flow for Legends of Valor after a match concludes.
 *
 * Responsibilities:
 *   - Finalize game result and build a persistent GameRecord from GameStats
 *   - Update and query leaderboard data for end-screen display
 *   - Delegate end-screen rendering to ValorEndScreenRenderer
 *   - Provide save/load options for the most recent match record
 */
package legends.valor.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import legends.characters.Hero;
import legends.leaderboard.LeaderboardEntry;
import legends.leaderboard.LeaderboardService;
import legends.persistence.SaveManager;
import legends.stats.GameRecord;
import legends.stats.GameStats;
import legends.stats.HeroStats;
import legends.ui.ConsoleUI;
import legends.valor.ui.ValorEndScreenRenderer;

import static legends.ui.ConsoleUI.*;

public class ValorPostGameController {

    // Shared scanner for post-game input prompts
    private final Scanner in;

    // Persistence utility for saving/loading the last completed match
    private final SaveManager saveManager;

    // Leaderboard service for storing and retrieving ranked match entries
    private final LeaderboardService leaderboard;

    // Renderer responsible for displaying the end screen and loaded match views
    private final ValorEndScreenRenderer renderer;

    public ValorPostGameController(Scanner in,
                                   SaveManager saveManager,
                                   LeaderboardService leaderboard,
                                   ValorEndScreenRenderer renderer) {
        this.in = in;
        this.saveManager = saveManager;
        this.leaderboard = leaderboard;
        this.renderer = renderer;
    }

    /**
     * Executes the post-game flow:
     * finalize stats, update leaderboard, render end screen, and handle save/load input.
     */
    public void handle(ValorMatch.Outcome outcome, GameStats gameStats, int roundsPlayed) {
        // Guard against missing stats to avoid post-game crashes
        if (gameStats == null) {
            ConsoleUI.boxed("POST GAME", Arrays.asList(RED + "No stats available for this match." + RESET));
            return;
        }

        // Translate match outcome into GameStats result enum
        GameStats.GameResult result =
                (outcome == ValorMatch.Outcome.HERO_WIN) ? GameStats.GameResult.HEROES_WIN :
                (outcome == ValorMatch.Outcome.MONSTER_WIN) ? GameStats.GameResult.MONSTERS_WIN :
                GameStats.GameResult.QUIT;

        // Finalize lifecycle timestamps and outcome on the stats object
        gameStats.markEnded(result);

        // Build a serializable record used by persistence and leaderboard services
        GameRecord record = buildRecordFromStats(gameStats, roundsPlayed);

        // Attempt to update leaderboard; failure should not block post-game flow
        try { leaderboard.add(record); } catch (Exception ignored) {}

        List<LeaderboardEntry> top3;
        List<LeaderboardEntry> recent5;

        // Query leaderboard for ranked and recent views; degrade gracefully on failure
        try { top3 = leaderboard.topByScore(3); }
        catch (Exception e) { top3 = new ArrayList<LeaderboardEntry>(); }

        try { recent5 = leaderboard.recent(5); }
        catch (Exception e) { recent5 = new ArrayList<LeaderboardEntry>(); }

        // Render end screen summary, including leaderboard panels
        renderer.renderEndScreen(record, top3, recent5);

        // Prompt user for optional save/load actions before continuing
        while (true) {
            System.out.print("\n" + CYAN + "[S]" + RESET + " Save  "
                    + CYAN + "[L]" + RESET + " Load  "
                    + CYAN + "[ENTER]" + RESET + " Continue : ");

            String line = in.nextLine().trim().toUpperCase();
            if (line.isEmpty()) break;

            if (line.startsWith("S")) {
                try {
                    saveManager.saveLastGame(record);
                    ConsoleUI.boxed("SAVED", Arrays.asList(GREEN + "Saved last match to disk." + RESET));
                } catch (Exception e) {
                    ConsoleUI.boxed("SAVE FAILED", Arrays.asList(RED + e.getMessage() + RESET));
                }
            } else if (line.startsWith("L")) {
                try {
                    GameRecord loaded = saveManager.loadLastGame();
                    if (loaded == null) {
                        ConsoleUI.boxed("LOAD", Arrays.asList(RED + "No saved match found yet." + RESET));
                    } else {
                        renderer.renderLoadedMatch(loaded);
                    }
                } catch (Exception e) {
                    ConsoleUI.boxed("LOAD FAILED", Arrays.asList(RED + e.getMessage() + RESET));
                }
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Converts aggregated GameStats into a serializable GameRecord.
     */
    private GameRecord buildRecordFromStats(GameStats stats, int roundsPlayed) {
        GameRecord rec = new GameRecord(
                stats.getMode(),
                stats.getResult(),
                stats.getStartedAt(),
                stats.getEndedAt(),
                roundsPlayed
        );

        // Capture per-hero performance snapshot for persistence/leaderboard summaries
        for (HeroStats hs : stats.getHeroStats()) {
            Hero h = hs.getHero();
            rec.heroes.add(new GameRecord.HeroRecord(
                    h.getName(),
                    h.getLevel(),
                    hs.getMonstersKilled(),
                    hs.getTimesFainted(),
                    hs.getDamageDealt(),
                    hs.getDamageTaken(),
                    hs.getGoldGained(),
                    hs.getXpGained()
            ));
        }
        return rec;
    }
}
