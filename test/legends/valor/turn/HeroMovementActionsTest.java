package legends.valor.turn;

import legends.characters.Hero;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorCellType;
import legends.valor.world.ValorDirection;
import legends.valor.world.ValorMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class HeroMovementActionsTest {

    private ValorBoard board;
    private ValorMovement movement;
    private Map<Hero, Integer> homeLane;
    private Hero h;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        movement = new ValorMovement(board);
        homeLane = new HashMap<Hero, Integer>();
        h = hero("Hero");
    }

    private HeroMovementActions actions(String... answers) {
        return new HeroMovementActions(board, movement,
                new HeroTurnUIHelper(scriptedInput(answers)), homeLane);
    }

    @Test
    void moveReportsSuccessAndFailure() {
        place(board, h, 7, 0);
        assertTrue(actions().move(h, ValorDirection.NORTH));
        board.getTile(5, 0).setType(ValorCellType.OBSTACLE);
        assertFalse(actions().move(h, ValorDirection.NORTH));
    }

    @Test
    void homeLaneBindsOnlyOnce() {
        place(board, h, 7, 3);
        actions().bindHomeLaneIfMissing(h);
        assertEquals(Integer.valueOf(1), homeLane.get(h));

        movement.teleportHeroTo(h, 7, 6);
        actions().bindHomeLaneIfMissing(h);
        assertEquals(Integer.valueOf(1), homeLane.get(h));
    }

    @Test
    void recallReturnsHeroToHomeNexus() {
        place(board, h, 3, 0);
        homeLane.put(h, 1);
        assertTrue(actions().recall(h));
        assertArrayEquals(new int[]{7, 3}, movement.findHero(h));
    }

    @Test
    void recallUsesTheSecondNexusCellWhenFirstIsTaken() {
        place(board, h, 3, 0);
        place(board, hero("Other"), 7, 3);
        homeLane.put(h, 1);
        assertTrue(actions().recall(h));
        assertArrayEquals(new int[]{7, 4}, movement.findHero(h));
    }

    @Test
    void recallFailsWhenNexusIsFull() {
        place(board, h, 3, 0);
        place(board, hero("One"), 7, 3);
        place(board, hero("Two"), 7, 4);
        homeLane.put(h, 1);
        assertFalse(actions().recall(h));
        assertArrayEquals(new int[]{3, 0}, movement.findHero(h));
    }

    @Test
    void recallFailsWithoutAHomeLane() {
        place(board, h, 3, 0);
        assertFalse(actions().recall(h));
    }

    @Test
    void recallRevertsTerrainBonus() {
        board.getTile(3, 0).setType(ValorCellType.BUSH);
        place(board, h, 3, 0);
        board.getTile(3, 0).onEnter(h);
        homeLane.put(h, 0);

        actions().recall(h);

        assertEquals(600, h.getDexterity(), 1e-6);
    }

    @ParameterizedTest
    @CsvSource({"W, 3, 0", "S, 5, 0", "D, 4, 1", "w, 3, 0"})
    void removesAdjacentObstacle(String direction, int row, int col) {
        place(board, h, 4, 0);
        board.getTile(row, col).setType(ValorCellType.OBSTACLE);

        assertTrue(actions(direction).removeObstacle(h, scriptedInput(direction)));

        assertEquals(ValorCellType.PLAIN, board.getTile(row, col).getType());
    }

    @Test
    void removesObstacleToTheWest() {
        place(board, h, 4, 1);
        board.getTile(4, 0).setType(ValorCellType.OBSTACLE);
        assertTrue(actions().removeObstacle(h, scriptedInput("A")));
        assertEquals(ValorCellType.PLAIN, board.getTile(4, 0).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "", "X"})
    void cancelledOrInvalidDirectionsRemoveNothing(String answer) {
        place(board, h, 4, 0);
        board.getTile(3, 0).setType(ValorCellType.OBSTACLE);
        assertFalse(actions().removeObstacle(h, scriptedInput(answer)));
        assertEquals(ValorCellType.OBSTACLE, board.getTile(3, 0).getType());
    }

    @Test
    void closedInputRemovesNothing() {
        place(board, h, 4, 0);
        assertFalse(actions().removeObstacle(h, scriptedInput()));
    }

    @Test
    void onlyObstaclesCanBeRemoved() {
        place(board, h, 4, 1);
        assertFalse(actions().removeObstacle(h, scriptedInput("W")));
        assertFalse(actions().removeObstacle(h, scriptedInput("D"))); // wall
        assertEquals(ValorCellType.INACCESSIBLE, board.getTile(4, 2).getType());
    }

    @Test
    void cannotRemoveOffTheBoard() {
        place(board, h, 7, 0);
        assertFalse(actions().removeObstacle(h, scriptedInput("S")));
    }

    @Test
    void teleportLandsNextToAHeroInAnotherLane() {
        Hero target = hero("Target");
        place(board, h, 5, 0);
        place(board, target, 4, 3);

        assertTrue(actions("1", "1").teleport(h));

        assertArrayEquals(new int[]{5, 3}, movement.findHero(h));
    }

    @Test
    void teleportNeverLandsAheadOfTheTargetHero() {
        Hero target = hero("Target");
        place(board, h, 5, 0);
        place(board, target, 4, 3);

        // Options are (5,3) and (4,4); a third option would be the illegal (3,3).
        assertFalse(actions("1", "3").teleport(h));
        assertArrayEquals(new int[]{5, 0}, movement.findHero(h));
    }

    @Test
    void teleportRequiresADifferentLane() {
        place(board, h, 5, 0);
        place(board, hero("Target"), 4, 1);
        assertFalse(actions("1", "1").teleport(h));
        assertArrayEquals(new int[]{5, 0}, movement.findHero(h));
    }

    @Test
    void teleportRequiresAnotherLivingHero() {
        Hero fainted = hero("Fainted");
        fainted.setHP(0);
        place(board, h, 5, 0);
        place(board, fainted, 4, 3);
        assertFalse(actions("1", "1").teleport(h));
    }

    @Test
    void cancellingTeleportLeavesHeroInPlace() {
        place(board, h, 5, 0);
        place(board, hero("Target"), 4, 3);
        assertFalse(actions("0").teleport(h));
        assertFalse(actions("1", "0").teleport(h));
        assertArrayEquals(new int[]{5, 0}, movement.findHero(h));
    }
}
