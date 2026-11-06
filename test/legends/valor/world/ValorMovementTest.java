package legends.valor.world;

import legends.characters.Hero;
import legends.characters.Monster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ValorMovementTest {

    private ValorBoard board;
    private ValorMovement movement;
    private Hero h;
    private Monster m;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        movement = new ValorMovement(board);
        h = hero("Hero");
        m = monster("Monster");
    }

    @Test
    void constructorRejectsNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new ValorMovement(null));
    }

    @Test
    void findLocatesPiecesOnTheBoard() {
        assertNull(movement.findHero(h));
        assertNull(movement.findMonster(m));
        assertNull(movement.findHero(null));
        assertNull(movement.findMonster(null));

        place(board, h, 4, 3);
        place(board, m, 2, 6);
        assertArrayEquals(new int[]{4, 3}, movement.findHero(h));
        assertArrayEquals(new int[]{2, 6}, movement.findMonster(m));
    }

    @Test
    void heroMovesInEachDirection() {
        place(board, h, 4, 3);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertArrayEquals(new int[]{3, 3}, movement.findHero(h));
        assertTrue(movement.moveHero(h, ValorDirection.EAST));
        assertArrayEquals(new int[]{3, 4}, movement.findHero(h));
        assertTrue(movement.moveHero(h, ValorDirection.SOUTH));
        assertArrayEquals(new int[]{4, 4}, movement.findHero(h));
        assertTrue(movement.moveHero(h, ValorDirection.WEST));
        assertArrayEquals(new int[]{4, 3}, movement.findHero(h));
    }

    @Test
    void invalidHeroMoveArgumentsFail() {
        place(board, h, 4, 3);
        assertFalse(movement.moveHero(null, ValorDirection.NORTH));
        assertFalse(movement.moveHero(h, null));
        assertFalse(movement.moveHero(hero("Offboard"), ValorDirection.NORTH));
    }

    @Test
    void invalidMonsterMoveArgumentsFail() {
        place(board, m, 4, 3);
        assertFalse(movement.moveMonster(null, ValorDirection.SOUTH));
        assertFalse(movement.moveMonster(m, null));
        assertFalse(movement.moveMonster(monster("Offboard"), ValorDirection.SOUTH));
    }

    @Test
    void heroCannotLeaveTheBoard() {
        place(board, h, 7, 0);
        assertFalse(movement.moveHero(h, ValorDirection.SOUTH));
        assertFalse(movement.moveHero(h, ValorDirection.WEST));
        assertArrayEquals(new int[]{7, 0}, movement.findHero(h));
    }

    @Test
    void wallsBlockMovementBetweenLanes() {
        place(board, h, 4, 1);
        place(board, m, 3, 4);
        assertFalse(movement.moveHero(h, ValorDirection.EAST));
        assertFalse(movement.moveMonster(m, ValorDirection.EAST));
    }

    @Test
    void obstaclesBlockMovement() {
        board.getTile(3, 0).setType(ValorCellType.OBSTACLE);
        board.getTile(5, 3).setType(ValorCellType.OBSTACLE);
        place(board, h, 4, 0);
        place(board, m, 4, 3);
        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
        assertFalse(movement.moveMonster(m, ValorDirection.SOUTH));
    }

    @Test
    void tilesHoldOnlyOnePieceOfEachKind() {
        place(board, h, 4, 0);
        place(board, hero("Other"), 3, 0);
        place(board, m, 2, 3);
        place(board, monster("Other"), 3, 3);
        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
        assertFalse(movement.moveMonster(m, ValorDirection.SOUTH));
    }

    @Test
    void heroMayStepOntoAMonstersTileButNotPastIt() {
        place(board, h, 4, 0);
        place(board, m, 3, 0);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertArrayEquals(new int[]{3, 0}, movement.findHero(h));
        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
    }

    @Test
    void heroCannotPassAMonsterInTheNeighboringLaneColumn() {
        place(board, h, 5, 0);
        place(board, m, 4, 1);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
    }

    @Test
    void heroBesideAMonsterCannotWalkPastIt() {
        place(board, h, 5, 0);
        place(board, m, 5, 1);
        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
    }

    @Test
    void heroBesideAMonsterMayRetreatOrSidestep() {
        place(board, h, 5, 0);
        place(board, m, 4, 1);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertTrue(movement.moveHero(h, ValorDirection.SOUTH));
        assertTrue(movement.moveHero(h, ValorDirection.EAST));
    }

    @Test
    void monstersInOtherLanesAndBehindDoNotBlockHeroes() {
        place(board, h, 5, 0);
        place(board, m, 1, 3);
        place(board, monster("Behind"), 6, 1);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
    }

    @Test
    void deadMonstersDoNotBlockHeroes() {
        place(board, h, 5, 0);
        place(board, m, 4, 0);
        m.setHP(0);
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
    }

    @Test
    void monsterMayStepOntoAHerosTileButNotPastIt() {
        place(board, m, 2, 0);
        place(board, h, 3, 0);
        assertTrue(movement.moveMonster(m, ValorDirection.SOUTH));
        assertFalse(movement.moveMonster(m, ValorDirection.SOUTH));
    }

    @Test
    void monsterBesideAHeroCannotWalkPastIt() {
        place(board, m, 3, 0);
        place(board, h, 3, 1);
        assertFalse(movement.moveMonster(m, ValorDirection.SOUTH));
    }

    @Test
    void deadHeroesDoNotBlockMonsters() {
        place(board, m, 2, 0);
        place(board, h, 3, 0);
        h.setHP(0);
        assertTrue(movement.moveMonster(m, ValorDirection.SOUTH));
        assertTrue(movement.moveMonster(m, ValorDirection.SOUTH));
    }

    @Test
    void movingTriggersTerrainExitAndEnter() {
        board.getTile(6, 0).setType(ValorCellType.BUSH);
        place(board, h, 7, 0);

        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertEquals(660, h.getDexterity(), 1e-6);

        assertTrue(movement.moveHero(h, ValorDirection.NORTH));
        assertEquals(600, h.getDexterity(), 1e-6);
    }

    @Test
    void blockedMoveLeavesTerrainBonusInPlace() {
        board.getTile(6, 0).setType(ValorCellType.BUSH);
        board.getTile(5, 0).setType(ValorCellType.OBSTACLE);
        place(board, h, 7, 0);
        movement.moveHero(h, ValorDirection.NORTH);

        assertFalse(movement.moveHero(h, ValorDirection.NORTH));
        assertEquals(660, h.getDexterity(), 1e-6);
    }

    @Test
    void teleportChecksBasicLegality() {
        place(board, h, 5, 0);
        board.getTile(3, 3).setType(ValorCellType.OBSTACLE);
        place(board, hero("Other"), 4, 3);

        assertFalse(movement.canTeleportHeroTo(null, 5, 3));
        assertFalse(movement.canTeleportHeroTo(hero("Offboard"), 5, 3));
        assertFalse(movement.canTeleportHeroTo(h, 5, 9));
        assertFalse(movement.canTeleportHeroTo(h, 5, 2));
        assertFalse(movement.canTeleportHeroTo(h, 3, 3));
        assertFalse(movement.canTeleportHeroTo(h, 4, 3));
        assertTrue(movement.canTeleportHeroTo(h, 5, 3));
    }

    @Test
    void crossLaneTeleportCannotLandBehindTheFrontMonster() {
        place(board, h, 6, 0);
        place(board, m, 4, 3);
        assertFalse(movement.canTeleportHeroTo(h, 3, 3));
        assertTrue(movement.canTeleportHeroTo(h, 4, 4));
        assertTrue(movement.canTeleportHeroTo(h, 5, 3));
    }

    @Test
    void sameLaneTeleportCannotBypassAMonster() {
        place(board, h, 6, 0);
        place(board, m, 4, 1);
        assertFalse(movement.canTeleportHeroTo(h, 3, 0));
        assertTrue(movement.canTeleportHeroTo(h, 4, 0));
    }

    @Test
    void teleportMovesHeroAndKeepsTerrainConsistent() {
        board.getTile(6, 0).setType(ValorCellType.BUSH);
        board.getTile(5, 3).setType(ValorCellType.KOULOU);
        place(board, h, 7, 0);
        movement.moveHero(h, ValorDirection.NORTH);

        movement.teleportHeroTo(h, 5, 3);

        assertArrayEquals(new int[]{5, 3}, movement.findHero(h));
        assertEquals(600, h.getDexterity(), 1e-6);
        assertEquals(770, h.getStrength(), 1e-6);
    }

    @Test
    void teleportIntoAnIllegalCellDoesNothing() {
        place(board, h, 5, 0);
        movement.teleportHeroTo(h, 5, 2);
        movement.teleportHeroTo(h, 9, 9);
        movement.teleportHeroTo(null, 5, 3);
        assertArrayEquals(new int[]{5, 0}, movement.findHero(h));
    }
}
