/**
 * File: DataLoader.java
 * Description:
 *  Loads heroes, monsters, and items from text files.
 *  This class builds the initial game data used everywhere else.
 *  It reads the files in src/data/ and converts each line into objects.
 */

package legends.data;

import legends.characters.*;
import legends.items.*;
import java.util.*;

public class DataLoader {

    // Holds all monsters after loading so battles can generate from this list
    public static List<Monster> globalMonsters = new ArrayList<>();

    // Loads every hero type into one combined list
    public List<Hero> loadAllHeroes() {
        List<Hero> list = new ArrayList<>();
        list.addAll(loadWarriors());
        list.addAll(loadPaladins());
        list.addAll(loadSorcerers());
        return list;
    }

    // Loads every monster type from text files
    public List<Monster> loadAllMonsters() {
        List<Monster> list = new ArrayList<>();

        // Read all 3 monster categories
        list.addAll(MonsterFactory.loadMonsters("Dragons.txt", MonsterType.DRAGON));
        list.addAll(MonsterFactory.loadMonsters("Spirits.txt", MonsterType.SPIRIT));
        list.addAll(MonsterFactory.loadMonsters("Exoskeletons.txt", MonsterType.EXOSKELETON));

        globalMonsters = list; // Save for use in monster generation
        return list;
    }

    // Loads all shop items (weapons, armor, potions, spells)
    public List<Item> loadAllItems() {
        List<Item> list = new ArrayList<>();

        list.addAll(ItemFactory.loadWeapons("Weaponry.txt"));
        list.addAll(ItemFactory.loadArmor("Armory.txt"));
        list.addAll(ItemFactory.loadPotions("Potions.txt"));
        list.addAll(ItemFactory.loadSpells("FireSpells.txt", SpellType.FIRE));
        list.addAll(ItemFactory.loadSpells("IceSpells.txt", SpellType.ICE));
        list.addAll(ItemFactory.loadSpells("LightningSpells.txt", SpellType.LIGHTNING));

        return list;
    }

    // Reads Warriors.txt and converts each line into a Warrior hero
    public List<Warrior> loadWarriors() {
        List<Warrior> list = new ArrayList<>();
        List<String> lines = FileUtils.readResource("Warriors.txt");

        boolean skipHeader = true; // Skip the column names

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue; // Ignore empty lines

            String[] p = line.trim().split("\\s+");

            // File format: name MP STR AGI DEX GOLD EXP
            String name = p[0];
            int level = 1; // Start all heroes at level 1
            double mp  = Double.parseDouble(p[1]);
            double str = Double.parseDouble(p[2]);
            double agi = Double.parseDouble(p[3]);
            double dex = Double.parseDouble(p[4]);
            int gold   = Integer.parseInt(p[5]);
            int exp    = Integer.parseInt(p[6]);

            // HP uses PDF formula: HP = level × 100
            Warrior w = new Warrior(name, level, level * 100, mp, str, dex, agi);
            w.setGold(gold);
            w.setExperience(exp);

            list.add(w);
        }
        return list;
    }

    // Reads Paladins.txt
    public List<Paladin> loadPaladins() {
        List<Paladin> list = new ArrayList<>();
        List<String> lines = FileUtils.readResource("Paladins.txt");

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] p = line.trim().split("\\s+");

            String name = p[0];
            int level = 1;
            double mp  = Double.parseDouble(p[1]);
            double str = Double.parseDouble(p[2]);
            double agi = Double.parseDouble(p[3]);
            double dex = Double.parseDouble(p[4]);
            int gold   = Integer.parseInt(p[5]);
            int exp    = Integer.parseInt(p[6]);

            Paladin pal = new Paladin(name, level, level * 100, mp, str, dex, agi);
            pal.setGold(gold);
            pal.setExperience(exp);

            list.add(pal);
        }
        return list;
    }

    // Reads Sorcerers.txt
    public List<Sorcerer> loadSorcerers() {
        List<Sorcerer> list = new ArrayList<>();
        List<String> lines = FileUtils.readResource("Sorcerers.txt");

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] p = line.trim().split("\\s+");

            String name = p[0];
            int level = 1;
            double mp  = Double.parseDouble(p[1]);
            double str = Double.parseDouble(p[2]);
            double agi = Double.parseDouble(p[3]);
            double dex = Double.parseDouble(p[4]);
            int gold   = Integer.parseInt(p[5]);
            int exp    = Integer.parseInt(p[6]);

            Sorcerer s = new Sorcerer(name, level, level * 100, mp, str, dex, agi);
            s.setGold(gold);
            s.setExperience(exp);

            list.add(s);
        }
        return list;
    }
}