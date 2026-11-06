package legends.items;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static legends.TestSupport.EPS;
import static org.junit.jupiter.api.Assertions.*;

class ItemsTest {

    @Test
    void weaponExposesItsStats() {
        Weapon w = new Weapon("Bow", 300, 2, 500, 2);
        assertEquals("Bow", w.getName());
        assertEquals(300, w.getPrice());
        assertEquals(2, w.getRequiredLevel());
        assertEquals(500, w.getDamage());
        assertEquals(2, w.getHandsRequired());
        assertEquals(2, w.getHands());
        assertEquals("Bow (Lvl 2, DMG: 500, Hands: 2)", w.toString());
    }

    @Test
    void armorExposesItsStats() {
        Armor a = new Armor("Shield", 150, 1, 200);
        assertEquals("Shield", a.getName());
        assertEquals(150, a.getPrice());
        assertEquals(1, a.getRequiredLevel());
        assertEquals(200, a.getReduction());
        assertEquals("Shield (Reduction: 200)", a.toString());
    }

    @Test
    void potionExposesItsStats() {
        Potion p = new Potion("Tears", 850, 5, 100,
                Arrays.asList(PotionAttribute.HEALTH, PotionAttribute.MANA));
        assertEquals("Tears", p.getName());
        assertEquals(850, p.getPrice());
        assertEquals(5, p.getRequiredLevel());
        assertEquals(100, p.getEffectAmount());
        assertEquals(Arrays.asList(PotionAttribute.HEALTH, PotionAttribute.MANA), p.getAttributes());
        assertEquals(Collections.emptyList(),
                new Potion("Empty", 1, 1, 1, Collections.<PotionAttribute>emptyList()).getAttributes());
    }

    @Test
    void spellExposesItsStats() {
        Spell s = new Spell("Flame", 700, 4, 850, 300, SpellType.FIRE);
        assertEquals("Flame", s.getName());
        assertEquals(700, s.getPrice());
        assertEquals(4, s.getRequiredLevel());
        assertEquals(850, s.getDamage(), EPS);
        assertEquals(300, s.getManaCost(), EPS);
        assertEquals(SpellType.FIRE, s.getType());
    }
}
