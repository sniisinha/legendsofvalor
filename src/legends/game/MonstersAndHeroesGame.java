package legends.game;

/**
 * Wraps the existing Monsters & Heroes implementation
 * so it can be run via the Game interface.
 */
public class MonstersAndHeroesGame implements Game {

    @Override
    public void run() {
        // Your existing main game engine
        LegendsGame game = new LegendsGame();
        game.run();   // LegendsGame already has run()
    }
}
