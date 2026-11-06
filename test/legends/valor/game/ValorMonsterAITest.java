package legends.valor.game;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorCellType;
import legends.valor.world.ValorMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ValorMonsterAITest {

    private ValorBoard board;
    private ValorMovement movement;
    private ValorMonsterAI ai;
    private Monster m;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        movement = new ValorMovement(board);
        ai = new ValorMonsterAI(board, movement);
        m = monster("M");
    }

    private void block(int row, int col) {
        board.getTile(row, col).setType(ValorCellType.OBSTACLE);
    }

    @Test
    void advancesTowardTheHeroesNexus() {
        place(board, m, 2, 3);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{3, 3}, movement.findMonster(m));
    }

    @RepeatedTest(10)
    void sidestepsEastWhenBlockedInTheLeftColumn() {
        place(board, m, 2, 0);
        block(3, 0);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{2, 1}, movement.findMonster(m));
    }

    @RepeatedTest(10)
    void sidestepsWestWithoutEnteringTheWall() {
        place(board, m, 2, 1);
        block(3, 1);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{2, 0}, movement.findMonster(m));
    }

    @RepeatedTest(10)
    void fallsBackNorthWhenForwardAndSidesAreBlocked() {
        place(board, m, 2, 0);
        block(3, 0);
        block(2, 1);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{1, 0}, movement.findMonster(m));
    }

    @Test
    void staysPutWhenBoxedIn() {
        place(board, m, 2, 0);
        block(3, 0);
        block(2, 1);
        block(1, 0);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{2, 0}, movement.findMonster(m));
    }

    @RepeatedTest(10)
    void sidestepsWhenAHeroBlocksTheLane() {
        Hero h = hero("Blocker");
        place(board, m, 2, 3);
        place(board, h, 2, 4);
        block(3, 3);
        ai.advanceMonster(m);
        assertArrayEquals(new int[]{2, 4}, movement.findMonster(m));
    }

    @Test
    void nullAndOffBoardMonstersAreIgnored() {
        assertDoesNotThrow(() -> ai.advanceMonster(null));
        assertDoesNotThrow(() -> ai.advanceMonster(monster("Offboard")));
        assertDoesNotThrow(() -> new ValorMonsterAI(null, null).advanceMonster(m));
    }
}
