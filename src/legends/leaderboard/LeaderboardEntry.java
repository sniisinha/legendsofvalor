/**
 * File: LeaderboardEntry.java
 * Package: legends.leaderboard
 *
 * Purpose:
 *   Represents a single immutable entry in the game leaderboard.
 *
 * Responsibilities:
 *   - Store the outcome of a completed game session
 *   - Capture metadata such as game mode, result, score, and timestamp
 *   - Act as a serializable data record for persistence and retrieval
 */
package legends.leaderboard;

import legends.stats.GameStats;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LeaderboardEntry implements Serializable {

    // Explicit version ID for serialization compatibility
    private static final long serialVersionUID = 1L;

    // Timestamp indicating when the game session ended
    public final LocalDateTime endedAt;

    // Game mode played (e.g., Monsters & Heroes, Valor)
    public final GameStats.GameMode mode;

    // Final result of the game session (win, loss, etc.)
    public final GameStats.GameResult result;

    // Score achieved during the session
    public final int score;

    // Pre-formatted summary string for display purposes
    public final String summaryLine;

    /**
     * Constructs an immutable leaderboard entry.
     *
     * All fields are set at creation time and remain unchanged,
     * allowing this object to function as a safe data record.
     */
    public LeaderboardEntry(LocalDateTime endedAt,
                            GameStats.GameMode mode,
                            GameStats.GameResult result,
                            int score,
                            String summaryLine) {
        this.endedAt = endedAt;
        this.mode = mode;
        this.result = result;
        this.score = score;
        this.summaryLine = summaryLine;
    }
}
