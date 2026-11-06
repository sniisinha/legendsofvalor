package legends.characters;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static legends.TestSupport.EPS;
import static org.junit.jupiter.api.Assertions.*;

class MonsterTest {

    private static List<Monster> oneOfEach() {
        return Arrays.asList(
                new Dragon("D", 3, 300, 200, 0.2),
                new Spirit("S", 3, 300, 200, 0.2),
                new Exoskeleton("E", 3, 300, 200, 0.2));
    }

    @Test
    void hpIsLevelTimes100() {
        for (Monster m : oneOfEach()) {
            assertEquals(300, m.getHP(), EPS);
        }
    }

    @Test
    void takeDamageNeverDropsBelowZero() {
        Monster m = oneOfEach().get(0);
        m.takeDamage(120);
        assertEquals(180, m.getHP(), EPS);
        assertTrue(m.isAlive());
        m.takeDamage(1000);
        assertEquals(0, m.getHP(), EPS);
        assertFalse(m.isAlive());
    }

    @Test
    void defenseReducesDamageByAShareThatGrowsWithDefense() {
        Monster m = new Dragon("D", 1, 1, 0, 0);
        assertEquals(100, m.reduceByDefense(100), EPS);

        m.setDefense(1000);
        assertEquals(50, m.reduceByDefense(100), EPS);

        m.setDefense(9000);
        assertEquals(10, m.reduceByDefense(100), EPS);
    }

    @Test
    void defenseDoesNotChangeHpByItself() {
        Monster m = oneOfEach().get(0);
        m.reduceByDefense(100);
        assertEquals(300, m.getHP(), EPS);
    }

    @Test
    void copyKeepsConcreteTypeAndStats() {
        for (Monster original : oneOfEach()) {
            original.setHP(123);
            Monster copy = original.copy();

            assertNotSame(original, copy);
            assertEquals(original.getClass(), copy.getClass());
            assertEquals(original.getName(), copy.getName());
            assertEquals(original.getLevel(), copy.getLevel());
            assertEquals(123, copy.getHP(), EPS);
            assertEquals(original.getDamage(), copy.getDamage(), EPS);
            assertEquals(original.getDefense(), copy.getDefense(), EPS);
            assertEquals(original.getDodgeChance(), copy.getDodgeChance(), EPS);
        }
    }

    @Test
    void changingACopyLeavesTheOriginalAlone() {
        for (Monster original : oneOfEach()) {
            Monster copy = original.copy();
            copy.takeDamage(50);
            copy.applyFireDebuff(50);
            assertEquals(300, original.getHP(), EPS);
            assertEquals(200, original.getDefense(), EPS);
        }
    }

    @Test
    void debuffsLowerTheirStat() {
        Monster m = oneOfEach().get(0);
        m.applyFireDebuff(50);
        m.applyIceDebuff(100);
        m.applyLightningDebuff(0.05);
        assertEquals(150, m.getDefense(), EPS);
        assertEquals(200, m.getDamage(), EPS);
        assertEquals(0.15, m.getDodgeChance(), EPS);
    }

    @Test
    void debuffsStopAtZero() {
        Monster m = oneOfEach().get(0);
        m.applyFireDebuff(1000);
        m.applyIceDebuff(1000);
        m.applyLightningDebuff(1000);
        assertEquals(0, m.getDefense(), EPS);
        assertEquals(0, m.getDamage(), EPS);
        assertEquals(0, m.getDodgeChance(), EPS);
    }
}
