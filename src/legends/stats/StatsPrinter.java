/**
 * File: StatsPrinter.java
 * Package: legends.stats
 *
 * Purpose:
 *   Renders a readable summary of game statistics to the console.
 *
 * Responsibilities:
 *   - Display overall game outcome and timing information
 *   - Print per-hero performance statistics
 *   - Present aggregated data in a human-readable format
 */
package legends.stats;

import legends.characters.Hero;

public class StatsPrinter {

    /**
     * Prints a formatted summary of the completed game session.
     */
    public static void printGameSummary(GameStats stats) {
        if (stats == null) return;

        // Print overall game metadata
        System.out.println();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           GAME SUMMARY");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Mode   : " + stats.getMode());
        System.out.println("Result : " + stats.getResult());
        System.out.println("Started: " + stats.getStartedAt());
        System.out.println("Ended  : " + stats.getEndedAt());
        System.out.println();

        // Print per-hero statistics
        System.out.println("Per-Hero Performance:");
        System.out.println("───────────────────────────────────────");

        for (HeroStats hs : stats.getHeroStats()) {
            if (hs == null) continue;

            Hero h = hs.getHero();
            if (h == null) continue;

            System.out.println("Hero: " + h.getName() + "  (Lvl " + h.getLevel() + ")");
            System.out.println("  Damage Dealt   : " + (int) hs.getDamageDealt());
            System.out.println("  Damage Taken   : " + (int) hs.getDamageTaken());
            System.out.println("  Monsters Killed: " + hs.getMonstersKilled());
            System.out.println("  Times Fainted  : " + hs.getTimesFainted());
            System.out.println("  Gold Gained    : " + hs.getGoldGained());
            System.out.println("  XP Gained      : " + hs.getXpGained());
            System.out.println();
        }
    }
}
