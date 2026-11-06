package legends.valor.world.terrain;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.world.ValorCellType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class TerrainTest {

    private static final double DELTA = 1e-6;

    @Test
    void bushBoostsOnlyDexterityThenReverts() {
        Hero h = hero("A");
        Terrain t = new BushTerrain();
        t.onEnter(h);
        assertEquals(660, h.getDexterity(), DELTA);
        assertEquals(700, h.getStrength(), DELTA);
        t.onExit(h);
        assertEquals(600, h.getDexterity(), DELTA);
    }

    @Test
    void caveBoostsOnlyAgilityThenReverts() {
        Hero h = hero("A");
        h.setAgility(500);
        Terrain t = new CaveTerrain();
        t.onEnter(h);
        assertEquals(550, h.getAgility(), DELTA);
        assertEquals(600, h.getDexterity(), DELTA);
        t.onExit(h);
        assertEquals(500, h.getAgility(), DELTA);
    }

    @Test
    void koulouBoostsOnlyStrengthThenReverts() {
        Hero h = hero("A");
        Terrain t = new KoulouTerrain();
        t.onEnter(h);
        assertEquals(770, h.getStrength(), DELTA);
        assertEquals(600, h.getDexterity(), DELTA);
        t.onExit(h);
        assertEquals(700, h.getStrength(), DELTA);
    }

    @Test
    void monstersGetNoBonus() {
        Monster m = monster("M");
        for (Terrain t : new Terrain[]{new BushTerrain(), new CaveTerrain(), new KoulouTerrain()}) {
            t.onEnter(m);
            t.onExit(m);
        }
        assertEquals(400, m.getDamage(), DELTA);
        assertEquals(100, m.getDefense(), DELTA);
        assertEquals(100, m.getHP(), DELTA);
    }

    @Test
    void terrainsAreAccessibleWithTheirSymbols() {
        assertTrue(new BushTerrain().isAccessible());
        assertTrue(new CaveTerrain().isAccessible());
        assertTrue(new KoulouTerrain().isAccessible());
        assertEquals('B', new BushTerrain().getSymbol());
        assertEquals('C', new CaveTerrain().getSymbol());
        assertEquals('K', new KoulouTerrain().getSymbol());
    }

    @Test
    void factoryCreatesMatchingTerrain() {
        assertTrue(TerrainFactory.create(ValorCellType.BUSH) instanceof BushTerrain);
        assertTrue(TerrainFactory.create(ValorCellType.CAVE) instanceof CaveTerrain);
        assertTrue(TerrainFactory.create(ValorCellType.KOULOU) instanceof KoulouTerrain);
    }

    @ParameterizedTest
    @EnumSource(value = ValorCellType.class, names = {"NEXUS", "INACCESSIBLE", "OBSTACLE", "PLAIN"})
    void factoryReturnsNullForCellsWithoutTerrain(ValorCellType type) {
        assertNull(TerrainFactory.create(type));
    }

    @Test
    void factoryHandlesNull() {
        assertNull(TerrainFactory.create(null));
    }
}
