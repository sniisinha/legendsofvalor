/**
 * InventoryState
 *
 * Controls the inventory interface in the game.
 * Allows the player to:
 *   - Select a hero
 *   - View a hero’s inventory
 *   - Equip weapons or armor
 *   - Consume potions
 *
 * This state is reached from ExplorationState when the player presses 'I'.
 * It handles both rendering and input for managing hero equipment.
 */

package legends.game;

import legends.characters.*;
import legends.items.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryState implements GameState {

    private final LegendsGame game;
    private final Scanner in = new Scanner(System.in);

    // The hero currently being inspected inside the inventory menu.
    private Hero selectedHero = null;

    // ANSI colors for display
    private static final String RESET  = "\u001B[0m";
    private static final String CYAN   = "\u001B[96m";
    private static final String GREEN  = "\u001B[92m";
    private static final String YELLOW = "\u001B[93m";

    // Fixed menu width for consistent formatting
    private static final int WIDTH = 60;

    public InventoryState(LegendsGame game) {
        this.game = game;
    }

    /**
     * Renders either:
     *   - The hero selection menu (if no hero is chosen)
     *   - The inventory screen for a selected hero
     */
    @Override
    public void render() {
        if (selectedHero == null)
            printHeroSelection();
        else
            printHeroInventory(selectedHero);
    }

    // MAIN INVENTORY MENU — lets the user pick a hero
    private void printHeroSelection() {
        List<Hero> heroes = game.getParty().getHeroes();

        System.out.println("\n╔" + repeat("═", WIDTH) + "╗");
        System.out.println("║" + center(CYAN + "INVENTORY MENU" + RESET, WIDTH) + "║");
        System.out.println("╠" + repeat("═", WIDTH) + "╣");

        // Print each hero with basic stats
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);

            String line = String.format(
                "%d. %-15s  Lvl:%-3d  HP:%-5.0f  Gold:%-5d",
                (i + 1), h.getName(), h.getLevel(), h.getHP(), h.getGold()
            );

            System.out.println("║ " + pad(line, WIDTH - 2) + " ║");
        }

        System.out.println("║ " + pad("B. Back to Map", WIDTH - 2) + " ║");
        System.out.println("╚" + repeat("═", WIDTH) + "╝");
        System.out.print("Enter choice: ");
    }

    // HERO INVENTORY VIEW — shows equipment + actions for the chosen hero
    private void printHeroInventory(Hero h) {

        System.out.println("\n╔" + repeat("═", WIDTH) + "╗");
        System.out.println("║" + center(YELLOW + h.getName() + "'s INVENTORY" + RESET, WIDTH) + "║");
        System.out.println("╠" + repeat("═", WIDTH) + "╣");

        // Print full inventory contents formatted to WIDTH
        h.getInventory().printFormatted(WIDTH);

        // Separator before action buttons
        System.out.println("╠" + repeat("─", WIDTH) + "╣");

        // Inventory options
        System.out.println("║ " + pad("1. Equip Weapon", WIDTH - 2) + " ║");
        System.out.println("║ " + pad("2. Equip Armor", WIDTH - 2) + " ║");
        System.out.println("║ " + pad("3. Drink Potion", WIDTH - 2) + " ║");
        System.out.println("║ " + pad("B. Back", WIDTH - 2) + " ║");

        System.out.println("╚" + repeat("═", WIDTH) + "╝");
        System.out.print("Enter choice: ");
    }

    /**
     * Handles menu input:
     *  - If no hero selected → pick a hero
     *  - If hero selected → perform equipment or potion actions
     */
    @Override
    public void handleInput(String input) {

        input = input.trim().toUpperCase();

        // CASE 1 — Selecting a hero
        if (selectedHero == null) {

            // Back to map
            if (input.equals("B")) {
                game.setState(new ExplorationState(game.getParty(), game.getMap(), game));
                return;
            }

            // Attempt to read hero number
            int idx;
            try { idx = Integer.parseInt(input) - 1; }
            catch (Exception e) { System.out.println("Invalid input."); return; }

            List<Hero> heroes = game.getParty().getHeroes();

            if (idx < 0 || idx >= heroes.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            selectedHero = heroes.get(idx);
            return;
        }

        // CASE 2 — Hero is selected → perform inventory actions (Java 8 switch)
        switch (input) {
            case "1":
                equipWeapon(selectedHero);
                break;
            case "2":
                equipArmor(selectedHero);
                break;
            case "3":
                drinkPotion(selectedHero);
                break;
            case "B":
                selectedHero = null;  // Return to hero list
                break;
            default:
                System.out.println("Invalid input.");
                break;
        }
    }

    @Override public boolean isFinished() { return false; }
    @Override public void update(LegendsGame game) {}

    // EQUIP WEAPON — lets hero equip any weapon in their inventory
    private void equipWeapon(Hero hero) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (Item i : hero.getInventory().getItems()) {
            if (i instanceof Weapon) weapons.add((Weapon) i);
        }

        if (weapons.isEmpty()) { System.out.println("No weapons available."); return; }

        System.out.println("\nChoose a weapon:");
        for (int i = 0; i < weapons.size(); i++)
            System.out.println((i + 1) + ". " + weapons.get(i).getName());

        int idx = safeIndex(in.nextLine(), weapons.size());
        if (idx == -1) return;

        hero.equipWeapon(weapons.get(idx));
        System.out.println(GREEN + "Weapon equipped!" + RESET);
    }

    // EQUIP ARMOR — equips the chosen armor item
    private void equipArmor(Hero hero) {

        List<Armor> armors = new ArrayList<Armor>();
        for (Item i : hero.getInventory().getItems()) {
            if (i instanceof Armor) armors.add((Armor) i);
        }

        if (armors.isEmpty()) { System.out.println("No armor available."); return; }

        System.out.println("\nChoose armor:");
        for (int i = 0; i < armors.size(); i++)
            System.out.println((i + 1) + ". " + armors.get(i).getName());

        int idx = safeIndex(in.nextLine(), armors.size());
        if (idx == -1) return;

        hero.equipArmor(armors.get(idx));
        System.out.println(GREEN + "Armor equipped!" + RESET);
    }

    // DRINK POTION — applies potion effects and removes the used potion
    private void drinkPotion(Hero hero) {

        List<Potion> potions = new ArrayList<Potion>();
        for (Item i : hero.getInventory().getItems()) {
            if (i instanceof Potion) potions.add((Potion) i);
        }

        if (potions.isEmpty()) { System.out.println("No potions available."); return; }

        System.out.println("\nChoose potion:");
        for (int i = 0; i < potions.size(); i++)
            System.out.println((i + 1) + ". " + potions.get(i).getName());

        int idx = safeIndex(in.nextLine(), potions.size());
        if (idx == -1) return;

        hero.usePotion(potions.get(idx));
        hero.getInventory().removeItem(potions.get(idx));

        System.out.println(GREEN + "Potion consumed!" + RESET);
    }

    // Utility: Safely parse an index
    private int safeIndex(String input, int size) {
        try {
            int idx = Integer.parseInt(input.trim()) - 1;
            return (idx >= 0 && idx < size) ? idx : -1;
        } catch (Exception e) { return -1; }
    }

    // Centers text taking ANSI color codes into account
    private String center(String text, int width) {
        int pad = (width - stripColor(text).length()) / 2;
        return repeat(" ", pad) + text + repeat(" ", width - pad - stripColor(text).length());
    }

    // Removes color codes for consistent alignment
    private String stripColor(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    // Pads text to fixed width for box formatting
    private String pad(String text, int width) {
        int diff = width - stripColor(text).length();
        return text + repeat(" ", Math.max(diff, 0));
    }

    // Java 8 replacement for String.repeat(...)
    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }
}