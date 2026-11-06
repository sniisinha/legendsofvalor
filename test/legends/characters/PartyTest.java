package legends.characters;

import legends.world.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static legends.TestSupport.EPS;
import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class PartyTest {

    private Party party;
    private Hero alive;
    private Hero fainted;

    @BeforeEach
    void setUp() {
        party = new Party();
        alive = hero("Alive");
        fainted = hero("Fainted");
        fainted.setHP(0);
        party.addHero(alive);
        party.addHero(fainted);
    }

    @Test
    void newPartyIsEmptyAtOrigin() {
        Party empty = new Party();
        assertEquals(0, empty.size());
        assertEquals(0, empty.getPosition().row);
        assertEquals(0, empty.getPosition().col);
    }

    @Test
    void moveToUpdatesPosition() {
        Position p = new Position(3, 4);
        party.moveTo(p);
        assertSame(p, party.getPosition());
    }

    @Test
    void aliveQueriesIgnoreFaintedHeroes() {
        assertEquals(2, party.size());
        assertEquals(1, party.getAliveHeroes().size());
        assertSame(alive, party.getAliveHeroes().get(0));
        assertTrue(party.anyAlive());
        assertFalse(party.allDead());
    }

    @Test
    void randomAliveHeroNeverPicksFaintedHero() {
        for (int i = 0; i < 50; i++) {
            assertSame(alive, party.getRandomAliveHero());
        }
    }

    @Test
    void wipedPartyHasNoTargets() {
        alive.setHP(0);
        assertNull(party.getRandomAliveHero());
        assertTrue(party.allDead());
        assertFalse(party.anyAlive());
    }

    @Test
    void reviveRestoresOnlyFaintedHeroesToHalfHp() {
        alive.setHP(80);
        party.reviveFallenHeroes();
        assertEquals(80, alive.getHP(), EPS);
        assertEquals(50, fainted.getHP(), EPS);
    }

    @Test
    void regenerationHealsTenPercentUpToMaxHp() {
        alive.setHP(50);
        party.regenerateAfterBattle();
        assertEquals(55, alive.getHP(), 1e-6);
        assertEquals(55, alive.getMP(), 1e-6);

        alive.setHP(95);
        party.regenerateAfterBattle();
        assertEquals(100, alive.getHP(), EPS);
    }

    @Test
    void regenerationRevivesFaintedHeroesAtHalfHp() {
        party.regenerateAfterBattle();
        assertEquals(50, fainted.getHP(), EPS);
        assertEquals(52.5, fainted.getMP(), 1e-6);
    }
}
