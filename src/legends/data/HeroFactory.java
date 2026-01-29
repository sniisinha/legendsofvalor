/**
 * File: HeroFactory.java
 * Description: Loads hero data from text files and creates Hero objects
 *              (Warriors, Paladins, Sorcerers) based on their stats.
 * Notes:
 *  - Uses FileUtils to read hero attribute files.
 *  - Ensures each hero starts at level 1 with base HP = 100.
 *  - Correct ordering of stats is preserved for each hero type.
 */

package legends.data;

import legends.characters.*;
import java.util.*;

public class HeroFactory {

    // Load all warriors from a file
    public static List<Hero> loadWarriors(String filename) {
        return loadHeroesOfType(filename, "warrior");
    }

    // Load all paladins from a file
    public static List<Hero> loadPaladins(String filename) {
        return loadHeroesOfType(filename, "paladin");
    }

    // Load all sorcerers from a file
    public static List<Hero> loadSorcerers(String filename) {
        return loadHeroesOfType(filename, "sorcerer");
    }

    // Helper method that creates heroes based on the type string
    private static List<Hero> loadHeroesOfType(String filename, String type) {

        List<Hero> heroes = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true;

        // Process each line of the file
        for (String line : lines) {

            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] p = line.trim().split("\\s+");

            // Expected file format:
            // name  mana  strength  agility  dexterity  gold  experience
            String name = p[0];
            int mana = Integer.parseInt(p[1]);
            int strength = Integer.parseInt(p[2]);
            int agility = Integer.parseInt(p[3]);
            int dexterity = Integer.parseInt(p[4]);
            int startingMoney = Integer.parseInt(p[5]);
            int experience = Integer.parseInt(p[6]);

            Hero h = null;

            // Create the correct hero subclass
            switch (type) {

                case "warrior":
                    h = new Warrior(
                            name,
                            1,               // starting level
                            100,             // base HP
                            mana,
                            strength,
                            dexterity,       // correct stat order
                            agility
                    );
                    break;

                case "paladin":
                    h = new Paladin(
                            name,
                            1,
                            100,
                            mana,
                            strength,
                            dexterity,
                            agility
                    );
                    break;

                case "sorcerer":
                    h = new Sorcerer(
                            name,
                            1,
                            100,
                            mana,
                            strength,
                            dexterity,
                            agility
                    );
                    break;
            }

            // Set starting gold + experience
            h.setGold(startingMoney);
            h.setExperience(experience);

            heroes.add(h);
        }

        return heroes;
    }
}