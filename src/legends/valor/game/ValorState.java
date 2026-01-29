/**
 * File: ValorState.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Defines a common interface for Legends of Valor gameplay states.
 *
 * Responsibilities:
 *   - Standardize state rendering, input handling, and update behavior
 *   - Allow the game controller to transition between different states cleanly
 *   - Provide a consistent mechanism to determine when a state should exit
 */
package legends.valor.game;

public interface ValorState {

    /**
     * Renders this state's UI to the console.
     */
    void render();

    /**
     * Processes a single line of user input for this state.
     */
    void handleInput(String input);

    /**
     * Performs state updates independent of direct user input.
     */
    void update(ValorGame game);

    /**
     * Indicates whether the state has completed and should be exited.
     */
    boolean isFinished();
}
