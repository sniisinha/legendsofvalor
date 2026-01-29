/**
 * The Battle class represents an entire fight between a party
 * of heroes and a list of monsters. It stores references to both
 * groups and will eventually contain the full battle sequence logic.
 */

package legends.battle;

import legends.characters.Party;
import legends.characters.Monster;
import java.util.List;

public class Battle {

    private Party heroes;           // party of heroes participating
    private List<Monster> monsters; // monsters generated for this battle

    /**
     * Creates a new battle with the given party and monsters.
     */
    public Battle(Party heroes, List<Monster> monsters) {
        this.heroes = heroes;
        this.monsters = monsters;
    }

    /**
     * Starts the battle. Currently only prints a placeholder message.
     * Full battle logic will be implemented later.
     */
    public void start() {
        System.out.println("A battle has started!");
    }
}