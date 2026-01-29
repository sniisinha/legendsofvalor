/**
 * File: HeroTurnUIHelper.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Provides reusable console-selection prompts for Legends of Valor hero turn actions.
 *
 * Responsibilities:
 *   - Prompt the player to select targets, items, and destinations from lists
 *   - Validate numeric selections and return the chosen object
 *   - Extract typed item lists (spells, potions, weapons, armor) from inventory
 *   - Centralize turn UI selection logic for reuse across action classes
 */
package legends.valor.turn;

import legends.characters.Hero;
import legends.characters.Inventory;
import legends.characters.Monster;
import legends.items.*;

import java.util.ArrayList;
import java.util.List;

public class HeroTurnUIHelper {

    // Input abstraction used by turn UI helpers for prompting the player
    private final ValorInput input;

    public HeroTurnUIHelper(ValorInput input) {
        this.input = input;
    }

    /**
     * Prompts the player to choose a monster target from a list.
     */
    public Monster pickMonster(List<Monster> monsters) {
        if (monsters == null || monsters.isEmpty()) return null;
        if (monsters.size() == 1) return monsters.get(0);

        System.out.println("Monsters in range:");
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            System.out.println((i + 1) + ") " + m.getName() + " (HP=" + (int) m.getHP() + ")");
        }
        int idx = readIndex("Choose monster #: ", monsters.size());
        return idx < 0 ? null : monsters.get(idx);
    }

    /**
     * Prompts the player to choose a spell from a list.
     */
    public Spell pickSpell(List<Spell> spells) {
        if (spells == null || spells.isEmpty()) return null;

        System.out.println("Spells:");
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            System.out.println((i + 1) + ") " + s.getName()
                    + " [type=" + s.getType()
                    + ", dmg=" + s.getDamage()
                    + ", mana=" + s.getManaCost() + "]");
        }
        int idx = readIndex("Choose spell #: ", spells.size());
        return idx < 0 ? null : spells.get(idx);
    }

    /**
     * Prompts the player to choose a potion from a list.
     */
    public Potion pickPotion(List<Potion> potions) {
        if (potions == null || potions.isEmpty()) return null;

        System.out.println("Potions:");
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            System.out.println((i + 1) + ") " + p.getName()
                    + " [+" + p.getEffectAmount() + " " + p.getAttributes() + "]");
        }
        int idx = readIndex("Choose potion #: ", potions.size());
        return idx < 0 ? null : potions.get(idx);
    }

    /**
     * Prompts the player to choose a weapon from a list.
     */
    public Weapon pickWeapon(List<Weapon> weapons) {
        if (weapons == null || weapons.isEmpty()) return null;

        System.out.println("Weapons:");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            System.out.println((i + 1) + ") " + w.getName()
                    + " [lvl=" + w.getRequiredLevel() + ", dmg=" + w.getDamage() + "]");
        }
        int idx = readIndex("Choose weapon #: ", weapons.size());
        return idx < 0 ? null : weapons.get(idx);
    }

    /**
     * Prompts the player to choose an armor piece from a list.
     */
    public Armor pickArmor(List<Armor> armors) {
        if (armors == null || armors.isEmpty()) return null;

        System.out.println("Armors:");
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            System.out.println((i + 1) + ") " + a.getName()
                    + " [lvl=" + a.getRequiredLevel() + ", red=" + a.getReduction() + "]");
        }
        int idx = readIndex("Choose armor #: ", armors.size());
        return idx < 0 ? null : armors.get(idx);
    }

    /**
     * Prompts the player to choose another hero (used for teleport targeting).
     */
    public Hero pickHero(List<Hero> heroes) {
        if (heroes == null || heroes.isEmpty()) return null;

        System.out.println("Teleport targets:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println((i + 1) + ") " + h.getName());
        }
        int idx = readIndex("Choose hero #: ", heroes.size());
        return idx < 0 ? null : heroes.get(idx);
    }

    /**
     * Prompts the player to choose a board coordinate from candidate destinations.
     */
    public int[] pickPosition(List<int[]> positions) {
        if (positions == null || positions.isEmpty()) return null;

        System.out.println("Teleport destinations:");
        for (int i = 0; i < positions.size(); i++) {
            int[] p = positions.get(i);
            System.out.println((i + 1) + ") (" + p[0] + "," + p[1] + ")");
        }
        int idx = readIndex("Choose destination #: ", positions.size());
        return idx < 0 ? null : positions.get(idx);
    }

    /**
     * Reads a 1-based selection index and converts it to a 0-based list index.
     */
    public int readIndex(String prompt, int size) {
        String s = input.readLine(prompt);
        if (s == null) return -1;
        s = s.trim();
        try {
            int v = Integer.parseInt(s);
            if (v < 1 || v > size) return -1;
            return v - 1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Extracts all Spell items from an inventory into a typed list.
     */
    public List<Spell> getSpells(Inventory inv) {
        List<Spell> out = new ArrayList<>();
        if (inv == null) return out;
        for (Item it : inv.getItems()) if (it instanceof Spell s) out.add(s);
        return out;
    }

    /**
     * Extracts all Potion items from an inventory into a typed list.
     */
    public List<Potion> getPotions(Inventory inv) {
        List<Potion> out = new ArrayList<>();
        if (inv == null) return out;
        for (Item it : inv.getItems()) if (it instanceof Potion p) out.add(p);
        return out;
    }

    /**
     * Extracts all Weapon items from an inventory into a typed list.
     */
    public List<Weapon> getWeapons(Inventory inv) {
        List<Weapon> out = new ArrayList<>();
        if (inv == null) return out;
        for (Item it : inv.getItems()) if (it instanceof Weapon w) out.add(w);
        return out;
    }

    /**
     * Extracts all Armor items from an inventory into a typed list.
     */
    public List<Armor> getArmors(Inventory inv) {
        List<Armor> out = new ArrayList<>();
        if (inv == null) return out;
        for (Item it : inv.getItems()) if (it instanceof Armor a) out.add(a);
        return out;
    }
}
