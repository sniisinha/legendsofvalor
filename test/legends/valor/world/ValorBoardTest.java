package legends.valor.world;

import legends.characters.Hero;
import legends.characters.Monster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ValorBoardTest {

    private final ValorBoard board = new ValorBoard();

    @Test
    void wallColumnsAreInaccessibleOnEveryRow() {
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            assertEquals(ValorCellType.INACCESSIBLE, board.getTile(r, 2).getType());
            assertEquals(ValorCellType.INACCESSIBLE, board.getTile(r, 5).getType());
        }
    }

    @Test
    void firstAndLastRowsAreNexusOutsideWalls() {
        for (int c = 0; c < ValorBoard.COLS; c++) {
            if (c == 2 || c == 5) continue;
            assertEquals(ValorCellType.NEXUS, board.getTile(0, c).getType());
            assertEquals(ValorCellType.NEXUS, board.getTile(7, c).getType());
        }
    }

    @Test
    void laneInteriorHoldsOnlyLaneTerrain() {
        for (int attempt = 0; attempt < 20; attempt++) {
            ValorBoard b = new ValorBoard();
            for (int r = 1; r < 7; r++) {
                for (int c = 0; c < ValorBoard.COLS; c++) {
                    if (c == 2 || c == 5) continue;
                    ValorCellType t = b.getTile(r, c).getType();
                    assertNotEquals(ValorCellType.NEXUS, t);
                    assertNotEquals(ValorCellType.INACCESSIBLE, t);
                }
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"-1, 0", "0, -1", "8, 0", "0, 8"})
    void outOfBoundsCoordinatesAreRejected(int row, int col) {
        assertFalse(board.inBounds(row, col));
        assertNull(board.getTile(row, col));
        assertFalse(board.canHeroEnter(row, col));
        assertFalse(board.canMonsterEnter(row, col));
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "1, 0", "3, 1", "4, 1", "6, 2", "7, 2", "2, -1", "5, -1", "-1, -1", "8, -1"})
    void columnsMapToLanes(int col, int lane) {
        assertEquals(lane, board.getLane(col));
    }

    @Test
    void nexusSidesAreIdentifiedByRow() {
        assertTrue(board.isMonstersNexus(0, 0));
        assertFalse(board.isMonstersNexus(7, 0));
        assertTrue(board.isHeroesNexus(7, 0));
        assertFalse(board.isHeroesNexus(0, 0));
        assertFalse(board.isNexus(3, 0));
    }

    @ParameterizedTest
    @CsvSource({"0, 0, 1", "1, 3, 4", "2, 6, 7"})
    void laneSpawnCells(int lane, int heroCol, int monsterCol) {
        assertArrayEquals(new int[]{heroCol, monsterCol}, board.getNexusColumnsForLane(lane));
        assertArrayEquals(new int[]{7, heroCol}, board.getHeroSpawnCell(lane));
        assertArrayEquals(new int[]{0, monsterCol}, board.getMonsterSpawnCell(lane));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void invalidLanesHaveSafeDefaults(int lane) {
        assertEquals(0, board.getNexusColumnsForLane(lane).length);
        assertArrayEquals(new int[]{7, 0}, board.getHeroSpawnCell(lane));
        assertArrayEquals(new int[]{0, 0}, board.getMonsterSpawnCell(lane));
    }

    @Test
    void canEnterChecksAccessibilityAndOccupancy() {
        ValorBoard b = plainBoard();
        assertTrue(b.canHeroEnter(3, 0));
        assertFalse(b.canHeroEnter(3, 2));

        place(b, hero("A"), 3, 0);
        assertFalse(b.canHeroEnter(3, 0));
        assertTrue(b.canMonsterEnter(3, 0));

        place(b, monster("M"), 3, 0);
        assertFalse(b.canMonsterEnter(3, 0));
    }

    @Test
    void movingAPieceUpdatesBothTiles() {
        ValorBoard b = plainBoard();
        Hero h = hero("A");
        Monster m = monster("M");
        place(b, h, 5, 0);
        place(b, m, 2, 0);

        b.moveHero(h, 5, 0, 4, 0);
        b.moveMonster(m, 2, 0, 3, 0);

        assertFalse(b.getTile(5, 0).hasHero());
        assertSame(h, b.getTile(4, 0).getHero());
        assertFalse(b.getTile(2, 0).hasMonster());
        assertSame(m, b.getTile(3, 0).getMonster());
    }

    @Test
    void invalidMovesAreIgnored() {
        ValorBoard b = plainBoard();
        Hero h = hero("A");
        Monster m = monster("M");
        place(b, h, 5, 0);
        place(b, m, 2, 0);

        b.moveHero(null, 5, 0, 4, 0);
        b.moveHero(h, 5, 0, 5, 9);
        b.moveMonster(null, 2, 0, 3, 0);
        b.moveMonster(m, 2, 0, -1, 0);

        assertSame(h, b.getTile(5, 0).getHero());
        assertSame(m, b.getTile(2, 0).getMonster());
    }

    @Test
    void heroesWinOnlyOnTheTopRow() {
        ValorBoard b = plainBoard();
        assertFalse(b.heroesReachedEnemyNexus());
        place(b, hero("Low"), 1, 0);
        assertFalse(b.heroesReachedEnemyNexus());
        place(b, hero("High"), 0, 3);
        assertTrue(b.heroesReachedEnemyNexus());
    }

    @Test
    void monstersWinOnlyOnTheBottomRow() {
        ValorBoard b = plainBoard();
        assertFalse(b.monstersReachedHeroesNexus());
        place(b, monster("High"), 6, 0);
        assertFalse(b.monstersReachedHeroesNexus());
        place(b, monster("Low"), 7, 3);
        assertTrue(b.monstersReachedHeroesNexus());
    }

    @Test
    void printingAnOccupiedBoardDoesNotThrow() {
        ValorBoard b = plainBoard();
        place(b, hero("A"), 3, 0);
        place(b, monster("M"), 3, 0);
        place(b, hero("B"), 4, 0);
        place(b, monster("N"), 5, 0);
        assertDoesNotThrow(b::print);
    }
}
