/**
 * File: DataLoaderTest.java
 * Description: Simple test harness that loads heroes, monsters,
 *              and items from files, then prints them. Also builds
 *              a Market object to display available items.
 */

package legends.data;

import legends.characters.*;
import legends.items.*;
import legends.market.Market;

import java.util.List;

public class DataLoaderTest {

    public static void main(String[] args) {

        // Create loader to read all game data files
        DataLoader loader = new DataLoader();

        // --- Load heroes ---
        System.out.println("=== Loading Heroes ===");
        List<Hero> heroes = loader.loadAllHeroes();

        // Print hero names and levels
        for (Hero h : heroes) {
            System.out.println(h.getName() + " (Lvl " + h.getLevel() + ")");
        }

        // --- Load monsters ---
        System.out.println("\n=== Loading Monsters ===");
        List<Monster> monsters = loader.loadAllMonsters();

        // Print monster names and levels
        for (Monster m : monsters) {
            System.out.println(m.getName() + " (Lvl " + m.getLevel() + ")");
        }

        // --- Load items ---
        System.out.println("\n=== Loading Items ===");
        List<Item> items = loader.loadAllItems();

        // Build a market using all available items
        Market market = new Market(items);

        // Print weapon list
        System.out.println("\n--- Weapons ---");
        for (Weapon w : market.getWeapons()) {
            System.out.println(w.getName() + " (Lvl " + w.getRequiredLevel() + ")");
        }

        // Print armor list
        System.out.println("\n--- Armor ---");
        for (Armor a : market.getArmor()) {
            System.out.println(a.getName() + " (Lvl " + a.getRequiredLevel() + ")");
        }

        // Print potion list
        System.out.println("\n--- Potions ---");
        for (Potion p : market.getPotions()) {
            System.out.println(p.getName() + " (Lvl " + p.getRequiredLevel() + ")");
        }

        // Print spell list
        System.out.println("\n--- Spells ---");
        for (Spell s : market.getSpells()) {
            System.out.println(s.getName() + " (Lvl " + s.getRequiredLevel() + ")");
        }
    }
}