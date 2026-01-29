/**
 * File: ItemFactory.java
 * Description: Loads all item types (weapons, armor, potions, spells)
 *              from text files into usable Java objects.
 * Notes:
 *  - Uses FileUtils to read files from /src/data/
 *  - Each loader handles parsing its own item format
 *  - No game logic is modified
 */

package legends.data;

import legends.items.*;
import java.util.*;

public class ItemFactory {

    // Loads all weapons from a text file
    public static List<Weapon> loadWeapons(String filename) {
        List<Weapon> weapons = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true; // ignore first line

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue; // skip blank lines

            String[] parts = line.trim().split("\\s+");

            // File format: name price level damage hands
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int damage = Integer.parseInt(parts[3]);
            int hands = Integer.parseInt(parts[4]);

            weapons.add(new Weapon(name, price, level, damage, hands));
        }

        return weapons;
    }

    // Loads all armor pieces
    public static List<Armor> loadArmor(String filename) {
        List<Armor> armorList = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");

            // File format: name price level reduction
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int reduction = Integer.parseInt(parts[3]);

            armorList.add(new Armor(name, price, level, reduction));
        }

        return armorList;
    }

    // Loads all potions
    public static List<Potion> loadPotions(String filename) {
        List<Potion> potions = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");

            // File format: name price level effect attributes
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int effect = Integer.parseInt(parts[3]);

            List<PotionAttribute> attrs = new ArrayList<>();
            String attributeField = parts[4].trim().toUpperCase();

            // If potion boosts ALL attributes
            if (attributeField.equals("ALL")) {
                attrs.addAll(Arrays.asList(PotionAttribute.values()));
            } 
            else {
                // For formats like HEALTH/MANA/AGILITY
                String[] boosts = attributeField.split("/");
                for (String b : boosts) {
                    attrs.add(PotionAttribute.valueOf(b.trim()));
                }
            }

            potions.add(new Potion(name, price, level, effect, attrs));
        }

        return potions;
    }

    // Loads all spells of a specific type (Fire, Ice, Lightning)
    public static List<Spell> loadSpells(String filename, SpellType type) {
        List<Spell> spells = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");

            // File format: name price level damage manaCost
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int damage = Integer.parseInt(parts[3]);
            int manaCost = Integer.parseInt(parts[4]);

            spells.add(new Spell(name, price, level, damage, manaCost, type));
        }

        return spells;
    }
}