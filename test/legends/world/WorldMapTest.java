package legends.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    private WorldMap map;

    @BeforeEach
    void setUp() {
        map = new WorldMap(2);
        map.setTile(0, 0, new CommonTile());
        map.setTile(0, 1, new InaccessibleTile());
        map.setTile(1, 0, new MarketTile());
        map.setTile(1, 1, new CommonTile());
    }

    @Test
    void tileTypesDefineAccessAndSymbol() {
        assertTrue(new CommonTile().isAccessible());
        assertTrue(new MarketTile().isAccessible());
        assertFalse(new InaccessibleTile().isAccessible());
        assertEquals(" ", new CommonTile().getSymbol());
        assertEquals("M", new MarketTile().getSymbol());
        assertEquals("X", new InaccessibleTile().getSymbol());
    }

    @Test
    void returnsTheTilePlacedAtAPosition() {
        assertTrue(map.getTile(new Position(1, 0)) instanceof MarketTile);
    }

    @ParameterizedTest
    @CsvSource({"-1, 0", "0, -1", "2, 0", "0, 2"})
    void outOfBoundsPositionsCannotBeEntered(int row, int col) {
        Position p = new Position(row, col);
        assertFalse(map.inBounds(p));
        assertFalse(map.canMove(p));
    }

    @Test
    void canMoveOnlyOntoAccessibleTiles() {
        assertTrue(map.canMove(new Position(0, 0)));
        assertTrue(map.canMove(new Position(1, 0)));
        assertFalse(map.canMove(new Position(0, 1)));
    }

    @Test
    void printingDoesNotThrow() {
        assertDoesNotThrow(() -> map.print(new Position(0, 0)));
    }

    @Test
    void generatedMapIsFullyPopulated() {
        WorldMap generated = MapGenerator.generate(8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                assertNotNull(generated.getTile(new Position(r, c)));
            }
        }
        assertFalse(generated.inBounds(new Position(8, 0)));
    }

    @Test
    void generatedTileMixMatchesTheDocumentedOdds() {
        int size = 300;
        int blocked = 0;
        int markets = 0;
        WorldMap generated = MapGenerator.generate(size);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Tile t = generated.getTile(new Position(r, c));
                if (t instanceof InaccessibleTile) blocked++;
                if (t instanceof MarketTile) markets++;
            }
        }
        double total = size * size;
        assertEquals(0.20, blocked / total, 0.015);
        assertEquals(0.15, markets / total, 0.015);
    }
}
