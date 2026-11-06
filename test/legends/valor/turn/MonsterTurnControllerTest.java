package legends.valor.turn;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.combat.ValorCombat;
import legends.valor.game.ValorMonsterAI;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class MonsterTurnControllerTest {

    private ValorBoard board;
    private ValorMovement movement;
    private MonsterTurnController controller;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        movement = new ValorMovement(board);
        controller = new MonsterTurnController(
                new ValorCombat(board, null), new ValorMonsterAI(board, movement));
    }

    @Test
    void monsterAttacksAHeroInRangeInsteadOfMoving() {
        Hero h = hero("H");
        Monster m = monster("M");
        place(board, h, 3, 0);
        place(board, m, 2, 0);

        controller.monstersPhase(new ArrayList<Monster>(Arrays.asList(m)));

        assertEquals(0, h.getHP(), EPS); // 400 * 0.3 = 120 > 100
        assertArrayEquals(new int[]{2, 0}, movement.findMonster(m));
    }

    @Test
    void monsterAdvancesWhenNoHeroIsInRange() {
        Monster m = monster("M");
        place(board, m, 2, 3);

        controller.monstersPhase(new ArrayList<Monster>(Arrays.asList(m)));

        assertArrayEquals(new int[]{3, 3}, movement.findMonster(m));
    }

    @Test
    void deadAndNullMonstersAreSkipped() {
        Monster dead = monster("Dead");
        dead.setHP(0);
        place(board, dead, 2, 3);
        List<Monster> monsters = new ArrayList<Monster>(Arrays.asList(dead, null));

        assertDoesNotThrow(() -> controller.monstersPhase(monsters));
        assertArrayEquals(new int[]{2, 3}, movement.findMonster(dead));
    }

    @Test
    void everyLivingMonsterActs() {
        Monster first = new Dragon("First", 1, 100, 100, 0);
        Monster second = new Dragon("Second", 1, 100, 100, 0);
        place(board, first, 1, 0);
        place(board, second, 1, 3);

        controller.monstersPhase(new ArrayList<Monster>(Arrays.asList(first, second)));

        assertEquals(2, movement.findMonster(first)[0]);
        assertEquals(2, movement.findMonster(second)[0]);
    }

    @Test
    void missingCollaboratorsOrMonstersAreIgnored() {
        assertDoesNotThrow(() -> controller.monstersPhase(null));
        assertDoesNotThrow(() -> controller.monstersPhase(new ArrayList<Monster>()));
        assertDoesNotThrow(() -> new MonsterTurnController(null, null)
                .monstersPhase(new ArrayList<Monster>(Arrays.asList(monster("M")))));
    }
}
