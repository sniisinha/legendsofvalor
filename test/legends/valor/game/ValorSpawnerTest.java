package legends.valor.game;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.data.DataLoader;
import legends.valor.world.ValorBoard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class ValorSpawnerTest {

    private ValorBoard board;
    private ValorSpawner spawner;
    private Party party;
    private Hero a;
    private Hero b;
    private Hero c;
    private List<Monster> savedMonsters;

    @BeforeEach
    void setUp() {
        savedMonsters = DataLoader.globalMonsters;
        board = new ValorBoard();
        spawner = new ValorSpawner(board);
        a = hero("A");
        b = hero("B");
        c = hero("C");
        party = new Party();
        party.addHero(a);
        party.addHero(b);
        party.addHero(c);
    }

    @AfterEach
    void restoreGlobals() {
        DataLoader.globalMonsters = savedMonsters;
    }

    @Test
    void defaultPlacementPutsOneHeroInEachLane() {
        spawner.placeHeroesOnBoard(party);
        assertSame(a, board.getTile(7, 0).getHero());
        assertSame(b, board.getTile(7, 3).getHero());
        assertSame(c, board.getTile(7, 6).getHero());
    }

    @Test
    void explicitLaneAssignmentIsRespected() {
        Map<Hero, Integer> lanes = new HashMap<Hero, Integer>();
        lanes.put(a, 2);
        lanes.put(b, 0);
        lanes.put(c, 1);

        spawner.placeHeroesOnBoard(party, lanes);

        assertSame(a, board.getTile(7, 6).getHero());
        assertSame(b, board.getTile(7, 0).getHero());
        assertSame(c, board.getTile(7, 3).getHero());
    }

    @Test
    void emptyAssignmentFallsBackToDefaultPlacement() {
        spawner.placeHeroesOnBoard(party, Collections.<Hero, Integer>emptyMap());
        assertSame(a, board.getTile(7, 0).getHero());
    }

    @Test
    void sharedLaneUsesTheSecondNexusCell() {
        Map<Hero, Integer> lanes = new HashMap<Hero, Integer>();
        lanes.put(a, 1);
        lanes.put(b, 1);
        lanes.put(c, 2);

        spawner.placeHeroesOnBoard(party, lanes);

        assertSame(a, board.getTile(7, 3).getHero());
        assertSame(b, board.getTile(7, 4).getHero());
    }

    @Test
    void fullLaneSkipsTheExtraHeroWithoutThrowing() {
        Map<Hero, Integer> lanes = new HashMap<Hero, Integer>();
        lanes.put(a, 0);
        lanes.put(b, 0);
        lanes.put(c, 0);

        assertDoesNotThrow(() -> spawner.placeHeroesOnBoard(party, lanes));
        assertSame(a, board.getTile(7, 0).getHero());
        assertSame(b, board.getTile(7, 1).getHero());
    }

    @Test
    void invalidLaneFallsBackToMidLane() {
        Map<Hero, Integer> lanes = new HashMap<Hero, Integer>();
        lanes.put(a, 9);

        spawner.placeHeroesOnBoard(party, lanes);

        assertSame(a, board.getTile(7, 3).getHero());
    }

    @Test
    void nullPartyIsIgnored() {
        assertDoesNotThrow(() -> spawner.placeHeroesOnBoard(null));
        assertDoesNotThrow(() -> spawner.placeHeroesOnBoard(null, null));
    }

    @Test
    void spawnsOneMonsterPerLaneOnTheTopRow() {
        List<Monster> pool = new ArrayList<Monster>();
        pool.add(new Dragon("D", 1, 100, 100, 0));
        DataLoader.globalMonsters = pool;

        List<Monster> spawned = spawner.spawnLaneMonsters(party);

        assertEquals(3, spawned.size());
        assertSame(spawned.get(0), board.getTile(0, 1).getMonster());
        assertSame(spawned.get(1), board.getTile(0, 4).getMonster());
        assertSame(spawned.get(2), board.getTile(0, 7).getMonster());
    }

    @Test
    void occupiedSpawnCellsAreSkipped() {
        List<Monster> pool = new ArrayList<Monster>();
        pool.add(new Dragon("D", 1, 100, 100, 0));
        DataLoader.globalMonsters = pool;
        board.getTile(0, 1).placeMonster(new Dragon("Existing", 1, 1, 1, 0));

        assertEquals(2, spawner.spawnLaneMonsters(party).size());
    }
}
