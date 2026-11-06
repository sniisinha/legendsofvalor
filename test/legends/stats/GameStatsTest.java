package legends.stats;

import legends.characters.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static legends.TestSupport.EPS;
import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class GameStatsTest {

    private Hero first;
    private Hero second;
    private GameStats stats;

    @BeforeEach
    void setUp() {
        first = hero("First");
        second = hero("Second");
        stats = new GameStats(GameStats.GameMode.LEGENDS_OF_VALOR, Arrays.asList(first, second));
    }

    @Test
    void tracksAStatsEntryPerHeroInOrder() {
        assertEquals(GameStats.GameMode.LEGENDS_OF_VALOR, stats.getMode());
        assertEquals(2, stats.getHeroStats().size());
        assertSame(first, stats.getHeroStats().iterator().next().getHero());
        assertSame(second, stats.statsFor(second).getHero());
    }

    @Test
    void nullHeroListAndUnknownHeroAreSafe() {
        GameStats none = new GameStats(GameStats.GameMode.MONSTERS_AND_HEROES, null);
        assertTrue(none.getHeroStats().isEmpty());
        assertNull(none.statsFor(first));
    }

    @Test
    void totalsAddUpAllHeroes() {
        HeroStats a = stats.statsFor(first);
        HeroStats b = stats.statsFor(second);
        a.addKill();
        b.addKill();
        b.addFaint();
        a.addDamageDealt(10.5);
        b.addDamageDealt(1);
        a.addDamageTaken(3);
        b.addDamageTaken(4);
        a.addGoldGained(5);
        b.addGoldGained(6);
        a.addXpGained(7);
        b.addXpGained(8);

        assertEquals(2, stats.totalKills());
        assertEquals(1, stats.totalFaints());
        assertEquals(11.5, stats.totalDamageDealt(), EPS);
        assertEquals(7, stats.totalDamageTaken(), EPS);
        assertEquals(11, stats.totalGoldGained());
        assertEquals(15, stats.totalXpGained());
    }

    @Test
    void roundsAreCounted() {
        stats.addRound();
        stats.addRound();
        assertEquals(2, stats.getRounds());
    }

    @Test
    void durationIsZeroUntilGameEnds() {
        assertEquals(Duration.ZERO, stats.getDuration());
        assertNull(stats.getResult());
        assertNull(stats.getEndedAt());
    }

    @Test
    void markEndedRecordsResultAndTime() {
        stats.markEnded(GameStats.GameResult.QUIT);
        assertEquals(GameStats.GameResult.QUIT, stats.getResult());
        assertNotNull(stats.getEndedAt());
        assertFalse(stats.getDuration().isNegative());
    }

    @Test
    void scoreCombinesEveryFactor() {
        HeroStats a = stats.statsFor(first);
        a.addKill();
        a.addKill();
        a.addFaint();
        a.addDamageDealt(1000);
        a.addDamageTaken(500);
        stats.addRound();
        stats.addRound();
        stats.addRound();
        stats.markEnded(GameStats.GameResult.HEROES_WIN);

        // 1000 + 2*200 + 500 - 150 - 100 - 30
        assertEquals(1620, stats.computeScore());
    }

    @Test
    void lossPenaltyAppliesAgainstOtherPoints() {
        stats.statsFor(first).addDamageDealt(1000);
        stats.markEnded(GameStats.GameResult.MONSTERS_WIN);
        assertEquals(300, stats.computeScore());
    }

    @Test
    void quittingHasNoBonusOrPenalty() {
        stats.statsFor(first).addDamageDealt(1000);
        stats.markEnded(GameStats.GameResult.QUIT);
        assertEquals(500, stats.computeScore());
    }

    @Test
    void scoreNeverGoesNegative() {
        stats.statsFor(first).addFaint();
        stats.markEnded(GameStats.GameResult.MONSTERS_WIN);
        assertEquals(0, stats.computeScore());
    }
}
