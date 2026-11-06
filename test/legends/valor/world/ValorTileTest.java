package legends.valor.world;

import legends.characters.Hero;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ValorTileTest {

    @Test
    void cellTypeAccessibilityAndBonuses() {
        assertFalse(ValorCellType.INACCESSIBLE.isAccessible());
        assertFalse(ValorCellType.OBSTACLE.isAccessible());
        assertTrue(ValorCellType.NEXUS.isAccessible());
        assertTrue(ValorCellType.PLAIN.isAccessible());
        assertTrue(ValorCellType.BUSH.hasTerrainBonus());
        assertTrue(ValorCellType.CAVE.hasTerrainBonus());
        assertTrue(ValorCellType.KOULOU.hasTerrainBonus());
        assertFalse(ValorCellType.PLAIN.hasTerrainBonus());
        assertFalse(ValorCellType.NEXUS.hasTerrainBonus());
    }

    @ParameterizedTest
    @EnumSource(ValorCellType.class)
    void tileReflectsItsCellType(ValorCellType type) {
        ValorTile tile = new ValorTile(type);
        assertEquals(type, tile.getType());
        assertEquals(type.isAccessible(), tile.isAccessible());
        assertEquals(type.getSymbol(), tile.getSymbol());
    }

    @Test
    void directionsMoveOneCell() {
        assertEquals(-1, ValorDirection.NORTH.deltaRow());
        assertEquals(1, ValorDirection.SOUTH.deltaRow());
        assertEquals(-1, ValorDirection.WEST.deltaCol());
        assertEquals(1, ValorDirection.EAST.deltaCol());
        assertEquals(0, ValorDirection.NORTH.deltaCol());
        assertEquals(0, ValorDirection.EAST.deltaRow());
    }

    @Test
    void heroAndMonsterMayShareATile() {
        ValorTile tile = new ValorTile(ValorCellType.PLAIN);
        tile.placeHero(hero("A"));
        tile.placeMonster(monster("M"));
        assertTrue(tile.hasHero());
        assertTrue(tile.hasMonster());
    }

    @Test
    void secondHeroOrMonsterIsRejected() {
        ValorTile tile = new ValorTile(ValorCellType.PLAIN);
        tile.placeHero(hero("A"));
        tile.placeMonster(monster("M"));
        assertThrows(IllegalStateException.class, () -> tile.placeHero(hero("B")));
        assertThrows(IllegalStateException.class, () -> tile.placeMonster(monster("N")));
    }

    @Test
    void placingNullIsIgnored() {
        ValorTile tile = new ValorTile(ValorCellType.PLAIN);
        tile.placeHero(null);
        tile.placeMonster(null);
        assertFalse(tile.hasHero());
        assertFalse(tile.hasMonster());
    }

    @Test
    void removingClearsOccupants() {
        ValorTile tile = new ValorTile(ValorCellType.PLAIN);
        tile.placeHero(hero("A"));
        tile.placeMonster(monster("M"));
        tile.removeHero();
        tile.removeMonster();
        assertNull(tile.getHero());
        assertNull(tile.getMonster());
    }

    @Test
    void emptinessDependsOnAccessAndOccupancy() {
        ValorTile tile = new ValorTile(ValorCellType.PLAIN);
        assertTrue(tile.isEmptyForHero());
        tile.placeHero(hero("A"));
        assertFalse(tile.isEmptyForHero());
        assertTrue(tile.isEmptyForMonster());
        assertFalse(new ValorTile(ValorCellType.OBSTACLE).isEmptyForMonster());
        assertFalse(new ValorTile(ValorCellType.INACCESSIBLE).isEmptyForHero());
    }

    @Test
    void changingTypeUpdatesTerrainBehavior() {
        Hero h = hero("A");
        ValorTile tile = new ValorTile(ValorCellType.OBSTACLE);
        tile.setType(ValorCellType.BUSH);
        assertTrue(tile.isAccessible());

        tile.onEnter(h);
        assertEquals(660, h.getDexterity(), 1e-6);

        tile.setType(ValorCellType.PLAIN);
        tile.onExit(h); // plain terrain has no bonus to revert
        assertEquals(660, h.getDexterity(), 1e-6);
    }

    @Test
    void settingNullTypeIsIgnored() {
        ValorTile tile = new ValorTile(ValorCellType.BUSH);
        tile.setType(null);
        assertEquals(ValorCellType.BUSH, tile.getType());
    }

    @Test
    void terrainHooksAcceptNullEntities() {
        ValorTile tile = new ValorTile(ValorCellType.BUSH);
        assertDoesNotThrow(() -> tile.onEnter(null));
        assertDoesNotThrow(() -> tile.onExit(null));
    }
}
