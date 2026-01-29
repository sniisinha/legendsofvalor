package legends.game;

/**
 * Entry point of the complete Legends of Valor and Monsters & Heroes application
 * 
 * This class simply initializes the game engine and starts execution.
 * 
 * Main responsibilities:
 *  - Create a LegendsApp instance
 *  - Call run() to start intro → hero selection → gameplay loop
 */
public class Main {

    /**
     * Program entry point.
     * Creates the game and launches the main run sequence.
     */
        // Create the core game controller object
        public static void main(String[] args) {
        new LegendsApp().run();

    }
}