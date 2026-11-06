package legends.market;

import legends.items.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    @Test
    void mixedListIsSortedIntoCategories() {
        Weapon w = new Weapon("W", 1, 1, 1, 1);
        Armor a = new Armor("A", 1, 1, 1);
        Potion p = new Potion("P", 1, 1, 1, Collections.singletonList(PotionAttribute.HEALTH));
        Spell s = new Spell("S", 1, 1, 1, 1, SpellType.ICE);

        Market market = new Market(Arrays.<Item>asList(s, p, a, w));

        assertEquals(Collections.singletonList(w), market.getWeapons());
        assertEquals(Collections.singletonList(a), market.getArmor());
        assertEquals(Collections.singletonList(p), market.getPotions());
        assertEquals(Collections.singletonList(s), market.getSpells());
    }

    @Test
    void emptyListGivesEmptyCategories() {
        Market market = new Market(Collections.<Item>emptyList());
        assertTrue(market.getWeapons().isEmpty());
        assertTrue(market.getArmor().isEmpty());
        assertTrue(market.getPotions().isEmpty());
        assertTrue(market.getSpells().isEmpty());
    }

    @Test
    void categorizedConstructorKeepsTheGivenLists() {
        Weapon w = new Weapon("W", 1, 1, 1, 1);
        Market market = new Market(Collections.singletonList(w),
                Collections.<Armor>emptyList(),
                Collections.<Potion>emptyList(),
                Collections.<Spell>emptyList());
        assertSame(w, market.getWeapons().get(0));
        assertTrue(market.getArmor().isEmpty());
    }
}
