package legends.characters;

import legends.items.Armor;
import legends.items.Potion;
import legends.items.PotionAttribute;
import legends.items.Spell;
import legends.items.SpellType;
import legends.items.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static legends.TestSupport.EPS;
import static org.junit.jupiter.api.Assertions.*;

class HeroTest {

    private static Hero warrior() {
        return new Warrior("W", 1, 100, 50, 700, 600, 500);
    }

    private static Spell spell(double manaCost) {
        return new Spell("S", 100, 1, 100, manaCost, SpellType.FIRE);
    }

    private static Potion potion(int amount, PotionAttribute... attrs) {
        return new Potion("P", 100, 1, amount, Arrays.asList(attrs));
    }

    @Test
    void newHeroStartsWith100GoldAndNoExperience() {
        Hero h = warrior();
        assertEquals(100, h.getGold());
        assertEquals(0, h.getExperience());
        assertNull(h.getWeapon());
        assertNull(h.getArmor());
        assertTrue(h.getInventory().getItems().isEmpty());
    }

    @Test
    void attackDamageWithoutWeaponUsesStrengthOnly() {
        assertEquals(217.0, warrior().getAttackDamage(), EPS);
    }

    @Test
    void attackDamageIncludesEquippedWeapon() {
        Hero h = warrior();
        h.equipWeapon(new Weapon("Sword", 500, 1, 800, 1));
        assertEquals(465.0, h.getAttackDamage(), EPS);
    }

    @ParameterizedTest
    @CsvSource({"0, 0.0", "100, 0.2", "249, 0.498", "250, 0.5", "1000, 0.5"})
    void dodgeChanceScalesWithAgilityAndCapsAtHalf(double agility, double expected) {
        Hero h = warrior();
        h.setAgility(agility);
        assertEquals(expected, h.getDodgeChance(), EPS);
    }

    @Test
    void damageWithoutArmorIsTakenInFull() {
        Hero h = warrior();
        h.takeDamage(30);
        assertEquals(70, h.getHP(), EPS);
    }

    @Test
    void armorReducesDamage() {
        Hero h = warrior();
        h.equipArmor(new Armor("A", 100, 1, 25));
        h.takeDamage(30);
        assertEquals(95, h.getHP(), EPS);
    }

    @Test
    void armorAbsorbingAllDamageNeverHeals() {
        Hero h = warrior();
        h.equipArmor(new Armor("A", 100, 1, 500));
        h.takeDamage(30);
        assertEquals(100, h.getHP(), EPS);
    }

    @Test
    void hpNeverDropsBelowZeroAndHeroFaints() {
        Hero h = warrior();
        h.takeDamage(1000);
        assertEquals(0, h.getHP(), EPS);
        assertFalse(h.isAlive());
    }

    @Test
    void experienceBelowThresholdDoesNotLevel() {
        Hero h = warrior();
        h.addExperience(9);
        assertEquals(1, h.getLevel());
        assertEquals(9, h.getExperience());
    }

    @Test
    void experienceAtThresholdLevelsUpAndConsumesXp() {
        Hero h = warrior();
        h.addExperience(10);
        assertEquals(2, h.getLevel());
        assertEquals(0, h.getExperience());
    }

    @Test
    void leftoverExperienceCarriesOver() {
        Hero h = warrior();
        h.addExperience(25);
        assertEquals(2, h.getLevel());
        assertEquals(15, h.getExperience());
    }

    @Test
    void largeExperienceGainLevelsMultipleTimes() {
        Hero h = warrior();
        h.addExperience(30); // 10 for level 2, then 20 for level 3
        assertEquals(3, h.getLevel());
        assertEquals(0, h.getExperience());
    }

    @Test
    void warriorLevelUpFavorsStrengthAndAgility() {
        Hero h = new Warrior("W", 1, 40, 50, 100, 100, 100);
        h.levelUp();
        assertLevelUp(h, 110, 105, 110);
    }

