/**
 * File: BattleInput.java
 * Package: legends.game.battle
 *
 * Description:
 * Handles all user-driven selection and input logic during battles.
 * This class is responsible for:
 * - Displaying choice menus (monsters, spells, potions, weapons, armor)
 * - Reading and validating user input
 * - Returning the selected domain objects to the caller
*/

package legends.game.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleInput {

    /**
     * Scanner used to read user input from standard input.
     * Injected to avoid hard-coupling input creation inside this class.
     */
    private final Scanner in;

    /**
     * Constructs a BattleInput handler.
     *
     * @param in Scanner instance used for reading user input
     */
    public BattleInput(Scanner in) {
        this.in = in;
    }

    /**
     * Prompts the user to choose a living monster from the given list.
     *
     * @param monsters list of all monsters in the battle
     * @return selected Monster, or null if no valid choice was made
     */
    public Monster chooseMonster(List<Monster> monsters) {
        List<Monster> alive = new ArrayList<Monster>();
        for (Monster m : monsters) {
            if (m.getHP() > 0) {
                alive.add(m);
            }
        }

        if (alive.isEmpty()) return null;

        System.out.println("\nChoose monster:");
        for (int i = 0; i < alive.size(); i++) {
            Monster m = alive.get(i);
            System.out.println((i + 1) + ". " + m.getName()
                    + " (HP: " + (int) m.getHP() + ")");
        }

        System.out.print("Enter number: ");
        int idx = readIndex(alive.size());
        return idx == -1 ? null : alive.get(idx);
    }

    /**
     * Prompts the user to choose a spell from the hero's inventory.
     *
     * @param hero acting hero
     * @return selected Spell, or null if no spell is chosen or available
     */
    public Spell chooseSpell(Hero hero) {
        List<Spell> spells = new ArrayList<Spell>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Spell) {
                spells.add((Spell) it);
            }
        }

        if (spells.isEmpty()) {
            System.out.println("No spells!");
            return null;
        }

        System.out.println("\nChoose Spell:");
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            System.out.println((i + 1) + ". " + s.getName()
                    + " | DMG: " + (int) s.getDamage()
                    + " | Mana: " + (int) s.getManaCost()
                    + " | Type: " + s.getType());
        }

        System.out.print("Enter number: ");
        int idx = readIndex(spells.size());
        return idx == -1 ? null : spells.get(idx);
    }

    /**
     * Prompts the user to choose a potion from the hero's inventory.
     *
     * @param hero acting hero
     * @return selected Potion, or null if no potion is chosen or available
     */
    public Potion choosePotion(Hero hero) {
        List<Potion> potions = new ArrayList<Potion>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Potion) {
                potions.add((Potion) it);
            }
        }

        if (potions.isEmpty()) {
            System.out.println("No potions available!");
            return null;
        }

        System.out.println("\nAvailable Potions:");
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            System.out.println((i + 1) + ". " + p.getName()
                    + " | +" + p.getEffectAmount()
                    + " (" + p.getAttributes() + ")");
        }

        System.out.print("Choose potion: ");
        int idx = readIndex(potions.size());
        return idx == -1 ? null : potions.get(idx);
    }

    /**
     * Prompts the user to choose a weapon from the hero's inventory.
     *
     * @param hero acting hero
     * @return selected Weapon, or null if no weapon is chosen or available
     */
    public Weapon chooseWeapon(Hero hero) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Weapon) {
                weapons.add((Weapon) it);
            }
        }

        if (weapons.isEmpty()) {
            System.out.println("No weapons!");
            return null;
        }

        System.out.println("\nAvailable Weapons:");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            System.out.println((i + 1) + ". " + w.getName()
                    + " | DMG: " + w.getDamage()
                    + " | Hands: " + w.getHands());
        }

        System.out.print("Choose: ");
        int idx = readIndex(weapons.size());
        return idx == -1 ? null : weapons.get(idx);
    }

    /**
     * Prompts the user to choose an armor item from the hero's inventory.
     *
     * @param hero acting hero
     * @return selected Armor, or null if no armor is chosen or available
     */
    public Armor chooseArmor(Hero hero) {
        List<Armor> armors = new ArrayList<Armor>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Armor) {
                armors.add((Armor) it);
            }
        }

        if (armors.isEmpty()) {
            System.out.println("No armor!");
            return null;
        }

        System.out.println("\nAvailable Armor:");
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            System.out.println((i + 1) + ". " + a.getName()
                    + " | Reduce: " + a.getReduction());
        }

        System.out.print("Choose: ");
        int idx = readIndex(armors.size());
        return idx == -1 ? null : armors.get(idx);
    }

    /**
     * Displays the equipment menu and returns the user's choice.
     *
     * @return menu option chosen by the user (normalized to uppercase)
     */
    public String chooseEquipMenuOption() {
        System.out.println("\n1. Equip Weapon");
        System.out.println("2. Equip Armor");
        System.out.println("B. Back");
        System.out.print("Choose: ");
        return in.nextLine().trim().toUpperCase();
    }

    /**
     * Reads a numeric index from user input and validates its range.
     *
     * @param size upper bound (exclusive) for valid indices
     * @return zero-based index if valid, or -1 if invalid input was provided
     */
    public int readIndex(int size) {
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idx < 0 || idx >= size) return -1;
            return idx;
        } catch (Exception e) {
            return -1;
        }
    }
}