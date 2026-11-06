package legends.valor.turn;

import legends.characters.Inventory;
import legends.characters.Monster;
import legends.items.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static legends.TestSupport.monster;
import static legends.TestSupport.scriptedInput;
import static org.junit.jupiter.api.Assertions.*;

class HeroTurnUIHelperTest {

    @ParameterizedTest
    @CsvSource({"1, 0", "3, 2", "' 2 ', 1", "0, -1", "4, -1", "abc, -1"})
    void readIndexConvertsValidChoices(String answer, int expected) {
        assertEquals(expected, new HeroTurnUIHelper(scriptedInput(answer)).readIndex("?", 3));
    }

    @Test
    void readIndexHandlesClosedInput() {
        assertEquals(-1, new HeroTurnUIHelper(scriptedInput()).readIndex("?", 3));
    }

    @Test
    void singleMonsterIsChosenWithoutPrompting() {
        Monster m = monster("Only");
        assertSame(m, new HeroTurnUIHelper(scriptedInput()).pickMonster(Collections.singletonList(m)));
    }

    @Test
    void emptyChoicesReturnNull() {
        HeroTurnUIHelper ui = new HeroTurnUIHelper(scriptedInput());
        assertNull(ui.pickMonster(Collections.<Monster>emptyList()));
        assertNull(ui.pickSpell(null));
        assertNull(ui.pickPosition(null));
    }

    @Test
    void inventoryFiltersReturnOnlyTheRequestedType() {
        Weapon sword = new Weapon("Sword", 1, 1, 1, 1);
        Armor plate = new Armor("Plate", 1, 1, 1);
        Potion heal = new Potion("Heal", 1, 1, 1, Collections.singletonList(PotionAttribute.HEALTH));
        Spell bolt = new Spell("Bolt", 1, 1, 1, 1, SpellType.LIGHTNING);
        Inventory inv = new Inventory();
        for (Item item : Arrays.<Item>asList(sword, plate, heal, bolt)) inv.addItem(item);

        HeroTurnUIHelper ui = new HeroTurnUIHelper(scriptedInput());

        assertEquals(Collections.singletonList(sword), ui.getWeapons(inv));
        assertEquals(Collections.singletonList(plate), ui.getArmors(inv));
        assertEquals(Collections.singletonList(heal), ui.getPotions(inv));
        assertEquals(Collections.singletonList(bolt), ui.getSpells(inv));
    }

    @Test
    void pickersReturnTheChosenEntry() {
        Weapon first = new Weapon("First", 1, 1, 1, 1);
        Weapon second = new Weapon("Second", 1, 1, 1, 1);
        List<Weapon> weapons = Arrays.asList(first, second);
        assertSame(second, new HeroTurnUIHelper(scriptedInput("2")).pickWeapon(weapons));
        assertNull(new HeroTurnUIHelper(scriptedInput("0")).pickWeapon(weapons));
    }
}
