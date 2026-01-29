/**
 * File: HeroCombatActions.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Encapsulates all hero combat-related actions in Legends of Valor.
 *
 * Responsibilities:
 *   - Execute basic attacks against monsters in range
 *   - Execute spell-casting actions with target selection
 *   - Coordinate with the combat engine for damage and effects
 *   - Clean up defeated monsters from active lane state
 */
package legends.valor.turn;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Spell;
import legends.valor.combat.ValorCombat;

import java.util.List;

public class HeroCombatActions {

    // Core combat engine handling damage, dodge, and effects
    private final ValorCombat combat;

    // Shared list of monsters currently active in the hero's lane
    private final List<Monster> laneMonsters;

    // UI helper used for interactive selection (targets, spells)
    private final HeroTurnUIHelper ui;

    public HeroCombatActions(ValorCombat combat,
                             List<Monster> laneMonsters,
                             HeroTurnUIHelper ui) {
        this.combat = combat;
        this.laneMonsters = laneMonsters;
        this.ui = ui;
    }

    /**
     * Performs a basic attack action for the given hero.
     * Prompts the user to select a valid monster target if multiple exist.
     */
    public boolean attack(Hero hero) {
        if (hero == null || combat == null) return false;

        // Determine which monsters are currently attackable
        List<Monster> inRange = combat.getMonstersInRange(hero);
        if (inRange == null || inRange.isEmpty()) {
            System.out.println("No monsters in attack range!");
            return false;
        }

        // Let the UI decide which monster is targeted
        Monster target = ui.pickMonster(inRange);
        if (target == null) return false;

        // Execute the attack through the combat engine
        combat.heroAttack(hero, target);

        // Remove defeated monsters from the lane list
        cleanupDeadMonsters();
        return true;
    }

    /**
     * Performs a spell-casting action for the given hero.
     * Requires both a valid spell and a valid monster target.
     */
    public boolean castSpell(Hero hero) {
        if (hero == null || combat == null) return false;

        // Retrieve available spells from the hero's inventory
        List<Spell> spells = ui.getSpells(hero.getInventory());
        if (spells == null || spells.isEmpty()) {
            System.out.println("You have no spells.");
            return false;
        }

        // Prompt the user to choose a spell
        Spell spell = ui.pickSpell(spells);
        if (spell == null) return false;

        // Determine which monsters are valid spell targets
        List<Monster> inRange = combat.getMonstersInRange(hero);
        if (inRange == null || inRange.isEmpty()) {
            System.out.println("No monsters in range to cast on!");
            return false;
        }

        // Prompt the user to choose a target monster
        Monster target = ui.pickMonster(inRange);
        if (target == null) return false;

        // Execute the spell through the combat engine
        boolean ok = combat.heroCastSpell(hero, spell, target);

        // Remove defeated monsters from the lane list
        cleanupDeadMonsters();
        return ok;
    }

    /**
     * Removes any monsters that are dead or invalid from the lane list.
     */
    public void cleanupDeadMonsters() {
        if (laneMonsters == null) return;
        laneMonsters.removeIf(m -> m == null || m.getHP() <= 0);
    }
}
