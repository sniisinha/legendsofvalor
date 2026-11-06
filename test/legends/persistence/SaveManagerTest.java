package legends.persistence;

import legends.stats.GameRecord;
import legends.stats.GameStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static legends.TestSupport.EPS;
import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {

    @TempDir
    Path dir;

    private SaveManager manager;

    @BeforeEach
    void setUp() {
        manager = new SaveManager(dir.toString());
    }

    private static GameRecord record() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 12, 0);
        GameRecord r = new GameRecord(GameStats.GameMode.LEGENDS_OF_VALOR,
                GameStats.GameResult.HEROES_WIN, start, start.plusMinutes(9), 7);
        r.heroes.add(new GameRecord.HeroRecord("Hero", 3, 4, 1, 250.5, 90.5, 600, 12));
        return r;
    }

    @Test
    void loadWithoutASaveReturnsNull() {
        assertNull(manager.loadLastGame());
    }

    @Test
    void saveAndLoadRoundTripKeepsAllFields() {
        GameRecord saved = record();
        assertTrue(manager.saveLastGame(saved));

        GameRecord loaded = manager.loadLastGame();

        assertNotSame(saved, loaded);
        assertEquals(saved.mode, loaded.mode);
        assertEquals(saved.result, loaded.result);
        assertEquals(saved.startedAt, loaded.startedAt);
        assertEquals(saved.endedAt, loaded.endedAt);
        assertEquals(7, loaded.roundsPlayed);
        assertEquals(1, loaded.heroes.size());

        GameRecord.HeroRecord h = loaded.heroes.get(0);
        assertEquals("Hero", h.heroName);
        assertEquals(3, h.level);
        assertEquals(4, h.monstersKilled);
        assertEquals(1, h.timesFainted);
        assertEquals(250.5, h.damageDealt, EPS);
        assertEquals(90.5, h.damageTaken, EPS);
        assertEquals(600, h.goldGained);
        assertEquals(12, h.xpGained);
    }

    @Test
    void savingAgainOverwritesThePreviousGame() {
        manager.saveLastGame(record());
        GameRecord newer = new GameRecord(GameStats.GameMode.MONSTERS_AND_HEROES,
                GameStats.GameResult.QUIT, null, null, 1);
        manager.saveLastGame(newer);

        assertEquals(GameStats.GameResult.QUIT, manager.loadLastGame().result);
    }

    @Test
    void savingNullFailsAndWritesNothing() {
        assertFalse(manager.saveLastGame(null));
        assertFalse(Files.exists(manager.getLastGameFilePath()));
    }

    @Test
    void createsMissingDirectories() {
        Path nested = dir.resolve("a").resolve("b");
        SaveManager m = new SaveManager(nested.toString());
        assertTrue(m.saveLastGame(record()));
        assertTrue(Files.exists(nested.resolve("last_game.ser")));
    }

    @Test
    void corruptSaveLoadsAsNull() throws IOException {
        Files.write(manager.getLastGameFilePath(), "garbage".getBytes("UTF-8"));
        assertNull(manager.loadLastGame());
    }

    @Test
    void saveHoldingTheWrongTypeLoadsAsNull() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(manager.getLastGameFilePath()))) {
            out.writeObject("not a game record");
        }
        assertNull(manager.loadLastGame());
    }
}
