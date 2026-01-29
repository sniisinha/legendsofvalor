/**
 * File: LeaderboardService.java
 * Package: legends.leaderboard
 *
 * Purpose:
 *   Manages persistence and retrieval of leaderboard data.
 *
 * Responsibilities:
 *   - Convert completed game records into leaderboard entries
 *   - Persist leaderboard entries to disk
 *   - Load and sort leaderboard data for queries
 *   - Provide ranked and recent leaderboard views
 */
package legends.leaderboard;

import legends.stats.GameRecord;
import legends.stats.GameStats;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class LeaderboardService {

    // Directory where leaderboard data is stored
    private final Path dir;

    // Serialized leaderboard file path
    private final Path filePath;

    public LeaderboardService(String folderName) {
        // Java 8 compatible directory creation
        this.dir = Paths.get(folderName);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.out.println("[Leaderboard] Could not create directory: " + dir);
        }

        // Resolve the serialized leaderboard file within the directory
        this.filePath = dir.resolve("leaderboard.ser");
    }

    /**
     * Adds a completed game record to the leaderboard.
     */
    public void add(GameRecord record) {
        if (record == null) return;

        // Load existing entries, append new one, and persist
        List<LeaderboardEntry> entries = loadAll();
        entries.add(toEntry(record));
        saveAll(entries);
    }

    /**
     * Returns the top N leaderboard entries by score (descending).
     */
    public List<LeaderboardEntry> topByScore(int n) {
        if (n <= 0) return new ArrayList<LeaderboardEntry>();

        List<LeaderboardEntry> entries = loadAll();

        // Sort by score (desc), then by completion time (newer first)
        Comparator<LeaderboardEntry> cmp =
                Comparator.comparingInt((LeaderboardEntry e) -> e.score).reversed()
                        .thenComparing((LeaderboardEntry e) -> e.endedAt,
                                Comparator.nullsLast(Comparator.<Comparable>naturalOrder()))
                        .reversed();

        entries.sort(cmp);

        return sliceCopy(entries, n);
    }

    /**
     * Returns the most recent N leaderboard entries.
     */
    public List<LeaderboardEntry> recent(int n) {
        if (n <= 0) return new ArrayList<LeaderboardEntry>();

        List<LeaderboardEntry> entries = loadAll();

        // Sort by end time (newest first), nulls last
        entries.sort(
                Comparator.comparing((LeaderboardEntry e) -> e.endedAt,
                        Comparator.nullsLast(Comparator.<Comparable>naturalOrder()))
                        .reversed()
        );

        return sliceCopy(entries, n);
    }

    /**
     * Exposes the leaderboard file path (useful for debugging or UI display).
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Converts a GameRecord into a leaderboard entry.
     *
     * IMPORTANT:
     * Scoring must remain consistent with GameStats.computeScore().
     */
    private LeaderboardEntry toEntry(GameRecord r) {
        int totalKills = 0;
        int totalFaints = 0;
        int totalGold = 0;
        int totalXp = 0;
        double totalDealt = 0.0;
        double totalTaken = 0.0;

        // Aggregate hero-level statistics
        if (r.heroes != null) {
            for (GameRecord.HeroRecord h : r.heroes) {
                if (h == null) continue;
                totalKills += h.monstersKilled;
                totalFaints += h.timesFainted;
                totalGold += h.goldGained;
                totalXp += h.xpGained;
                totalDealt += h.damageDealt;
                totalTaken += h.damageTaken;
            }
        }

        // Compute score using the same logic as GameStats
        int score = computeScoreLikeGameStats(
                r.result,
                r.roundsPlayed,
                totalKills,
                totalFaints,
                totalDealt,
                totalTaken
        );

        // Build a concise summary line for display
        String summary = r.result
                + " | rounds=" + r.roundsPlayed
                + " | kills=" + totalKills
                + " | faints=" + totalFaints
                + " | dealt=" + (int) Math.round(totalDealt)
                + " | taken=" + (int) Math.round(totalTaken)
                + " | gold=" + totalGold
                + " | xp=" + totalXp;

        return new LeaderboardEntry(r.endedAt, r.mode, r.result, score, summary);
    }

    /**
     * Computes leaderboard score using the same formula as GameStats.
     */
    private int computeScoreLikeGameStats(GameStats.GameResult result,
                                         int rounds,
                                         int totalKills,
                                         int totalFaints,
                                         double totalDamageDealt,
                                         double totalDamageTaken) {
        int score = 0;

        // Base score adjustments based on game result
        if (result == GameStats.GameResult.HEROES_WIN) score += 1000;
        if (result == GameStats.GameResult.MONSTERS_WIN) score -= 200;

        // Positive contributions
        score += totalKills * 200;
        score += (int) Math.round(totalDamageDealt * 0.5);

        // Penalties
        score -= totalFaints * 150;
        score -= (int) Math.round(totalDamageTaken * 0.2);
        score -= rounds * 10;

        // Prevent negative scores
        return Math.max(score, 0);
    }

    /**
     * Loads all leaderboard entries from disk.
     */
    private List<LeaderboardEntry> loadAll() {
        if (!Files.exists(filePath)) return new ArrayList<LeaderboardEntry>();

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath.toFile()))
        )) {
            Object obj = in.readObject();

            // Validate file contents
            if (!(obj instanceof List<?>)) {
                System.out.println("[Leaderboard] Corrupt file (not a List). Resetting leaderboard.");
                return new ArrayList<LeaderboardEntry>();
            }

            @SuppressWarnings("unchecked")
            List<LeaderboardEntry> list = (List<LeaderboardEntry>) obj;

            // Remove any null entries caused by partial writes
            list.removeIf(Objects::isNull);

            return list;

        } catch (InvalidClassException ice) {
            System.out.println("[Leaderboard] Old/incompatible leaderboard file. Delete it to reset: " + filePath);
            return new ArrayList<LeaderboardEntry>();
        } catch (Exception e) {
            System.out.println("[Leaderboard] Failed to load leaderboard (" +
                    e.getClass().getSimpleName() + "). Resetting.");
            return new ArrayList<LeaderboardEntry>();
        }
    }

    /**
     * Persists all leaderboard entries to disk.
     */
    private void saveAll(List<LeaderboardEntry> entries) {
        if (entries == null) entries = new ArrayList<LeaderboardEntry>();

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath.toFile()))
        )) {
            out.writeObject(entries);
            out.flush();
        } catch (NotSerializableException nse) {
            System.out.println("[Leaderboard] SAVE FAILED: Something is not Serializable.");
        } catch (IOException e) {
            System.out.println("[Leaderboard] SAVE FAILED: " + e.getMessage());
        }
    }

    /**
     * Returns a defensive copy of the first N entries.
     */
    private List<LeaderboardEntry> sliceCopy(List<LeaderboardEntry> entries, int n) {
        if (entries == null || entries.isEmpty()) return new ArrayList<LeaderboardEntry>();
        if (entries.size() <= n) return new ArrayList<LeaderboardEntry>(entries);
        return new ArrayList<LeaderboardEntry>(entries.subList(0, n));
    }
}
