/**
 * File: ValorCombat.java
 * Package: legends.valor.combat
 *
 * Purpose:
 *   Implements core combat mechanics for Legends of Valor encounters.
 *
 * Responsibilities:
 *   - Determine valid targets within attack/cast range on the Valor board
 *   - Execute hero and monster combat actions (attacks and spell casts)
 *   - Apply damage, dodges, and spell debuffs using game rules
 *   - Update per-hero statistics and emit combat log output
 */
package legends.valor.combat;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Spell;
import legends.items.SpellType;
import legends.stats.GameStats;
import legends.stats.HeroStats;
import legends.valor.ui.ValorCombatLogView;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorTile;

import java.util.ArrayList;
import java.util.List;

public class ValorCombat {

    // Board provides spatial context for range checks and piece removal
    private final ValorBoard board;

    // GameStats tracks performance metrics used for summaries/leaderboards
    private final GameStats gameStats;

    // Combat log view renders turn-by-turn messages (buffered where needed)
    private final ValorCombatLogView log = new ValorCombatLogView();

    // Percent reduction applied by debuff spells (keeps current 10% behavior)
    private static final double DEBUFF_PCT = 0.10;

    // Monster damage scaling for Valor combat
    private static final double MONSTER_DAMAGE_FACTOR = 0.30;

    // Dexterity scaling factor for spell damage calculations
    private static final double DEX_SPELL_SCALE_DIVISOR = 10000.0;

    // Range model: current tile + 4-neighborhood (orthogonal adjacency)
    private static final int[][] NEIGHBOR_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    public ValorCombat(ValorBoard board, GameStats gameStats) {
        this.board = board;
        this.gameStats = gameStats;
    }

    /**
     * Flushes any buffered combat log output.
     * Intended to be called at the end of a phase so grouped events print.
     */
    public void flushLogs() {
        log.flush();
    }

    /**
     * Returns a list of living monsters within range of the given hero.
     * Range is defined by NEIGHBOR_OFFSETS around the hero's board position.
     */
    public List<Monster> getMonstersInRange(Hero hero) {
        List<Monster> result = new ArrayList<Monster>();

        int[] pos = findHero(hero);
        if (pos == null) return result;

        int r0 = pos[0];
        int c0 = pos[1];

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
            int[] d = NEIGHBOR_OFFSETS[i];
            int r = r0 + d[0];
            int c = c0 + d[1];
            if (!board.inBounds(r, c)) continue;

            ValorTile tile = board.getTile(r, c);
            Monster m = tile.getMonster();
            if (m != null && m.getHP() > 0 && !result.contains(m)) {
                result.add(m);
            }
        }

