/**
 * Utility class for generating randomized game maps.
 * 
 * This class creates a WorldMap of a given size and fills it
 * with different types of tiles:
 * 
 *     InaccessibleTile (20% chance)
 *     MarketTile (15% chance)
 *     CommonTile (65% chance)
 * 
 * The distribution provides a mix of safe paths, markets,
 * and obstacles, making exploration unpredictable and varied.
 */

package legends.world;

import java.util.Random;

public class MapGenerator {

    /**
     * Generates a new randomized map filled with tiles.
     *
     * @param size the height and width of the square map
     * @return a fully populated WorldMap instance
     */
    public static WorldMap generate(int size) {
        WorldMap map = new WorldMap(size);
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                // Random number 0–99 to determine tile type
                int r = rand.nextInt(100);

                if (r < 10) {
                    // 10% of tiles are inaccessible ("X")
                    map.setTile(i, j, new InaccessibleTile());
                }
                else if (r < 35) {
                    // Next 15% are markets ("M")
                    map.setTile(i, j, new MarketTile());
                }
                else {
                    // Remaining 65% are common walkable tiles (" ")
                    map.setTile(i, j, new CommonTile());
                }
            }
        }

        return map;
    }
}