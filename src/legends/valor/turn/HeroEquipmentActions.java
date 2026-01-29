/**
 * File: HeroEquipmentActions.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Encapsulates equipment, inventory usage, and market access actions for Valor heroes.
 *
 * Responsibilities:
 *   - Allow heroes to use consumables (potions) from inventory
 *   - Allow heroes to equip weapons and armor through a small selection UI
 *   - Enforce board-based rules for when the market can be opened
 *   - Delegate market interaction to ValorMarketController when permitted
 */
package legends.valor.turn;

import legends.characters.Hero;
import legends.items.Armor;
import legends.items.Potion;
import legends.items.Weapon;
import legends.market.Market;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;

import java.util.List;
import java.util.Scanner;

public class HeroEquipmentActions {

    // Board and movement are used to validate market access rules based on hero position
    private final ValorBoard board;
    private final ValorMovement movement;
    private final HeroTurnUIHelper ui;

    // Market dependencies used for opening a market session
    private final Market market;
    private final Scanner scanner;

    public HeroEquipmentActions(ValorBoard board,
                                ValorMovement movement,
                                HeroTurnUIHelper ui,
                                Market market,
                                Scanner scanner) {
        this.board = board;
        this.movement = movement;
        this.ui = ui;
        this.market = market;
        this.scanner = scanner;
    }

    /**
     * Uses a selected potion from the hero's inventory and removes it after use.
     */
    public boolean usePotion(Hero hero) {
        if (hero == null) return false;

        List<Potion> potions = ui.getPotions(hero.getInventory());
        if (potions == null || potions.isEmpty()) {
            System.out.println("You have no potions.");
            return false;
        }

        Potion p = ui.pickPotion(potions);
        if (p == null) return false;

        hero.usePotion(p);
        hero.getInventory().removeItem(p);

        System.out.println(hero.getName() + " used potion: " + p.getName());
        return true;
    }

    /**
     * Opens a simple equip menu and equips either a weapon or armor based on input.
     */
    public boolean equip(Hero hero, ValorInput input) {
        if (hero == null || input == null) return false;

        System.out.println(
                "Equip menu:\n" +
                "  1) Weapon\n" +
                "  2) Armor\n" +
                "  0) Cancel\n"
        );

        String line = input.readLine("Choose: ");
        if (line == null) return false;

        line = line.trim();
        if ("0".equals(line)) return false;

        if ("1".equals(line)) {
            List<Weapon> weapons = ui.getWeapons(hero.getInventory());
            if (weapons == null || weapons.isEmpty()) {
                System.out.println("No weapons to equip.");
                return false;
            }
            Weapon w = ui.pickWeapon(weapons);
            if (w == null) return false;

            hero.equipWeapon(w);
            System.out.println("Equipped weapon: " + w.getName());
            return true;
        }

        if ("2".equals(line)) {
            List<Armor> armors = ui.getArmors(hero.getInventory());
            if (armors == null || armors.isEmpty()) {
                System.out.println("No armors to equip.");
                return false;
            }
            Armor a = ui.pickArmor(armors);
            if (a == null) return false;

            hero.equipArmor(a);
            System.out.println("Equipped armor: " + a.getName());
            return true;
        }

        System.out.println("Invalid choice.");
        return false;
    }

    /**
     * Opens the market for the hero if the hero is currently standing on the heroes' Nexus.
     */
    public boolean openMarket(Hero hero) {
        if (hero == null || board == null || movement == null) return false;

        // Market access is location-based; resolve the hero's current tile
        int[] pos = movement.findHero(hero);
        if (pos == null) return false;

        if (!board.isHeroesNexus(pos[0], pos[1])) {
            System.out.println("You can only open the Market while on your Nexus.");
            return false;
        }

        // Market dependencies must be available to open a market session
        if (market == null || scanner == null) {
            System.out.println("Market is not wired yet.");
            return false;
        }

        new legends.valor.game.ValorMarketController(market, scanner).openForHero(hero);
        return true;
    }
}
