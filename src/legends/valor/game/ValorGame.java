/**
 * File: ValorGame.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Acts as the entry point for the Legends of Valor game mode.
 *
 * Responsibilities:
 *   - Start and run a Valor match session
 *   - Handle quit and replay flow for the game mode
 *   - Delegate post-game processing to the post-game controller
 *   - Integrate persistence and leaderboard services for end-of-game actions
 */
package legends.valor.game;

import java.util.*;

import legends.game.Game;
import legends.leaderboard.LeaderboardService;
import legends.persistence.SaveManager;
import legends.stats.GameStats;
import legends.valor.ui.ValorEndScreenRenderer;
import legends.characters.Hero;

public class ValorGame implements Game {

    // Shared scanner for all Valor game flow input
    private final Scanner in = new Scanner(System.in);

    // Manages save/load of the most recent completed game
    private final SaveManager saveManager = new SaveManager("saves");

    // Manages leaderboard persistence and queries
    private final LeaderboardService leaderboard = new LeaderboardService("saves");

    @Override
    public void run() {
        // Main loop supports consecutive matches until the user quits or declines replay
        while (true) {
            // Match encapsulates the core gameplay loop and produces an outcome
            ValorMatch match = new ValorMatch(in);
            ValorMatch.Outcome outcome = match.play();

            // If the match signals QUIT, exit the Valor game mode entirely
            if (outcome == ValorMatch.Outcome.QUIT) {
                System.out.println("Leaving Legends of Valor...");
                return;
            }

            // Post-game flow (leaderboard + summary + save/load UI)
            ValorEndScreenRenderer renderer = new ValorEndScreenRenderer();
            ValorPostGameController post =
                    new ValorPostGameController(in, saveManager, leaderboard, renderer);

            // Retrieve match statistics to drive scoring, summaries, and persistence
            GameStats stats = match.getGameStats();
            int roundsPlayed = match.getRoundsPlayed();
            post.handle(outcome, stats, roundsPlayed);

            // Ask whether to start a new match session
            if (!askPlayAgain()) {
                System.out.println("Returning to main menu...");
                return;
            }
        }
    }

    /**
     * Prompts the user to replay the Legends of Valor game mode.
     */
    private boolean askPlayAgain() {
        while (true) {
            System.out.print("\nPlay Legends of Valor again? (Y/N): ");
            String line = in.nextLine().trim().toUpperCase();
            if (line.startsWith("Y")) return true;
            if (line.startsWith("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

}
