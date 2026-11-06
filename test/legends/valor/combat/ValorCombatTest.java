package legends.valor.combat;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Armor;
import legends.items.Spell;
import legends.items.SpellType;
import legends.items.Weapon;
import legends.stats.GameStats;
import legends.stats.HeroStats;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorCellType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

import static legends.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ValorCombatTest {

    private ValorBoard board;
    private Hero h;
    private Monster strong;
    private GameStats stats;
    private ValorCombat combat;

    @BeforeEach
    void setUp() {
        board = plainBoard();
        h = hero("Hero");
        strong = new Dragon("Tank", 5, 400, 100, 0.0); // 500 HP
        stats = new GameStats(GameStats.GameMode.LEGENDS_OF_VALOR, Collections.singletonList(h));
        combat = new ValorCombat(board, stats);
    }

    private HeroStats heroStats() {
        return stats.statsFor(h);
    }

    private static Spell spell(SpellType type) {
        return new Spell("Spell", 100, 1, 100, 20, type);
    }

    @ParameterizedTest
    @CsvSource({"4, 3", "2, 3", "3, 4", "3, 3"})
    void sameTileAndOrthogonalNeighborsAreInRange(int row, int col) {
        place(board, h, 3, 3);
        place(board, strong, row, col);
        assertEquals(Collections.singletonList(strong), combat.getMonstersInRange(h));
        assertEquals(Collections.singletonList(h), combat.getHeroesInRange(strong));
    }

    @ParameterizedTest
    @CsvSource({"2, 4", "4, 4", "3, 7", "1, 3", "5, 3"})
    void diagonalsAndDistantTilesAreOutOfRange(int row, int col) {
        place(board, h, 3, 3);
        place(board, strong, row, col);
        assertTrue(combat.getMonstersInRange(h).isEmpty());
        assertTrue(combat.getHeroesInRange(strong).isEmpty());
    }

    @Test
    void rangeChecksAtTheBoardEdgeDoNotThrow() {
        place(board, h, 0, 0);
        place(board, strong, 7, 7);
        assertTrue(combat.getMonstersInRange(h).isEmpty());
        assertTrue(combat.getHeroesInRange(strong).isEmpty());
    }

    @Test
    void piecesOffTheBoardHaveNoTargets() {
        assertTrue(combat.getMonstersInRange(h).isEmpty());
        assertTrue(combat.getHeroesInRange(strong).isEmpty());
    }

    @Test
    void deadTargetsAreOutOfRange() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        strong.setHP(0);
        assertTrue(combat.getMonstersInRange(h).isEmpty());

        strong.setHP(10);
        h.setHP(0);
        assertTrue(combat.getHeroesInRange(strong).isEmpty());
    }

    @Test
    void heroAttackDealsRoundedDamageAndRecordsIt() {
        place(board, strong, 3, 3);

        assertFalse(combat.heroAttack(h, strong));

        assertEquals(303, strong.getHP(), EPS); // 500 - 700 * 0.31 / 1.1
        assertEquals(197, heroStats().getDamageDealt(), EPS);
        assertEquals(0, heroStats().getMonstersKilled());
    }

    @Test
    void heroAttackIncludesWeaponDamage() {
        h.equipWeapon(new Weapon("Sword", 1, 1, 100, 1));
        combat.heroAttack(h, strong);
        assertEquals(275, strong.getHP(), EPS); // 500 - 800 * 0.31 / 1.1
    }

    @Test
    void lethalHeroAttackCountsKillAndClearsTile() {
        Monster weak = monster("Weak");
        weak.setHP(10);
        place(board, weak, 3, 3);

        assertTrue(combat.heroAttack(h, weak));

        assertFalse(board.getTile(3, 3).hasMonster());
        assertEquals(1, heroStats().getMonstersKilled());
    }

    @Test
    void certainDodgePreventsHeroAttackDamage() {
        strong.setDodgeChance(1.0);
        assertFalse(combat.heroAttack(h, strong));
        assertEquals(500, strong.getHP(), EPS);
        assertEquals(0, heroStats().getDamageDealt(), EPS);
    }

    @Test
    void attackingADeadMonsterDoesNothing() {
        strong.setHP(0);
        assertFalse(combat.heroAttack(h, strong));
        assertEquals(0, heroStats().getDamageDealt(), EPS);
    }

    @Test
    void attacksWithNullArgumentsReturnFalse() {
        assertFalse(combat.heroAttack(null, strong));
        assertFalse(combat.heroAttack(h, null));
        assertFalse(combat.monsterAttack(null, h));
        assertFalse(combat.monsterAttack(strong, null));
    }

    @Test
    void monsterAttackScalesDamageAndRecordsIt() {
        h.setHP(200);
        assertFalse(combat.monsterAttack(strong, h));
        assertEquals(80, h.getHP(), EPS); // 200 - 400 * 0.3
        assertEquals(120, heroStats().getDamageTaken(), EPS);
    }

    @Test
    void armorReducesMonsterDamage() {
        h.setHP(200);
        h.equipArmor(new Armor("Plate", 1, 1, 20));
        combat.monsterAttack(strong, h);
        assertEquals(100, h.getHP(), EPS);
    }

    @Test
    void lethalMonsterAttackCountsFaintAndClearsTile() {
        place(board, h, 3, 3);
        assertTrue(combat.monsterAttack(strong, h));
        assertEquals(0, h.getHP(), EPS);
        assertFalse(board.getTile(3, 3).hasHero());
        assertEquals(1, heroStats().getTimesFainted());
    }

    @Test
    void faintingRevertsTheTerrainBonus() {
        board.getTile(3, 3).setType(ValorCellType.BUSH);
        place(board, h, 3, 3);
        board.getTile(3, 3).onEnter(h);
        assertEquals(660, h.getDexterity(), 1e-6);

        combat.monsterAttack(strong, h);

        assertEquals(600, h.getDexterity(), 1e-6);
    }

    @Test
    void attackingAFaintedHeroDoesNothing() {
        h.setHP(0);
        assertFalse(combat.monsterAttack(strong, h));
        assertEquals(0, heroStats().getTimesFainted());
    }

    @Test
    void spellDamageScalesWithDexterityAndSpendsMana() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);

        assertTrue(combat.heroCastSpell(h, spell(SpellType.ICE), strong));

        assertEquals(404, strong.getHP(), EPS); // 500 - (100 + 0.06 * 100) / 1.1
        assertEquals(30, h.getMP(), EPS);
        assertEquals(96, heroStats().getDamageDealt(), EPS);
    }

    @Test
    void fireSpellLowersDefenseByTenPercent() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        combat.heroCastSpell(h, spell(SpellType.FIRE), strong);
        assertEquals(90, strong.getDefense(), 1e-6);
        assertEquals(400, strong.getDamage(), EPS);
    }

    @Test
    void higherDefenseMeansLessAttackDamage() {
        Monster armored = new Dragon("Armored", 5, 400, 1000, 0.0);
        combat.heroAttack(h, armored);
        assertEquals(391.5, armored.getHP(), 0.5); // half of 217
    }

    @Test
    void zeroDefenseTakesFullDamage() {
        Monster soft = new Dragon("Soft", 5, 400, 0, 0.0);
        combat.heroAttack(h, soft);
        assertEquals(283, soft.getHP(), EPS);
    }

    @Test
    void fireDebuffAppliesToTheSpellThatCausedIt() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        combat.heroCastSpell(h, spell(SpellType.FIRE), strong);
        assertEquals(403, strong.getHP(), EPS); // 500 - 106 / 1.09: the debuff applies first
    }

    @Test
    void fireDebuffMakesLaterAttacksHitHarder() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        combat.heroAttack(h, strong);
        double firstHit = 500 - strong.getHP();

        strong.setHP(500);
        combat.heroCastSpell(h, spell(SpellType.FIRE), strong);
        strong.setHP(500);
        combat.heroAttack(h, strong);

        assertTrue(500 - strong.getHP() > firstHit);
    }

    @Test
    void iceSpellLowersDamageByTenPercent() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        combat.heroCastSpell(h, spell(SpellType.ICE), strong);
        assertEquals(360, strong.getDamage(), 1e-6);
        assertEquals(100, strong.getDefense(), EPS);
    }

    @Test
    void lightningSpellLowersDodgeByTenPercent() {
        strong.setDodgeChance(0.0);
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        combat.heroCastSpell(h, spell(SpellType.LIGHTNING), strong);
        assertEquals(0, strong.getDodgeChance(), EPS);
        assertEquals(100, strong.getDefense(), EPS);
    }

    @Test
    void dodgedSpellStillCostsManaButDealsNoDamage() {
        strong.setDodgeChance(1.0);
        place(board, h, 3, 3);
        place(board, strong, 2, 3);

        assertTrue(combat.heroCastSpell(h, spell(SpellType.FIRE), strong));

        assertEquals(500, strong.getHP(), EPS);
        assertEquals(30, h.getMP(), EPS);
        assertEquals(100, strong.getDefense(), EPS);
    }

    @Test
    void outOfRangeSpellFailsWithoutSpendingMana() {
        place(board, h, 6, 3);
        place(board, strong, 2, 3);
        assertFalse(combat.heroCastSpell(h, spell(SpellType.FIRE), strong));
        assertEquals(50, h.getMP(), EPS);
    }

    @Test
    void spellWithoutEnoughManaFails() {
        h.setMP(19);
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        assertFalse(combat.heroCastSpell(h, spell(SpellType.FIRE), strong));
        assertEquals(19, h.getMP(), EPS);
        assertEquals(500, strong.getHP(), EPS);
    }

    @Test
    void lethalSpellCountsKillAndClearsTile() {
        Monster weak = monster("Weak");
        weak.setHP(10);
        place(board, h, 3, 3);
        place(board, weak, 2, 3);

        assertTrue(combat.heroCastSpell(h, spell(SpellType.FIRE), weak));

        assertFalse(board.getTile(2, 3).hasMonster());
        assertEquals(1, heroStats().getMonstersKilled());
    }

    @Test
    void invalidSpellCastsFail() {
        place(board, h, 3, 3);
        place(board, strong, 2, 3);
        assertFalse(combat.heroCastSpell(null, spell(SpellType.FIRE), strong));
        assertFalse(combat.heroCastSpell(h, null, strong));
        assertFalse(combat.heroCastSpell(h, spell(SpellType.FIRE), null));

        h.setHP(0);
        assertFalse(combat.heroCastSpell(h, spell(SpellType.FIRE), strong));
    }

    @Test
    void combatWorksWithoutGameStats() {
        ValorCombat noStats = new ValorCombat(board, null);
        assertDoesNotThrow(() -> noStats.heroAttack(h, strong));
        assertDoesNotThrow(() -> noStats.monsterAttack(strong, h));
    }

    @Test
    void heroesMissingFromGameStatsAreStillCombatants() {
        Hero stranger = hero("Stranger");
        assertDoesNotThrow(() -> combat.heroAttack(stranger, strong));
        assertEquals(303, strong.getHP(), EPS);
    }
}
