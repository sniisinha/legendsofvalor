/**
 * File: BattleActions.java
 * Package: legends.game.battle
 *
 * Description:
 * Encapsulates the core *battle mechanics* for a turn-based fight:
 * - Hero actions (attack, cast spell, use potion, change equipment)
 * - Monster turn execution (select target, dodge check, apply damage)
 * - Spell side-effects (type-based debuffs)
 *
 */

package legends.game.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Armor;
import legends.items.Potion;
import legends.items.Spell;
import legends.items.Weapon;
import legends.characters.Party;

import java.util.List;

public class BattleActions {

    /**
     * Dependency that encapsulates user-driven selection logic (choosing monsters/items).
     * This keeps this class focused on mechanics rather than input handling.
     */
    private final BattleInput input;

    /**
     * Constructs a BattleActions service.
     *
     * @param input BattleInput used to choose targets and inventory items (composition).
     */
    public BattleActions(BattleInput input) {
        this.input = input;
    }

    // HERO ACTIONS

    /**
     * Executes a physical attack from a hero against a selected monster.
     *
     * Flow:
     * 1) Select a living target monster (delegated to BattleInput)
     * 2) Resolve dodge check
     * 3) Compute and apply damage
     *
     * @param hero     acting hero
     * @param monsters list of monsters in the current battle
     * @return true if the hero's turn is consumed; false if action was canceled/invalid
     */
    public boolean doAttack(Hero hero, List<Monster> monsters) {
        // Target selection is delegated out to keep this method purely mechanical.
        Monster monster = input.chooseMonster(monsters);
        if (monster == null) return false;

        // Defensive programming: avoid spending a turn on an invalid target.
        if (monster.getHP() <= 0) {
            System.out.println("That monster is already down.");
            return false;
        }

        // Dodge is probabilistic; if dodged, the action still consumes the turn.
        if (Math.random() < monster.getDodgeChance()) {
            System.out.println(monster.getName() + " dodged the attack!");
            return true;
        }

        // Damage calculation is delegated to Hero (encapsulation of hero stats/equipment).
        double raw = hero.getAttackDamage();
        int dmg = (int) Math.round(raw);

        // Apply damage through Monster API (encapsulation of HP rules).
        monster.takeDamage(dmg);

        System.out.println("\u001B[92m" + hero.getName() + " hit "
                + monster.getName() + " for " + dmg + " damage!\u001B[0m");

        return true;
    }

    /**
     * Executes spell casting from a hero against a selected monster.
     *
     * Flow:
     * 1) Select a spell from hero inventory
     * 2) Validate mana cost (guard clause)
     * 3) Select target monster
     * 4) Spend mana, resolve dodge, compute damage
     * 5) Apply spell debuff + damage
     *
     * @param hero     acting hero
     * @param monsters list of monsters in the current battle
     * @return true if the hero's turn is consumed; false if canceled/invalid
     */
    public boolean doCastSpell(Hero hero, List<Monster> monsters) {
        Spell spell = input.chooseSpell(hero);
        if (spell == null) return false;

        // Uses Hero API to keep MP rules encapsulated in the Hero class.
        if (!hero.canCast(spell)) {
            System.out.println("\u001B[91mNot enough mana!\u001B[0m");
            return false;
        }

        Monster monster = input.chooseMonster(monsters);
        if (monster == null) return false;

        // Spend resource up-front; a dodge still consumes the casting attempt.
        hero.spendMana(spell.getManaCost());

        if (Math.random() < monster.getDodgeChance()) {
            System.out.println(monster.getName() + " dodged the spell!");
            return true;
        }

        // Spell damage scales with hero dexterity (business rule).
        double base = spell.getDamage();
        double raw = base + (hero.getDexterity() / 10000.0) * base;
        int dmg = (int) Math.round(raw);

        // Apply spell-specific side effects (debuffs) before damage is applied.
        applySpellEffect(spell, monster);

        monster.takeDamage(dmg);

        System.out.println(hero.getName() + " casts " + spell.getName()
                + " on " + monster.getName()
                + " for " + dmg + " damage!");

        return true;
    }

