/**
 * File: GameRecord.java
 * Package: legends.stats
 *
 * Purpose:
 *   Captures a complete snapshot of a finished game session.
 *
 * Responsibilities:
 *   - Store high-level game outcome and timing information
 *   - Aggregate per-hero performance statistics
 *   - Serve as a serializable record for persistence and leaderboard use
 */
package legends.stats;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameRecord implements Serializable {

    // Explicit version ID for serialization compatibility
    private static final long serialVersionUID = 1L;

    /**
     * Immutable per-hero statistics captured at the end of a game.
     */
    public static class HeroRecord implements Serializable {

        // Explicit version ID for nested serialization
        private static final long serialVersionUID = 1L;

        // Hero identity and progression state
        public final String heroName;
        public final int level;

        // Combat-related statistics
        public final int monstersKilled;
        public final int timesFainted;
        public final double damageDealt;
        public final double damageTaken;

        // Resource gains accumulated during the game
        public final int goldGained;
        public final int xpGained;

        /**
         * Constructs an immutable hero performance record.
         */
        public HeroRecord(String heroName, int level,
                          int monstersKilled, int timesFainted,
                          double damageDealt, double damageTaken,
                          int goldGained, int xpGained) {
            this.heroName = heroName;
            this.level = level;
            this.monstersKilled = monstersKilled;
            this.timesFainted = timesFainted;
            this.damageDealt = damageDealt;
            this.damageTaken = damageTaken;
            this.goldGained = goldGained;
            this.xpGained = xpGained;
        }
    }

    // Game mode played during this session
    public final GameStats.GameMode mode;

    // Final outcome of the game
    public final GameStats.GameResult result;

    // Timestamps marking game duration
    public final LocalDateTime startedAt;
    public final LocalDateTime endedAt;

    // Total number of rounds played
    public final int roundsPlayed;

    // Per-hero records collected for this session
    public final List<HeroRecord> heroes = new ArrayList<>();

    /**
     * Constructs a game record capturing high-level session data.
     */
    public GameRecord(GameStats.GameMode mode,
                      GameStats.GameResult result,
                      LocalDateTime startedAt,
                      LocalDateTime endedAt,
                      int roundsPlayed) {
        this.mode = mode;
        this.result = result;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.roundsPlayed = roundsPlayed;
    }
}
