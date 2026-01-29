/**
 * File: HeroActionService.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Provides a unified facade for hero actions during Legends of Valor turns.
 *
 * Responsibilities:
 *   - Expose a stable public API for hero actions used by turn controllers
 *   - Delegate combat actions to HeroCombatActions
 *   - Delegate movement and board-rule actions to HeroMovementActions
 *   - Delegate equipment, inventory, and market actions to HeroEquipmentActions
 */
package legends.valor.turn;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.market.Market;
import legends.valor.combat.ValorCombat;
import legends.valor.world.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HeroActionService {

    // Encapsulates attack/cast spell logic and combat-related prompts
    private final HeroCombatActions combatActions;

    // Encapsulates movement rules including recall/teleport and obstacle interaction
    private final HeroMovementActions movementActions;

    // Encapsulates inventory/equipment usage and market access rules
    private final HeroEquipmentActions equipmentActions;

    public HeroActionService(ValorBoard board,
                             ValorMovement movement,
                             ValorCombat combat,
                             List<Monster> laneMonsters,
                             HeroTurnUIHelper ui,
                             Map<Hero, Integer> homeLane,
                             Market market,
                             Scanner scanner) {

        // Action classes isolate responsibilities while preserving this facade API
        this.combatActions = new HeroCombatActions(combat, laneMonsters, ui);
        this.movementActions = new HeroMovementActions(board, movement, ui, homeLane);
        this.equipmentActions = new HeroEquipmentActions(board, movement, ui, market, scanner);
    }

    /**
     * Executes a basic attack action for the given hero.
     */
    public boolean attack(Hero hero) {
        return combatActions.attack(hero);
    }

    /**
     * Executes a spell-cast action for the given hero.
     */
    public boolean castSpell(Hero hero) {
        return combatActions.castSpell(hero);
    }

    /**
     * Uses a potion from the hero's inventory if available.
     */
    public boolean usePotion(Hero hero) {
        return equipmentActions.usePotion(hero);
    }

    /**
     * Equips an item for the hero based on user input.
     */
    public boolean equip(Hero hero, ValorInput input) {
        return equipmentActions.equip(hero, input);
    }

    /**
     * Teleports the hero according to Valor movement rules.
     */
    public boolean teleport(Hero hero) {
        return movementActions.teleport(hero);
    }

    /**
     * Recalls the hero back to their home lane/nexus position.
     */
    public boolean recall(Hero hero) {
        return movementActions.recall(hero);
    }

    /**
     * Attempts to remove an obstacle based on the hero's location and input.
     */
    public boolean removeObstacle(Hero hero, ValorInput input) {
        return movementActions.removeObstacle(hero, input);
    }

    /**
     * Attempts to move the hero one tile in the given direction.
     */
    public boolean move(Hero hero, ValorDirection dir) {
        return movementActions.move(hero, dir);
    }

    /**
     * Ensures the hero has a home lane binding for recall/teleport rules.
     */
    public void bindHomeLaneIfMissing(Hero hero) {
        movementActions.bindHomeLaneIfMissing(hero);
    }

    /**
     * Opens the market for the hero if market access rules are satisfied.
     */
    public boolean openMarket(Hero hero) {
        return equipmentActions.openMarket(hero);
    }
}
