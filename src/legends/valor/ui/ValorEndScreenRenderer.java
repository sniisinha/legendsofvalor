/**
 * File: ValorEndScreenRenderer.java
 * Package: legends.valor.ui
 *
 * Purpose:
 *   Renders the Legends of Valor post-game end screen and loaded-save summaries.
 *
 * Responsibilities:
 *   - Display leaderboard panels (top scores and recent matches)
 *   - Present a match summary with aggregated totals across heroes
 *   - Present per-hero performance statistics in a table format
 *   - Show post-game options (save, load, continue) using ConsoleUI utilities
 */
package legends.valor.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import legends.leaderboard.LeaderboardEntry;
import legends.stats.GameRecord;
import legends.stats.GameStats;
import legends.ui.ConsoleUI;

import static legends.ui.ConsoleUI.*;

public class ValorEndScreenRenderer {

    /**
     * Renders the full post-game end screen including leaderboards and match summaries.
     */
    public void renderEndScreen(GameRecord record, List<LeaderboardEntry> top3, List<LeaderboardEntry> recent5) {
        // Clear any stray ANSI artifacts before printing boxed UI
        ConsoleUI.clearLineJunk();

        // Leaderboard panels provide quick comparisons across games
        renderLeaderboard("LEADERBOARD (Top 3 by Score)", top3);
        renderLeaderboard("RECENT MATCHES (Last 5)", recent5);

        // Match summary and per-hero breakdown provide performance details
        renderMatchSummary(record);
        renderPerHeroStats(record);

        // Final prompt section describes available post-game actions
        ConsoleUI.boxed("POST-GAME OPTIONS",
                Arrays.asList(
                        CYAN + "[S]" + RESET + " Save last match",
                        CYAN + "[L]" + RESET + " Load last saved",
                        CYAN + "[ENTER]" + RESET + " Continue"
                )
        );
    }

    /**
     * Renders summary panels for a match loaded from disk.
     */
    public void renderLoadedMatch(GameRecord loaded) {
        ConsoleUI.boxed("LOADED LAST SAVE", Arrays.asList("Showing loaded match summary below:"));
        renderMatchSummary(loaded);
        renderPerHeroStats(loaded);
    }

    /**
     * Renders a leaderboard table in a boxed UI section.
     */
    private void renderLeaderboard(String title, List<LeaderboardEntry> entries) {
        List<String[]> rows = new ArrayList<String[]>();
        rows.add(new String[]{
                BOLD + "Rank" + RESET,
                BOLD + "Score" + RESET,
                BOLD + "Result" + RESET,
                BOLD + "Ended" + RESET,
                BOLD + "Summary" + RESET
        });

        if (entries == null || entries.isEmpty()) {
            rows.add(new String[]{"-", "-", "-", "-", DIM + "No games recorded yet." + RESET});
        } else {
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                rows.add(new String[]{
                        String.valueOf(i + 1),
                        String.valueOf(e.score),
                        colorResult(e.result),
                        String.valueOf(e.endedAt),
                        e.summaryLine
                });
            }
        }

        ConsoleUI.boxed(title, ConsoleUI.table(rows));
    }

    /**
     * Renders overall match information and aggregated totals across all heroes.
     */
    private void renderMatchSummary(GameRecord r) {
        List<String[]> sumRows = new ArrayList<String[]>();
        sumRows.add(new String[]{BOLD + "Mode" + RESET, String.valueOf(r.mode)});
        sumRows.add(new String[]{BOLD + "Result" + RESET, colorResult(r.result)});
        sumRows.add(new String[]{BOLD + "Rounds" + RESET, String.valueOf(r.roundsPlayed)});
        sumRows.add(new String[]{BOLD + "Start" + RESET, String.valueOf(r.startedAt)});
        sumRows.add(new String[]{BOLD + "End" + RESET, String.valueOf(r.endedAt)});

        // Aggregate totals for quick performance overview
        int kills = 0, faints = 0, gold = 0, xp = 0;
        double dealt = 0, taken = 0;
        for (GameRecord.HeroRecord h : r.heroes) {
            kills += h.monstersKilled;
            faints += h.timesFainted;
            gold += h.goldGained;
            xp += h.xpGained;
            dealt += h.damageDealt;
            taken += h.damageTaken;
        }

        sumRows.add(new String[]{BOLD + "Total Kills" + RESET, String.valueOf(kills)});
        sumRows.add(new String[]{BOLD + "Total Faints" + RESET, String.valueOf(faints)});
        sumRows.add(new String[]{BOLD + "Dmg Dealt" + RESET, String.valueOf((int) dealt)});
        sumRows.add(new String[]{BOLD + "Dmg Taken" + RESET, String.valueOf((int) taken)});
        sumRows.add(new String[]{BOLD + "Gold Gained" + RESET, String.valueOf(gold)});
        sumRows.add(new String[]{BOLD + "XP Gained" + RESET, String.valueOf(xp)});

        ConsoleUI.boxed("MATCH SUMMARY", ConsoleUI.table(sumRows));
    }

    /**
     * Renders a per-hero performance table for the given match record.
     */
    private void renderPerHeroStats(GameRecord record) {
        List<String[]> heroRows = new ArrayList<String[]>();
        heroRows.add(new String[]{
                BOLD + "Hero" + RESET,
                BOLD + "Lv" + RESET,
                BOLD + "Kills" + RESET,
                BOLD + "Faints" + RESET,
                BOLD + "Dealt" + RESET,
                BOLD + "Taken" + RESET,
                BOLD + "Gold" + RESET,
                BOLD + "XP" + RESET
        });

        for (GameRecord.HeroRecord h : record.heroes) {
            heroRows.add(new String[]{
                    h.heroName,
                    String.valueOf(h.level),
                    String.valueOf(h.monstersKilled),
                    String.valueOf(h.timesFainted),
                    String.valueOf((int) h.damageDealt),
                    String.valueOf((int) h.damageTaken),
                    String.valueOf(h.goldGained),
                    String.valueOf(h.xpGained)
            });
        }

        ConsoleUI.boxed("PER-HERO STATS", ConsoleUI.table(heroRows));
    }

    /**
     * Applies a color style to the match result for end-screen display.
     */
    private String colorResult(GameStats.GameResult r) {
        if (r == null) return "";
        switch (r) {
            case HEROES_WIN:   return GREEN + r + RESET;
            case MONSTERS_WIN: return RED + r + RESET;
            case QUIT:         return YELLOW + r + RESET;
            default:           return String.valueOf(r);
        }
    }
}
