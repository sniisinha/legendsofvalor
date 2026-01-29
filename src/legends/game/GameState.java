package legends.game;

/**
 * Represents a generic state in the game.
 * 
 * Each game state (Exploration, Battle, Inventory, Market, etc.)
 * implements this interface to define:
 *  - how it renders itself to the screen,
 *  - how it responds to user input,
 *  - how it updates based on game logic,
 *  - and whether the state is finished and should transition.
 * 
 * This ensures all states follow the same structure,
 * making the game’s state machine clean and consistent.
 */
public interface GameState {

    /**
     * Draws or prints all visuals for this state.
     * Called each frame before input is handled.
     */
    void render();

    /**
     * Updates the internal logic of this state
     * (usually unused because the game is input-driven,
     * but included for future extensibility).
     *
     * @param game Reference to the main game object
     */
    void update(LegendsGame game);

    /**
     * Handles user input specific to this state.
     *
     * @param input The user's keystroke or command
     */
    void handleInput(String input);

    /**
     * Tells the game engine whether this state is complete
     * and should transition to another state.
     *
     * @return true if this state is done, false otherwise
     */
    boolean isFinished();
}