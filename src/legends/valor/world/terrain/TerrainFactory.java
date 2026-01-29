package legends.valor.world.terrain;

import legends.valor.world.ValorCellType;

/**
 * Class: TerrainFactory.java
 * Package: legends.valor.world.terrain
 * Purpose:
 * Creates Terrain instances for terrain-enabled board cells.
 */
public class TerrainFactory {

    private TerrainFactory() {
        // utility class
    }

    /**
     * Returns a Terrain instance for bonus terrain cell types.
     * Returns null for non-terrain cells.
     */
    public static Terrain create(ValorCellType type) {
        if (type == null) return null;

        switch (type) {
            case BUSH:
                return new BushTerrain();
            case CAVE:
                return new CaveTerrain();
            case KOULOU:
                return new KoulouTerrain();
            default:
                return null;
        }
    }
}
