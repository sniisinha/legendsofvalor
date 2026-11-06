package legends.characters;

import legends.items.Item;
import legends.items.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private final Item sword = new Weapon("Sword", 500, 1, 800, 1);
    private final Item bow = new Weapon("Bow", 300, 2, 500, 2);

    @Test
    void itemsKeepInsertionOrder() {
        Inventory inv = new Inventory();
        inv.addItem(sword);
        inv.add(bow);
        assertEquals(2, inv.getItems().size());
        assertSame(sword, inv.getItems().get(0));
        assertSame(bow, inv.getItems().get(1));
    }

    @Test
    void removeTakesOutTheItem() {
        Inventory inv = new Inventory();
        inv.addItem(sword);
        inv.addItem(bow);
        inv.removeItem(sword);
        assertFalse(inv.getItems().contains(sword));
        inv.remove(bow);
        assertTrue(inv.getItems().isEmpty());
    }

    @Test
    void removingAMissingItemIsHarmless() {
        Inventory inv = new Inventory();
        inv.addItem(sword);
        inv.removeItem(bow);
        assertEquals(1, inv.getItems().size());
    }

    @Test
    void duplicatesAreRemovedOneAtATime() {
        Inventory inv = new Inventory();
        inv.addItem(sword);
        inv.addItem(sword);
        inv.removeItem(sword);
        assertEquals(1, inv.getItems().size());
    }

    @Test
    void printingEmptyAndFullInventoriesDoesNotThrow() {
        Inventory inv = new Inventory();
        assertDoesNotThrow(inv::print);
        assertDoesNotThrow(() -> inv.printFormatted(40));
        inv.addItem(sword);
        assertDoesNotThrow(inv::print);
        assertDoesNotThrow(() -> inv.printFormatted(5)); // narrower than the text
    }
}