        return result;
    }

    /**
     * Returns a list of living heroes within range of the given monster.
     * Range is defined by NEIGHBOR_OFFSETS around the monster's board position.
     */
    public List<Hero> getHeroesInRange(Monster monster) {
        List<Hero> result = new ArrayList<Hero>();

        int[] pos = findMonster(monster);
        if (pos == null) return result;

        int r0 = pos[0];
        int c0 = pos[1];

        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
            int[] d = NEIGHBOR_OFFSETS[i];
            int r = r0 + d[0];
            int c = c0 + d[1];
            if (!board.inBounds(r, c)) continue;

            ValorTile tile = board.getTile(r, c);
            Hero h = tile.getHero();
            if (h != null && h.getHP() > 0 && !result.contains(h)) {
                result.add(h);
            }
        }

        return result;
    }

    /**
     * Performs a hero basic attack against a target monster.
     * Returns true if the attack defeats the monster.
     */
    public boolean heroAttack(Hero hero, Monster monster) {
        if (hero == null || monster == null) return false;

        if (monster.getHP() <= 0) {
            log.info("ATTACK", monster.getName() + " is already defeated.");
            return false;
        }

        // Dodge check occurs before damage is applied
        if (rollDodge(monster.getDodgeChance())) {
            log.dodge(monster.getName(), monster.getDodgeChance());
            return false;
        }

        double before = monster.getHP();
        int dmg = calculateHeroAttackDamage(hero);

        // Track contribution to damage dealt for post-game stats
        HeroStats hs = safeHeroStats(hero);
        if (hs != null) hs.addDamageDealt(dmg);

        monster.takeDamage(dmg);
        double after = monster.getHP();

        log.heroAttack(hero, monster, dmg, before, after);

        // On defeat, update stats and remove from the board
        if (monster.getHP() <= 0) {
            if (hs != null) hs.addKill();
            log.slain(monster.getName());
            removeMonsterFromBoard(monster);
            return true;
        }

        return false;
    }

    /**
     * Performs a monster basic attack against a target hero.
     * Returns true if the attack causes the hero to faint.
     */
    public boolean monsterAttack(Monster monster, Hero hero) {
        if (hero == null || monster == null) return false;
        if (hero.getHP() <= 0) return false;

        // Dodge check occurs before damage is applied
        if (rollDodge(hero.getDodgeChance())) {
            log.dodge(hero.getName(), hero.getDodgeChance());
            return false;
        }

        double before = hero.getHP();
        int dmg = calculateMonsterAttackDamage(monster);

        // Track damage taken for post-game stats
        HeroStats hs = safeHeroStats(hero);
        if (hs != null) hs.addDamageTaken(dmg);

        hero.takeDamage(dmg);
        double after = hero.getHP();

        log.monsterAttack(monster, hero, dmg, before, after);

        // On faint, update stats and remove from the board
        if (hero.getHP() <= 0) {
            if (hs != null) hs.addFaint();
            log.fallen(hero.getName());
            removeHeroFromBoard(hero);
            return true;
        }

        return false;
    }

    /**
     * Performs a hero spell cast on a target monster.
     * Returns true if the action is consumed (including a dodged spell).
     */
    public boolean heroCastSpell(Hero hero, Spell spell, Monster target) {
        if (hero == null || spell == null || target == null) return false;
        if (hero.getHP() <= 0 || target.getHP() <= 0) return false;

        if (!isTargetInRange(hero, target)) {
            log.info("SPELL", "Target is not in range.");
            return false;
        }

        if (!hero.canCast(spell)) {
            log.info("SPELL", "Not enough mana!");
            return false;
        }

        // Mana is spent once casting is committed
        hero.spendMana(spell.getManaCost());

        // Dodge preserves current behavior: spell attempt consumes the action
        if (rollDodge(target.getDodgeChance())) {
            log.dodge(target.getName(), target.getDodgeChance());
            return true;
        }

        double before = target.getHP();
        int dmg = calculateSpellDamage(hero, spell);

        // Apply spell-specific debuff effect before damage is logged/applied
        applySpellEffect(spell, target);

        HeroStats hs = safeHeroStats(hero);
        if (hs != null) hs.addDamageDealt(dmg);

        target.takeDamage(dmg);
        double after = target.getHP();

        log.spellCast(hero, spell, target, dmg, before, after);

        if (target.getHP() <= 0) {
            if (hs != null) hs.addKill();
            log.slain(target.getName());
            removeMonsterFromBoard(target);
        }

        return true;
    }

    /**
     * Returns true if the specified monster is within the hero's action range.
     */
    private boolean isTargetInRange(Hero hero, Monster target) {
        List<Monster> inRange = getMonstersInRange(hero);
        return inRange.contains(target);
    }

    /**
     * Resolves a dodge outcome based on the provided probability.
     */
    private boolean rollDodge(double dodgeChance) {
        return Math.random() < dodgeChance;
    }

    /**
     * Computes hero basic-attack damage using the hero's attack value.
     */
    private int calculateHeroAttackDamage(Hero hero) {
        return (int) Math.round(hero.getAttackDamage());
    }

    /**
     * Computes monster basic-attack damage using the configured scaling factor.
     */
    private int calculateMonsterAttackDamage(Monster monster) {
        return (int) Math.round(monster.getDamage() * MONSTER_DAMAGE_FACTOR);
    }

    /**
     * Computes spell damage using base spell damage and dexterity scaling.
     */
    private int calculateSpellDamage(Hero hero, Spell spell) {
        double base = spell.getDamage();
        double raw = base + (hero.getDexterity() / DEX_SPELL_SCALE_DIVISOR) * base;
        return (int) Math.round(raw);
    }

    /**
     * Applies a spell's debuff effect to the target based on spell type.
     */
    private void applySpellEffect(Spell spell, Monster target) {
        if (spell == null || target == null) return;

        SpellType type = spell.getType();
        if (type == null) return;

        switch (type) {
            case FIRE: {
                double before = target.getDefense();
                target.setDefense(applyPercentReduction(before, DEBUFF_PCT));
                log.info("DEBUFF", target.getName() + " DEF reduced by 10% (Fire)");
                break;
            }
            case ICE: {
                double before = target.getDamage();
                target.setDamage(applyPercentReduction(before, DEBUFF_PCT));
                log.info("DEBUFF", target.getName() + " DMG reduced by 10% (Ice)");
                break;
            }
            case LIGHTNING: {
                double before = target.getDodgeChance();
                target.setDodgeChance(applyPercentReduction(before, DEBUFF_PCT));
                log.info("DEBUFF", target.getName() + " Dodge reduced by 10% (Lightning)");
                break;
            }
            default:
                // no-op (future-proof if new types get added)
        }
    }

    /**
     * Applies a percentage reduction and clamps at zero to avoid negative stats.
     */
    private double applyPercentReduction(double value, double pct) {
        return Math.max(0, value - value * pct);
    }

    /**
     * Safely retrieves HeroStats for the given hero from the session GameStats.
     */
    private HeroStats safeHeroStats(Hero hero) {
        if (gameStats == null || hero == null) return null;
        try {
            return gameStats.statsFor(hero);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Locates the hero on the board and returns {row, col}, or null if not found.
     */
    private int[] findHero(Hero hero) {
        if (hero == null) return null;
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile.getHero() == hero) return new int[]{r, c};
            }
        }
        return null;
    }

    /**
     * Locates the monster on the board and returns {row, col}, or null if not found.
     */
    private int[] findMonster(Monster monster) {
        if (monster == null) return null;
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile.getMonster() == monster) return new int[]{r, c};
            }
        }
        return null;
    }

    /**
     * Removes the specified hero from its current board tile.
     */
    private void removeHeroFromBoard(Hero hero) {
        int[] pos = findHero(hero);
        if (pos == null) return;
        board.getTile(pos[0], pos[1]).removeHero();
    }

    /**
     * Removes the specified monster from its current board tile.
     */
    private void removeMonsterFromBoard(Monster monster) {
        int[] pos = findMonster(monster);
        if (pos == null) return;
        board.getTile(pos[0], pos[1]).removeMonster();
    }
}