    @Test
    void paladinLevelUpFavorsStrengthAndDexterity() {
        Hero h = new Paladin("P", 1, 40, 50, 100, 100, 100);
        h.levelUp();
        assertLevelUp(h, 110, 110, 105);
    }

    @Test
    void sorcererLevelUpFavorsDexterityAndAgility() {
        Hero h = new Sorcerer("S", 1, 40, 50, 100, 100, 100);
        h.levelUp();
        assertLevelUp(h, 105, 110, 110);
    }

    private static void assertLevelUp(Hero h, double str, double dex, double agi) {
        assertEquals(2, h.getLevel());
        assertEquals(200, h.getHP(), EPS);
        assertEquals(55, h.getMP(), 1e-6);
        assertEquals(str, h.getStrength(), 1e-6);
        assertEquals(dex, h.getDexterity(), 1e-6);
        assertEquals(agi, h.getAgility(), 1e-6);
    }

    @ParameterizedTest
    @CsvSource({
            "HEALTH, 110, 50, 700, 600, 500",
            "MANA, 100, 60, 700, 600, 500",
            "STRENGTH, 100, 50, 710, 600, 500",
            "DEXTERITY, 100, 50, 700, 610, 500",
            "AGILITY, 100, 50, 700, 600, 510"
    })
    void potionBoostsOnlyItsAttribute(PotionAttribute attr, double hp, double mp,
                                      double str, double dex, double agi) {
        Hero h = warrior();
        h.usePotion(potion(10, attr));
        assertEquals(hp, h.getHP(), EPS);
        assertEquals(mp, h.getMP(), EPS);
        assertEquals(str, h.getStrength(), EPS);
        assertEquals(dex, h.getDexterity(), EPS);
        assertEquals(agi, h.getAgility(), EPS);
    }

    @Test
    void potionWithSeveralAttributesBoostsEachOne() {
        Hero h = warrior();
        h.usePotion(potion(10, PotionAttribute.HEALTH, PotionAttribute.MANA));
        assertEquals(110, h.getHP(), EPS);
        assertEquals(60, h.getMP(), EPS);
    }

    @Test
    void potionWithNoAttributesChangesNothing() {
        Hero h = warrior();
        h.usePotion(new Potion("Empty", 1, 1, 10, Collections.<PotionAttribute>emptyList()));
        assertEquals(100, h.getHP(), EPS);
    }

    @Test
    void canCastRequiresEnoughManaAndASpell() {
        Hero h = warrior();
        assertTrue(h.canCast(spell(50)));
        assertFalse(h.canCast(spell(50.5)));
        assertFalse(h.canCast(null));
    }

    @Test
    void spendManaNeverDropsBelowZero() {
        Hero h = warrior();
        h.spendMana(20);
        assertEquals(30, h.getMP(), EPS);
        h.spendMana(1000);
        assertEquals(0, h.getMP(), EPS);
    }

    @Test
    void canAffordIncludesExactPrice() {
        Hero h = warrior();
        assertTrue(h.canAfford(new Weapon("Cheap", 100, 1, 1, 1)));
        assertFalse(h.canAfford(new Weapon("Costly", 101, 1, 1, 1)));
    }

    @Test
    void goldCanBeSpentAndEarned() {
        Hero h = warrior();
        h.spendGold(30);
        h.earnGold(5);
        assertEquals(75, h.getGold());
    }

    @Test
    void equippingReplacesPreviousItems() {
        Hero h = warrior();
        Weapon second = new Weapon("Second", 1, 1, 1, 1);
        Armor secondArmor = new Armor("Second", 1, 1, 1);
        h.equipWeapon(new Weapon("First", 1, 1, 1, 1));
        h.equipArmor(new Armor("First", 1, 1, 1));
        h.equipWeapon(second);
        h.equipArmor(secondArmor);
        assertSame(second, h.getWeapon());
        assertSame(secondArmor, h.getArmor());
    }
}
