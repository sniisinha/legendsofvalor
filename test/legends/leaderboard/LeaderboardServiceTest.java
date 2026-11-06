package legends.leaderboard;

import legends.characters.Hero;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardServiceTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 1, 1, 12, 0);

    @TempDir
    Path dir;

    private LeaderboardService service;

    @BeforeEach
    void setUp() {
        service = new LeaderboardService(dir.toString());
    }

    /** A win whose score is 1000 + kills * 200. */
    private static GameRecord win(int kills, LocalDateTime endedAt) {
        GameRecord r = new GameRecord(GameStats.GameMode.LEGENDS_OF_VALOR,
                GameStats.GameResult.HEROES_WIN, T0, endedAt, 0);
        r.heroes.add(new GameRecord.HeroRecord("H", 1, kills, 0, 0, 0, 0, 0));
        return r;
    }

    private static List<Integer> scores(List<LeaderboardEntry> entries) {
        List<Integer> out = new ArrayList<Integer>();
        for (LeaderboardEntry e : entries) out.add(e.score);
        return out;
    }

    @Test
    void emptyLeaderboardReturnsNothing() {
        assertTrue(service.topByScore(3).isEmpty());
        assertTrue(service.recent(3).isEmpty());
    }

    @Test
    void nonPositiveLimitsReturnNothing() {
        service.add(win(1, T0));
        assertTrue(service.topByScore(0).isEmpty());
        assertTrue(service.recent(-1).isEmpty());
    }

    @Test
    void nullRecordIsIgnored() {
        service.add(null);
        assertFalse(Files.exists(service.getFilePath()));
    }

    @Test
    void topByScoreListsHighestScoreFirst() {
        service.add(win(1, T0));
        service.add(win(3, T0.plusMinutes(1)));
        service.add(win(2, T0.plusMinutes(2)));

        assertEquals(Arrays.asList(1600, 1400, 1200), scores(service.topByScore(3)));
    }

    @Test
    void topByScoreKeepsOnlyTheBestN() {
        service.add(win(1, T0));
        service.add(win(3, T0));
        service.add(win(2, T0));

        assertEquals(Arrays.asList(1600, 1400), scores(service.topByScore(2)));
    }

    @Test
    void topByScoreBreaksTiesWithNewestFirst() {
        LocalDateTime later = T0.plusDays(1);
        service.add(win(1, T0));
        service.add(win(1, later));

        assertEquals(later, service.topByScore(2).get(0).endedAt);
    }

    @Test
    void topByScorePutsMissingDatesLastAmongTies() {
        service.add(win(1, null));
        service.add(win(1, T0));

        assertEquals(T0, service.topByScore(2).get(0).endedAt);
    }

    @Test
    void recentListsNewestFirstAndRespectsLimit() {
        service.add(win(1, T0));
        service.add(win(2, T0.plusDays(2)));
        service.add(win(3, T0.plusDays(1)));

        List<LeaderboardEntry> recent = service.recent(2);
        assertEquals(Arrays.asList(1400, 1600), scores(recent));
    }

    @Test
    void recentPutsMissingDatesLast() {
        service.add(win(1, null));
        service.add(win(2, T0));

        assertEquals(T0, service.recent(2).get(0).endedAt);
        assertNull(service.recent(2).get(1).endedAt);
    }

    @Test
    void entriesSurviveAcrossServiceInstances() {
        service.add(win(1, T0));
        assertEquals(1, new LeaderboardService(dir.toString()).recent(5).size());
    }

    @Test
    void createsMissingDirectories() {
        Path nested = dir.resolve("a").resolve("b");
        new LeaderboardService(nested.toString()).add(win(1, T0));
        assertTrue(Files.exists(nested.resolve("leaderboard.ser")));
    }

    @Test
    void scoreMatchesGameStats() {
        Hero h = hero("H");
        GameStats stats = new GameStats(GameStats.GameMode.LEGENDS_OF_VALOR, Collections.singletonList(h));
        stats.statsFor(h).addKill();
        stats.statsFor(h).addFaint();
        stats.statsFor(h).addDamageDealt(333);
        stats.statsFor(h).addDamageTaken(77);
        stats.addRound();
        stats.addRound();
        stats.markEnded(GameStats.GameResult.HEROES_WIN);

        GameRecord record = new GameRecord(stats.getMode(), stats.getResult(),
                stats.getStartedAt(), stats.getEndedAt(), stats.getRounds());
        record.heroes.add(new GameRecord.HeroRecord("H", 1, 1, 1, 333, 77, 0, 0));
        service.add(record);

        assertEquals(stats.computeScore(), service.recent(1).get(0).score);
    }

    @Test
    void entryCopiesRecordDetailsAndSummarizesTotals() {
        GameRecord r = new GameRecord(GameStats.GameMode.LEGENDS_OF_VALOR,
                GameStats.GameResult.MONSTERS_WIN, T0, T0.plusHours(1), 4);
        r.heroes.add(new GameRecord.HeroRecord("A", 1, 1, 2, 10, 20, 30, 40));
        r.heroes.add(new GameRecord.HeroRecord("B", 1, 1, 0, 5, 5, 5, 5));
        service.add(r);

        LeaderboardEntry e = service.recent(1).get(0);
        assertEquals(GameStats.GameMode.LEGENDS_OF_VALOR, e.mode);
        assertEquals(GameStats.GameResult.MONSTERS_WIN, e.result);
        assertEquals(T0.plusHours(1), e.endedAt);
        assertEquals("MONSTERS_WIN | rounds=4 | kills=2 | faints=2 | dealt=15 | taken=25 | gold=35 | xp=45",
                e.summaryLine);
    }

    @Test
    void corruptFileIsTreatedAsEmptyAndCanBeReplaced() throws IOException {
        Files.write(service.getFilePath(), "not serialized".getBytes("UTF-8"));
        assertTrue(service.recent(5).isEmpty());

        service.add(win(1, T0));
        assertEquals(1, service.recent(5).size());
    }

    @Test
    void fileHoldingTheWrongTypeIsTreatedAsEmpty() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(service.getFilePath()))) {
            out.writeObject("not a list");
        }
        assertTrue(service.recent(5).isEmpty());
    }
}