    /**
     * Uses a potion from the hero inventory and removes it after consumption.
     *
     * Note:
     * Potion effects are applied through Hero.usePotion(...) to keep stat mutation
     * encapsulated within the Hero domain model.
     *
     * @param hero acting hero
     * @return true if potion was used (turn consumed), false if canceled/none available
     */
    public boolean doUsePotion(Hero hero) {
        Potion p = input.choosePotion(hero);
        if (p == null) return false;

        // Apply effect through Hero (encapsulation of how stats change).
        hero.usePotion(p);

        // Remove item from inventory after successful use.
        hero.getInventory().removeItem(p);

        System.out.println("\u001B[92m" + hero.getName()
                + " used " + p.getName() + "!\u001B[0m");
        return true;
    }

    /**
     * Handles equipment changes for the acting hero (weapon/armor).
     *
     * Design:
     * - Selection and menu navigation are delegated to BattleInput.
     * - Equipment application is delegated to Hero (encapsulation).
     *
     * @param hero acting hero
     * @return true if an equipment change occurred (turn consumed), false if canceled/invalid
     */
    public boolean doChangeEquipment(Hero hero) {
        String choice = input.chooseEquipMenuOption();

        if ("1".equals(choice)) {
            Weapon w = input.chooseWeapon(hero);
            if (w == null) return false;

            // Equip through Hero API to centralize equipment state changes.
            hero.equipWeapon(w);
            System.out.println(hero.getName() + " equipped " + w.getName());
            return true;

        } else if ("2".equals(choice)) {
            Armor a = input.chooseArmor(hero);
            if (a == null) return false;

            hero.equipArmor(a);
            System.out.println(hero.getName() + " equipped " + a.getName());
            return true;

        } else if ("B".equals(choice)) {
            // Back means "no action taken" -> do not consume the turn.
            return false;

        } else {
            System.out.println("Invalid choice.");
            return false;
        }
    }

    // MONSTER TURN LOGIC

    /**
     * Executes the monsters' turn after all heroes have acted.
     *
     * Flow per monster:
     * 1) Skip dead monsters
     * 2) Choose a random living hero target (Party owns selection policy)
     * 3) Resolve hero dodge chance
     * 4) Compute monster damage and apply to hero
     *
     * @param party    party containing heroes (target selection policy lives here)
     * @param monsters list of monsters in the current battle
     */
    public void monsterTurn(Party party, List<Monster> monsters) {
        System.out.println("\n\u001B[91m━━━━━━━━ MONSTERS’ TURN ━━━━━━━━\u001B[0m");

        for (Monster m : monsters) {
            if (m.getHP() <= 0) continue;

            // Target selection is a Party responsibility (encapsulates "who can be targeted").
            Hero target = party.getRandomAliveHero();
            if (target == null) return; // no valid targets -> battle will end elsewhere

            // Dodge check is on the target hero (encapsulation of hero dodge mechanics).
            if (Math.random() < target.getDodgeChance()) {
                System.out.println(target.getName() + " dodged " + m.getName() + "'s attack!");
                continue;
            }

            // Monster damage rule: uses fixed conversion factor (business rule).
            double base = m.getDamage();
            int dmg = (int) Math.round(base * 0.3);

            // Apply damage through Hero API (armor reduction handled internally).
            target.takeDamage(dmg);

            System.out.println(m.getName() + " hit "
                    + target.getName() + " for " + dmg + " damage!");
        }
    }

    // SPELL DEBUFFS

    /**
     * Applies a spell-type-specific debuff to a monster.
     *
     * Note:
     * This method represents a simple "type switch" approach.
     * If you later want more extensibility, this can be replaced with a Strategy pattern
     * (e.g., SpellEffect interface implementations per SpellType).
     *
     * @param spell  the spell being cast
     * @param target monster receiving the debuff
     */
    private void applySpellEffect(Spell spell, Monster target) {
        double val;

        switch (spell.getType()) {
            case FIRE:
                // Fire reduces defense by 10%
                val = target.getDefense();
                target.setDefense(Math.max(0, val - val * 0.1));
                System.out.println("🔥 " + target.getName() + "'s defense reduced!");
                break;

            case ICE:
                // Ice reduces damage by 10%
                val = target.getDamage();
                target.setDamage(Math.max(0, val - val * 0.1));
                System.out.println("❄️ " + target.getName() + "'s damage reduced!");
                break;

            case LIGHTNING:
                // Lightning reduces dodge chance by 10%
                val = target.getDodgeChance();
                target.setDodgeChance(Math.max(0, val - val * 0.1));
                System.out.println("⚡ " + target.getName() + "'s dodge reduced!");
                break;

            default:
                // Defensive default: if new spell types are introduced, no debuff is applied.
                break;
        }
    }
}