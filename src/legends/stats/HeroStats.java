/**
 * File: HeroStats.java
 * Package: legends.stats
 *
 * Purpose:
 *   Tracks performance statistics for a single hero during a game session.
 *
 * Responsibilities:
 *   - Record combat and progression metrics for a hero
 *   - Accumulate per-hero statistics over the course of a game
 *   - Provide read-only access to collected hero data
 */
package legends.stats;

import legends.characters.Hero;

public class HeroStats {

    // Reference to the hero whose statistics are being tracked
    private final Hero hero;

    // Combat outcome counters
    private int monstersKilled = 0;
    private int timesFainted   = 0;

    // Damage metrics accumulated during gameplay
    private double damageDealt = 0;
    private double damageTaken = 0;

    // Resource gains accumulated by the hero
    private int goldGained = 0;
    private int xpGained   = 0;

    /**
     * Creates a statistics tracker for the given hero.
     */
    public HeroStats(Hero hero) {
        this.hero = hero;
    }

    /**
     * Returns the hero associated with this statistics record.
     */
    public Hero getHero() { return hero; }

    /**
     * Returns the hero's name, or a fallback if unavailable.
     */
    public String getHeroName() {
        return (hero == null) ? "Unknown" : hero.getName();
    }

    /**
     * Returns the hero's level if available.
     * Falls back to 0 if the hero does not expose a level accessor.
     */
    public int getHeroLevel() {
        try {
            return hero.getLevel();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Increments the number of monsters defeated by the hero.
     */
    public void addKill() { monstersKilled++; }

    /**
     * Increments the number of times the hero has fainted.
     */
    public void addFaint() { timesFainted++; }

    /**
     * Accumulates damage dealt by the hero.
     */
    public void addDamageDealt(double amount) { damageDealt += amount; }

    /**
     * Accumulates damage taken by the hero.
     */
    public void addDamageTaken(double amount) { damageTaken += amount; }

    /**
     * Accumulates gold gained by the hero.
     */
    public void addGoldGained(int amount) { goldGained += amount; }

    /**
     * Accumulates experience points gained by the hero.
     */
    public void addXpGained(int amount) { xpGained += amount; }

    /**
     * Returns the number of monsters killed by the hero.
     */
    public int getMonstersKilled() { return monstersKilled; }

    /**
     * Returns the number of times the hero has fainted.
     */
    public int getTimesFainted() { return timesFainted; }

    /**
     * Returns the total damage dealt by the hero.
     */
    public double getDamageDealt() { return damageDealt; }

    /**
     * Returns the total damage taken by the hero.
     */
    public double getDamageTaken() { return damageTaken; }

    /**
     * Returns the total gold gained by the hero.
     */
    public int getGoldGained() { return goldGained; }

    /**
     * Returns the total experience gained by the hero.
     */
    public int getXpGained() { return xpGained; }
}
