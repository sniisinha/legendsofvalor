package legends.data;

import legends.characters.*;
import legends.items.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static legends.TestSupport.EPS;
import static org.junit.jupiter.api.Assertions.*;

/** Reads the real files in src/data, which also catches malformed game data. */
class DataLoaderTest {

    private final DataLoader loader = new DataLoader();
    private List<Monster> savedMonsters;

    @BeforeEach
    void saveGlobals() {
        savedMonsters = DataLoader.globalMonsters;
    }

    @AfterEach
    void restoreGlobals() {
        DataLoader.globalMonsters = savedMonsters;
    }

    private static <T> T named(List<? extends T> items, Function<T, String> name, String wanted) {
        for (T item : items) {
            if (wanted.equals(name.apply(item))) return item;
        }
        return fail("Missing " + wanted);
    }

    @Test
    void warriorColumnsLoadIntoTheRightStats() {
        Warrior w = named(loader.loadWarriors(), Warrior::getName, "Gaerdal_Ironhand");
        assertEquals(1, w.getLevel());
        assertEquals(100, w.getHP(), EPS);
        assertEquals(100, w.getMP(), EPS);
        assertEquals(700, w.getStrength(), EPS);
        assertEquals(500, w.getAgility(), EPS);
        assertEquals(600, w.getDexterity(), EPS);
        assertEquals(1354, w.getGold());
        assertEquals(7, w.getExperience());
    }

    @Test
    void allHeroesCombineEachClass() {
        int expected = loader.loadWarriors().size()
                + loader.loadPaladins().size()
                + loader.loadSorcerers().size();
        assertFalse(loader.loadPaladins().isEmpty());
        assertFalse(loader.loadSorcerers().isEmpty());
        assertEquals(expected, loader.loadAllHeroes().size());
    }

    @Test
    void heroFactoryMatchesDataLoader() {
        assertHeroesMatch(loader.loadWarriors(), HeroFactory.loadWarriors("Warriors.txt"));
        assertHeroesMatch(loader.loadPaladins(), HeroFactory.loadPaladins("Paladins.txt"));
        assertHeroesMatch(loader.loadSorcerers(), HeroFactory.loadSorcerers("Sorcerers.txt"));
    }

