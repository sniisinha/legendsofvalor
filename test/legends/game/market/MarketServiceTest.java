package legends.game.market;

import legends.characters.Hero;
import legends.items.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static legends.TestSupport.hero;
import static org.junit.jupiter.api.Assertions.*;

class MarketServiceTest {

    private final MarketService service = new MarketService();
    private final Weapon sword = new Weapon("Sword", 60, 1, 800, 1);

    @Test
    void buyDeductsGoldAndAddsItem() {
        Hero h = hero("Buyer");
        service.buy(h, sword);
        assertEquals(40, h.getGold());
        assertTrue(h.getInventory().getItems().contains(sword));
    }

    @Test
    void sellPaysHalfPriceAndRemovesItem() {
        Hero h = hero("Seller");
        h.addItem(sword);
        assertEquals(30, service.sell(h, sword));
        assertEquals(130, h.getGold());
        assertFalse(h.getInventory().getItems().contains(sword));
    }

    @Test
    void sellRoundsOddPricesDown() {
        Hero h = hero("Seller");
        Weapon odd = new Weapon("Odd", 61, 1, 1, 1);
        h.addItem(odd);
        assertEquals(30, service.sell(h, odd));
    }

    @Test
    void categorizeGroupsByTypeWithoutChangingInput() {
        Armor armor = new Armor("A", 1, 1, 1);
        Potion potion = new Potion("P", 1, 1, 1, Collections.singletonList(PotionAttribute.MANA));
        Spell spell = new Spell("S", 1, 1, 1, 1, SpellType.FIRE);
        java.util.List<Item> items = Arrays.<Item>asList(spell, sword, potion, armor);

        MarketService.InventoryCategories cats = service.categorizeInventory(items);

        assertEquals(Collections.singletonList(sword), cats.weapons);
        assertEquals(Collections.singletonList(armor), cats.armors);
        assertEquals(Collections.singletonList(potion), cats.potions);
        assertEquals(Collections.singletonList(spell), cats.spells);
        assertEquals(4, items.size());
    }
}
