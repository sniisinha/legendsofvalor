package legends.stats;

import legends.characters.Hero;
import org.junit.jupiter.api.Test;

import static legends.TestSupport.EPS;
import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class HeroStatsTest {

    @Test
    void countersStartAtZero() {
        HeroStats s = new HeroStats(hero("H"));
        assertEquals(0, s.getMonstersKilled());
        assertEquals(0, s.getTimesFainted());
        assertEquals(0, s.getDamageDealt(), EPS);
        assertEquals(0, s.getDamageTaken(), EPS);
        assertEquals(0, s.getGoldGained());
        assertEquals(0, s.getXpGained());
    }

    @Test
    void valuesAccumulate() {
        HeroStats s = new HeroStats(hero("H"));
        s.addKill();
        s.addKill();
        s.addFaint();
        s.addDamageDealt(1.5);
        s.addDamageDealt(2);
        s.addDamageTaken(4);
        s.addGoldGained(10);
        s.addGoldGained(5);
        s.addXpGained(3);

        assertEquals(2, s.getMonstersKilled());
        assertEquals(1, s.getTimesFainted());
        assertEquals(3.5, s.getDamageDealt(), EPS);
        assertEquals(4, s.getDamageTaken(), EPS);
        assertEquals(15, s.getGoldGained());
        assertEquals(3, s.getXpGained());
    }

    @Test
    void reportsLiveHeroDetails() {
        Hero h = hero("H");
        HeroStats s = new HeroStats(h);
        h.setLevel(4);
        assertEquals("H", s.getHeroName());
        assertEquals(4, s.getHeroLevel());
    }

    @Test
    void nullHeroUsesFallbacks() {
        HeroStats s = new HeroStats(null);
        assertEquals("Unknown", s.getHeroName());
        assertEquals(0, s.getHeroLevel());
    }
}
