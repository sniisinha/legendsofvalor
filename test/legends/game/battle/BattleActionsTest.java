package legends.game.battle;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.items.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class BattleActionsTest {

    /** Returns preset selections instead of reading the console. */
    private static class FakeInput extends BattleInput {
        Monster monster;
        Spell spell;
        Potion potion;
        Weapon weapon;
        Armor armor;
        String menu = "B";

        FakeInput() { super(new Scanner("")); }

        @Override public Monster chooseMonster(List<Monster> monsters) { return monster; }
        @Override public Spell chooseSpell(Hero hero) { return spell; }
        @Override public Potion choosePotion(Hero hero) { return potion; }
        @Override public Weapon chooseWeapon(Hero hero) { return weapon; }
        @Override public Armor chooseArmor(Hero hero) { return armor; }
        @Override public String chooseEquipMenuOption() { return menu; }
    }

    private FakeInput input;
    private BattleActions actions;
    private Hero h;
    private Monster tank;
    private List<Monster> monsters;

    @BeforeEach
    void setUp() {
        input = new FakeInput();
        actions = new BattleActions(input);
        h = hero("Hero");
        tank = new Dragon("Tank", 5, 400, 100, 0.0);
        monsters = Collections.singletonList(tank);
        input.monster = tank;
    }

    @Test
    void attackDealsHeroDamage() {
        assertTrue(actions.doAttack(h, monsters));
        assertEquals(303, tank.getHP(), EPS); // 217 reduced by 100 defense
    }

    @Test
    void cancelledAttackDoesNotUseTheTurn() {
        input.monster = null;
        assertFalse(actions.doAttack(h, monsters));
    }

    @Test
    void attackingADeadMonsterDoesNotUseTheTurn() {
        tank.setHP(0);
        assertFalse(actions.doAttack(h, monsters));
    }

    @Test
    void dodgedAttackUsesTheTurnWithoutDamage() {
        tank.setDodgeChance(1.0);
        assertTrue(actions.doAttack(h, monsters));
        assertEquals(500, tank.getHP(), EPS);
    }

    @Test
    void spellDealsScaledDamageAndAppliesDebuff() {
        input.spell = new Spell("Flame", 1, 1, 100, 20, SpellType.FIRE);
        assertTrue(actions.doCastSpell(h, monsters));
        assertEquals(403, tank.getHP(), EPS); // 106 reduced by 90 defense after the debuff
        assertEquals(90, tank.getDefense(), 1e-6);
        assertEquals(30, h.getMP(), EPS);
    }

    @Test
    void iceAndLightningDebuffTheirStats() {
        tank.setDodgeChance(0.0);
        input.spell = new Spell("Frost", 1, 1, 1, 1, SpellType.ICE);
        actions.doCastSpell(h, monsters);
        assertEquals(360, tank.getDamage(), 1e-6);

        input.spell = new Spell("Bolt", 1, 1, 1, 1, SpellType.LIGHTNING);
        assertTrue(actions.doCastSpell(h, monsters));
        assertEquals(0, tank.getDodgeChance(), EPS);
    }

    @Test
    void spellWithoutManaOrSelectionDoesNotUseTheTurn() {
        assertFalse(actions.doCastSpell(h, monsters));

        input.spell = new Spell("Flame", 1, 1, 100, 51, SpellType.FIRE);
        assertFalse(actions.doCastSpell(h, monsters));
        assertEquals(50, h.getMP(), EPS);
    }

    @Test
    void cancellingSpellTargetKeepsMana() {
        input.spell = new Spell("Flame", 1, 1, 100, 20, SpellType.FIRE);
        input.monster = null;
        assertFalse(actions.doCastSpell(h, monsters));
        assertEquals(50, h.getMP(), EPS);
    }

    @Test
    void dodgedSpellStillCostsMana() {
        tank.setDodgeChance(1.0);
        input.spell = new Spell("Flame", 1, 1, 100, 20, SpellType.FIRE);
        assertTrue(actions.doCastSpell(h, monsters));
        assertEquals(500, tank.getHP(), EPS);
        assertEquals(30, h.getMP(), EPS);
    }

    @Test
    void potionIsAppliedAndConsumed() {
        Potion heal = new Potion("Heal", 1, 1, 10, Collections.singletonList(PotionAttribute.HEALTH));
        h.addItem(heal);
        input.potion = heal;

        assertTrue(actions.doUsePotion(h));

        assertEquals(110, h.getHP(), EPS);
        assertTrue(h.getInventory().getItems().isEmpty());
    }

    @Test
    void noPotionSelectedDoesNotUseTheTurn() {
        assertFalse(actions.doUsePotion(h));
    }

    @Test
    void equipmentMenuEquipsSelection() {
        input.menu = "1";
        input.weapon = new Weapon("Sword", 1, 1, 1, 1);
        assertTrue(actions.doChangeEquipment(h));
        assertSame(input.weapon, h.getWeapon());

        input.menu = "2";
        input.armor = new Armor("Plate", 1, 1, 1);
        assertTrue(actions.doChangeEquipment(h));
        assertSame(input.armor, h.getArmor());
    }

    @Test
    void backOrInvalidEquipmentChoiceDoesNotUseTheTurn() {
        input.menu = "B";
        assertFalse(actions.doChangeEquipment(h));
        input.menu = "?";
        assertFalse(actions.doChangeEquipment(h));
        input.menu = "1";
        assertFalse(actions.doChangeEquipment(h));
    }

    @Test
    void monstersHitLivingHeroes() {
        Party party = new Party();
        h.setHP(500);
        party.addHero(h);

        actions.monsterTurn(party, Arrays.asList(tank, tank));

        assertEquals(260, h.getHP(), EPS); // two hits of 400 * 0.3
    }

    @Test
    void deadMonstersDoNotAttack() {
        Party party = new Party();
        party.addHero(h);
        tank.setHP(0);

        actions.monsterTurn(party, monsters);

        assertEquals(100, h.getHP(), EPS);
    }

    @Test
    void monsterTurnEndsWhenNoHeroesRemain() {
        assertDoesNotThrow(() -> actions.monsterTurn(new Party(), monsters));
    }
}