    private static void assertHeroesMatch(List<? extends Hero> expected, List<Hero> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getClass(), actual.get(i).getClass());
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }
    }

    @Test
    void monsterColumnsLoadIntoTheRightStats() {
        Monster blinky = named(MonsterFactory.loadMonsters("Spirits.txt", MonsterType.SPIRIT),
                Monster::getName, "Blinky");
        assertTrue(blinky instanceof Spirit);
        assertEquals(1, blinky.getLevel());
        assertEquals(450, blinky.getDamage(), EPS);
        assertEquals(350, blinky.getDefense(), EPS);
        assertEquals(0.35, blinky.getDodgeChance(), EPS);
        assertEquals(100, blinky.getHP(), EPS);
    }

    @Test
    void monsterTypesProduceTheirSubclass() {
        assertTrue(MonsterFactory.loadMonsters("Dragons.txt", MonsterType.DRAGON).get(0) instanceof Dragon);
        assertTrue(MonsterFactory.loadMonsters("Exoskeletons.txt", MonsterType.EXOSKELETON).get(0) instanceof Exoskeleton);
    }

    @Test
    void loadAllMonstersFillsTheGlobalPool() {
        List<Monster> all = loader.loadAllMonsters();
        assertFalse(all.isEmpty());
        assertSame(all, DataLoader.globalMonsters);
    }

    @Test
    void weaponColumnsLoad() {
        Weapon sword = named(ItemFactory.loadWeapons("Weaponry.txt"), Weapon::getName, "Sword");
        assertEquals(500, sword.getPrice());
        assertEquals(1, sword.getRequiredLevel());
        assertEquals(800, sword.getDamage());
        assertEquals(1, sword.getHands());
    }

    @Test
    void armorColumnsLoad() {
        Armor shield = named(ItemFactory.loadArmor("Armory.txt"), Armor::getName, "Platinum_Shield");
        assertEquals(150, shield.getPrice());
        assertEquals(1, shield.getRequiredLevel());
        assertEquals(200, shield.getReduction());
    }

    @Test
    void spellsTakeTheRequestedType() {
        Spell flame = named(ItemFactory.loadSpells("FireSpells.txt", SpellType.FIRE),
                Spell::getName, "Breath_of_Fire");
        assertEquals(350, flame.getPrice());
        assertEquals(1, flame.getRequiredLevel());
        assertEquals(450, flame.getDamage(), EPS);
        assertEquals(100, flame.getManaCost(), EPS);
        assertEquals(SpellType.FIRE, flame.getType());
    }

    @Test
    void potionAttributesParseInEachFormat() {
        List<Potion> potions = ItemFactory.loadPotions("Potions.txt");

        Potion healing = named(potions, Potion::getName, "Healing_Potion");
        assertEquals(Arrays.asList(PotionAttribute.HEALTH), healing.getAttributes());
        assertEquals(100, healing.getEffectAmount());

        Potion tears = named(potions, Potion::getName, "Mermaid_Tears");
        assertEquals(Arrays.asList(PotionAttribute.HEALTH, PotionAttribute.MANA,
                PotionAttribute.STRENGTH, PotionAttribute.AGILITY), tears.getAttributes());

        Potion ambrosia = named(potions, Potion::getName, "Ambrosia");
        assertEquals(new HashSet<PotionAttribute>(Arrays.asList(PotionAttribute.values())),
                new HashSet<PotionAttribute>(ambrosia.getAttributes()));
    }

    @Test
    void allItemsCombineEveryFile() {
        List<Item> items = loader.loadAllItems();
        assertTrue(items.stream().anyMatch(i -> i instanceof Weapon));
        assertTrue(items.stream().anyMatch(i -> i instanceof Armor));
        assertTrue(items.stream().anyMatch(i -> i instanceof Potion));
        assertEquals(new HashSet<SpellType>(Arrays.asList(SpellType.values())),
                items.stream().filter(i -> i instanceof Spell)
                        .map(i -> ((Spell) i).getType()).collect(Collectors.toSet()));
    }

    @Test
    void missingFilesLoadAsEmpty() {
        assertTrue(FileUtils.readResource("does_not_exist.txt").isEmpty());
        assertTrue(ItemFactory.loadWeapons("does_not_exist.txt").isEmpty());
        assertTrue(MonsterFactory.loadMonsters("does_not_exist.txt", MonsterType.DRAGON).isEmpty());
    }

    @Test
    void generatedMonstersMatchPartySizeAndLevel() {
        Monster low = new Dragon("Low", 1, 1, 1, 0);
        Monster mid = new Dragon("Mid", 5, 1, 1, 0);
        DataLoader.globalMonsters = new ArrayList<Monster>(Arrays.asList(low, mid));

        Party party = new Party();
        party.addHero(new Warrior("A", 5, 500, 1, 1, 1, 1));
        party.addHero(new Warrior("B", 5, 500, 1, 1, 1, 1));

        List<Monster> generated = MonsterFactory.generateMonstersForParty(party);

        assertEquals(2, generated.size());
        for (Monster m : generated) {
            assertEquals("Mid", m.getName());
        }
    }

    @Test
    void generatedMonstersAreCopies() {
        Monster only = new Dragon("Only", 1, 1, 1, 0);
        DataLoader.globalMonsters = new ArrayList<Monster>(Arrays.asList(only));
        Party party = new Party();
        party.addHero(new Warrior("A", 1, 100, 1, 1, 1, 1));

        Monster generated = MonsterFactory.generateMonstersForParty(party).get(0);
        generated.takeDamage(50);

        assertNotSame(only, generated);
        assertEquals(100, only.getHP(), EPS);
    }

    @Test
    void generationFallsBackWhenNoMonsterIsCloseInLevel() {
        DataLoader.globalMonsters = new ArrayList<Monster>(Arrays.asList(new Dragon("Far", 10, 1, 1, 0)));
        Party party = new Party();
        party.addHero(new Warrior("A", 1, 100, 1, 1, 1, 1));

        assertEquals("Far", MonsterFactory.generateMonstersForParty(party).get(0).getName());
    }

    @Test
    void generationFailsClearlyWithoutLoadedMonsters() {
        DataLoader.globalMonsters = new ArrayList<Monster>();
        assertThrows(RuntimeException.class, () -> MonsterFactory.generateMonstersForParty(new Party()));
    }
}
