package legends.valor.turn;

import legends.characters.Hero;
import legends.items.Armor;
import legends.items.Potion;
import legends.items.PotionAttribute;
import legends.items.Weapon;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class HeroEquipmentActionsTest {

    private ValorBoard board;
    private Hero h;
    private final Weapon sword = new Weapon("Sword", 1, 1, 1, 1);
    private final Armor plate = new Armor("Plate", 1, 1, 1);
    private final Potion heal = new Potion("Heal", 1, 1, 10, Collections.singletonList(PotionAttribute.HEALTH));

    @BeforeEach
    void setUp() {
        board = plainBoard();
        h = hero("Hero");
    }

    private HeroEquipmentActions actions(String... answers) {
        return new HeroEquipmentActions(board, new ValorMovement(board),
                new HeroTurnUIHelper(scriptedInput(answers)), null, null);
    }

    @Test
    void usingAPotionAppliesItAndConsumesIt() {
        h.addItem(heal);
        assertTrue(actions("1").usePotion(h));
        assertEquals(110, h.getHP(), EPS);
        assertTrue(h.getInventory().getItems().isEmpty());
    }

    @Test
    void noPotionsMeansNoTurnUsed() {
        assertFalse(actions().usePotion(h));
    }

    @Test
    void cancellingPotionKeepsIt() {
        h.addItem(heal);
        assertFalse(actions("0").usePotion(h));
        assertEquals(1, h.getInventory().getItems().size());
    }

    @Test
    void equipsChosenWeapon() {
        h.addItem(sword);
        assertTrue(actions("1").equip(h, scriptedInput("1")));
        assertSame(sword, h.getWeapon());
    }

    @Test
    void equipsChosenArmor() {
        h.addItem(plate);
        assertTrue(actions("1").equip(h, scriptedInput("2")));
        assertSame(plate, h.getArmor());
    }

    @Test
    void equipFailsWithoutMatchingItems() {
        assertFalse(actions().equip(h, scriptedInput("1")));
        assertFalse(actions().equip(h, scriptedInput("2")));
    }

    @Test
    void cancelledOrInvalidEquipChoicesDoNothing() {
        h.addItem(sword);
        assertFalse(actions().equip(h, scriptedInput("0")));
        assertFalse(actions().equip(h, scriptedInput("9")));
        assertFalse(actions().equip(h, scriptedInput()));
        assertNull(h.getWeapon());
    }

    @Test
    void marketIsRefusedAwayFromTheNexus() {
        place(board, h, 5, 0);
        assertFalse(actions().openMarket(h));
    }

    @Test
    void marketIsRefusedOffTheBoard() {
        assertFalse(actions().openMarket(h));
    }

    @Test
    void marketNeedsItsDependenciesEvenOnTheNexus() {
        place(board, h, 7, 0);
        assertFalse(actions().openMarket(h));
    }
}
