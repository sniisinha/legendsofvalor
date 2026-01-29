/**
 * File: Party.java
 * Description: Represents the group of heroes the player controls.
 * Manages hero list, party movement, battle recovery, and status displays.
 */

package legends.characters;

import legends.ui.BarUtils;
import legends.world.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Party {

    private final List<Hero> heroes;   // all heroes in the party
    private Position position;         // current position on the map

    public Party() {
        this.heroes = new ArrayList<>();
        this.position = new Position(0, 0); // starting map location
    }

    // Returns the party’s current tile position
    public Position getPosition() {
        return position;
    }

    // Moves the party to a new map tile
    public void moveTo(Position p) {
        this.position = p;
    }

    // Adds a hero to the party
    public void addHero(Hero h) {
        heroes.add(h);
    }

    // Returns list of all heroes in the party
    public List<Hero> getHeroes() {
        return heroes;
    }

    // Number of heroes in the party
    public int size() {
        return heroes.size();
    }

    // Picks a random living hero (used by monsters during battle)
    public Hero getRandomAliveHero() {
        List<Hero> alive = new ArrayList<>();

        for (Hero h : heroes) {
            if (h.getHP() > 0)
                alive.add(h);
        }

        if (alive.isEmpty())
            return null;

        return alive.get(new Random().nextInt(alive.size()));
    }

    // Prints stats for every hero using HP/MP bars
    public void printStats() {
        System.out.println("\n\u001B[94m=== PARTY STATS ===\u001B[0m");

        for (Hero h : heroes) {

            double maxHP = h.getLevel() * 100;
            double maxMP = h.getLevel() * 50;

            System.out.println("\n\u001B[93m" + h.getName() +
                    " (Level " + h.getLevel() + ")\u001B[0m");

            System.out.println("HP: " +
                    BarUtils.makeBar(h.getHP(), maxHP, 15)
                    + " (" + (int) h.getHP() + "/" + (int) maxHP + ")");

            System.out.println("MP: " +
                    BarUtils.makeBar(h.getMP(), maxMP, 15)
                    + " (" + (int) h.getMP() + "/" + (int) maxMP + ")");

            System.out.println("STR: " + h.getStrength() +
                    " | DEX: " + h.getDexterity() +
                    " | AGI: " + h.getAgility() +
                    " | Gold: " + h.getGold());
        }
    }

    // Returns true only if every hero has fainted
    public boolean allDead() {
        for (Hero h : heroes) {
            if (h.getHP() > 0)
                return false;
        }
        return true;
    }

    // Returns true if at least one hero is alive
    public boolean anyAlive() {
        for (Hero h : heroes) {
            if (h.getHP() > 0)
                return true;
        }
        return false;
    }

    // Revives fainted heroes after a finished battle (50% HP)
    public void reviveFallenHeroes() {
        for (Hero h : heroes) {
            if (h.getHP() <= 0) {
                h.setHP(h.getLevel() * 50);
            }
        }
    }

    // Applies post-battle regeneration (10% recovery)
    public void regenerateAfterBattle() {
        for (Hero h : heroes) {

            // Alive heroes get HP/MP regen
            if (h.getHP() > 0) {
                double newHP = h.getHP() * 1.1;
                double newMP = h.getMP() * 1.1;

                double maxHP = 100 * h.getLevel();
                if (newHP > maxHP) newHP = maxHP;

                h.setHP(newHP);
                h.setMP(newMP);
            }

            // Fainted heroes revive at 50% HP and slight MP bonus
            else {
                double revivedHP = (100 * h.getLevel()) * 0.5;
                h.setHP(revivedHP);
                h.setMP(h.getMP() * 1.05);
            }
        }
    }

    // Returns only the heroes that are alive
    public List<Hero> getAliveHeroes() {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.getHP() > 0)
                alive.add(h);
        }
        return alive;
    }
}