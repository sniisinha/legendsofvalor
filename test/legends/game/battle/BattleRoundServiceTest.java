package legends.game.battle;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class BattleRoundServiceTest {

    private final BattleRoundService service = new BattleRoundService();
    private Party party;
    private Hero a;
    private Hero b;

    @BeforeEach
    void setUp() {
        a = hero("A");
        b = hero("B");
        party = new Party();
        party.addHero(a);
        party.addHero(b);
    }

    @Test
    void noMonstersCountsAsAllDead() {
        assertTrue(service.areAllMonstersDead(null));
        assertTrue(service.areAllMonstersDead(Collections.<Monster>emptyList()));
    }

    @Test
    void oneLivingMonsterKeepsTheBattleGoing() {
        Monster dead = monster("Dead");
        dead.setHP(0);
        assertFalse(service.areAllMonstersDead(Arrays.asList(dead, monster("Alive"))));
        assertTrue(service.areAllMonstersDead(Collections.singletonList(dead)));
    }

    @Test
    void regenerationRestoresTenPercent() {
        a.setHP(50);
        a.setMP(20);
        service.regenerateBetweenRounds(party);
        assertEquals(55, a.getHP(), 1e-6);
        assertEquals(22, a.getMP(), 1e-6);
    }

    @Test
    void regenerationIsCappedByLevel() {
        a.setHP(99);
        a.setMP(49);
        service.regenerateBetweenRounds(party);
        assertEquals(100, a.getHP(), EPS);
        assertEquals(50, a.getMP(), EPS);
    }

    @Test
    void faintedHeroesDoNotRegenerate() {
        a.setHP(0);
        service.regenerateBetweenRounds(party);
        assertEquals(0, a.getHP(), EPS);
    }

    @Test
    void survivorsEarnXpAndGold() {
        service.rewardHeroes(party, Collections.<Monster>singletonList(new Dragon("D", 3, 1, 1, 0)));
        assertEquals(4, a.getExperience()); // party size 2 * 2
        assertEquals(400, a.getGold());     // 100 + level 3 * 100
    }

    @Test
    void fallenHeroesReviveAtHalfWithoutRewards() {
        b.setHP(0);
        b.setMP(0);
        service.rewardHeroes(party, Collections.<Monster>singletonList(new Dragon("D", 3, 1, 1, 0)));
        assertEquals(50, b.getHP(), EPS);
        assertEquals(25, b.getMP(), EPS);
        assertEquals(0, b.getExperience());
        assertEquals(100, b.getGold());
    }

    @Test
    void rewardsDefaultToLevelOneWithoutMonsters() {
        service.rewardHeroes(party, Collections.<Monster>emptyList());
        assertEquals(200, a.getGold());
    }

    @Test
    void rewardsCanLevelUpSurvivors() {
        a.setExperience(8);
        service.rewardHeroes(party, Collections.<Monster>emptyList());
        assertEquals(2, a.getLevel());
    }
}
