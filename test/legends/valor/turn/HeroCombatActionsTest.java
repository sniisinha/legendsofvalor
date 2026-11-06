package legends.valor.turn;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Spell;
import legends.items.SpellType;
import legends.items.Weapon;
import legends.valor.combat.ValorCombat;
import legends.valor.world.ValorBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class HeroCombatActionsTest {

    private ValorBoard board;
    private ValorCombat combat;
    private List<Monster> laneMonsters;
    private Hero h;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        combat = new ValorCombat(board, null);
        laneMonsters = new ArrayList<Monster>();
        h = hero("Hero");
        place(board, h, 3, 3);
    }

    private HeroCombatActions actions(String... answers) {
        return new HeroCombatActions(combat, laneMonsters, new HeroTurnUIHelper(scriptedInput(answers)));
    }

    private Monster addMonster(Monster m, int row, int col) {
        place(board, m, row, col);
        laneMonsters.add(m);
        return m;
    }

    @Test
    void attackWithNoTargetsDoesNotUseTheTurn() {
        assertFalse(actions().attack(h));
    }

    @Test
    void attackAutoSelectsASingleTarget() {
        Monster m = addMonster(new Dragon("Tank", 5, 1, 1, 0), 2, 3);
        assertTrue(actions().attack(h));
        assertEquals(283, m.getHP(), EPS);
    }

    @Test
    void attackAsksWhenSeveralTargetsAreInRange() {
        Monster north = addMonster(new Dragon("North", 5, 1, 1, 0), 2, 3);
        Monster south = addMonster(new Dragon("South", 5, 1, 1, 0), 4, 3);

        assertTrue(actions("2").attack(h));

        assertEquals(500, north.getHP(), EPS);
        assertEquals(283, south.getHP(), EPS);
    }

    @Test
    void cancellingTargetSelectionDoesNotUseTheTurn() {
        addMonster(new Dragon("North", 5, 1, 1, 0), 2, 3);
        addMonster(new Dragon("South", 5, 1, 1, 0), 4, 3);
        assertFalse(actions("0").attack(h));
    }

    @Test
    void killedMonstersLeaveTheLaneList() {
        addMonster(monster("Weak"), 2, 3).setHP(1);
        assertTrue(actions().attack(h));
        assertTrue(laneMonsters.isEmpty());
    }

    @Test
    void castWithoutSpellsDoesNotUseTheTurn() {
        h.addItem(new Weapon("Sword", 1, 1, 1, 1));
        assertFalse(actions().castSpell(h));
    }

    @Test
    void castPromptsForSpellAndHitsTheTarget() {
        h.addItem(new Spell("Bolt", 1, 1, 100, 10, SpellType.LIGHTNING));
        Monster m = addMonster(new Dragon("Tank", 5, 1, 1, 0), 2, 3);

        assertTrue(actions("1").castSpell(h));

        assertEquals(394, m.getHP(), EPS);
        assertEquals(40, h.getMP(), EPS);
    }

    @Test
    void castWithNoTargetsDoesNotSpendMana() {
        h.addItem(new Spell("Bolt", 1, 1, 100, 10, SpellType.LIGHTNING));
        assertFalse(actions("1").castSpell(h));
        assertEquals(50, h.getMP(), EPS);
    }

    @Test
    void cleanupRemovesDeadAndNullEntries() {
        Monster alive = monster("Alive");
        Monster dead = monster("Dead");
        dead.setHP(0);
        laneMonsters.addAll(Arrays.asList(alive, dead, null));

        actions().cleanupDeadMonsters();

        assertEquals(Arrays.asList(alive), laneMonsters);
    }
}
