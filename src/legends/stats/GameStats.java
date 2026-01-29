/**
 * File: GameStats.java
 * Package: legends.stats
 *
 * Purpose:
 *   Tracks and aggregates runtime statistics for a single game session.
 *
 * Responsibilities:
 *   - Record game lifecycle timing and outcome
 *   - Maintain per-hero performance statistics
 *   - Aggregate totals for scoring and leaderboard use
 *   - Compute a final score based on game performance
 */
package legends.stats;

import legends.characters.Hero;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class GameStats {

    /**
     * Identifies which game mode was played.
     */
    public enum GameMode {
        MONSTERS_AND_HEROES,
        LEGENDS_OF_VALOR
    }

    /**
     * Represents the final outcome of a game session.
     */
    public enum GameResult {
        HEROES_WIN,
        MONSTERS_WIN,
        QUIT
    }

    // Game mode for this session
    private final GameMode mode;

    // Timestamp when the game started
    private final LocalDateTime startedAt;

    // Timestamp when the game ended
    private LocalDateTime endedAt;

    // Final result of the game
    private GameResult result;

    // Number of completed rounds (hero phase + monster phase)
    private int rounds = 0;

    // Per-hero statistics, preserved in insertion order
    private final Map<Hero, HeroStats> heroStats = new LinkedHashMap<>();

    /**
     * Initializes game statistics for a new session.
     */
    public GameStats(GameMode mode, List<Hero> heroes) {
        this.mode = mode;
        this.startedAt = LocalDateTime.now();
        if (heroes != null) {
            for (Hero h : heroes) {
                heroStats.put(h, new HeroStats(h));
            }
        }
    }

    // Lifecycle tracking

    public GameMode getMode() { return mode; }

    public void addRound() { rounds++; }

    public int getRounds() { return rounds; }

    /**
     * Marks the game as ended and records its result.
     */
    public void markEnded(GameResult result) {
        this.result = result;
        this.endedAt = LocalDateTime.now();
    }

    public GameResult getResult() { return result; }

    public LocalDateTime getStartedAt() { return startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }

    /**
     * Returns the total duration of the game.
     */
    public Duration getDuration() {
        if (endedAt == null) return Duration.ZERO;
        return Duration.between(startedAt, endedAt);
    }

    // Hero statistics access

    /**
     * Returns statistics for all heroes in the game.
     */
    public Collection<HeroStats> getHeroStats() {
        return heroStats.values();
    }

    /**
     * Returns statistics for a specific hero.
     */
    public HeroStats statsFor(Hero hero) {
        return heroStats.get(hero);
    }

    // Aggregated totals (leaderboard use)


    public int totalKills() {
        int sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getMonstersKilled();
        return sum;
    }

    public int totalFaints() {
        int sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getTimesFainted();
        return sum;
    }

    public double totalDamageDealt() {
        double sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getDamageDealt();
        return sum;
    }

    public double totalDamageTaken() {
        double sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getDamageTaken();
        return sum;
    }

    public int totalGoldGained() {
        int sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getGoldGained();
        return sum;
    }

    public int totalXpGained() {
        int sum = 0;
        for (HeroStats hs : heroStats.values()) sum += hs.getXpGained();
        return sum;
    }

    /**
     * Computes a final score for the game session.
     *
     * Scoring heuristic:
     * - Win bonus / loss penalty
     * - Rewards kills and damage dealt
     * - Penalizes fainting and damage taken
     * - Small penalty for longer games (more rounds)
     */
    public int computeScore() {
        int score = 0;

        if (result == GameResult.HEROES_WIN) score += 1000;
        if (result == GameResult.MONSTERS_WIN) score -= 200;

        score += totalKills() * 200;
        score += (int) Math.round(totalDamageDealt() * 0.5);

        score -= totalFaints() * 150;
        score -= (int) Math.round(totalDamageTaken() * 0.2);

        score -= rounds * 10;

        return Math.max(score, 0);
    }
}
