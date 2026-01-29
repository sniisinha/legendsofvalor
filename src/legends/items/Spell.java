/**
 * Spell.java
 * Represents a magic spell item that heroes can purchase and use.
 * A spell has a price, required level, base damage, mana cost,
 * and a specific spell type (Fire, Ice, Lightning).
 *
 * Spells follow the PDF damage formula:
 *   finalDamage = baseDamage + (heroDexterity / 10000) * baseDamage
 *
 * Spells may also apply secondary effects depending on their SpellType.
 */

package legends.items;

public class Spell implements Item {

    private String name;
    private int price;
    private int requiredLevel;
    private double damage;
    private double manaCost;
    private SpellType type;

    /**
     * Creates a new Spell.
     *
     * @param name           the spell's name
     * @param price          the gold cost to buy this spell
     * @param requiredLevel  the minimum level needed to use this spell
     * @param damage         the base damage this spell deals before modifiers
     * @param manaCost       how much mana the hero must spend to cast it
     * @param type           the spell's elemental type (FIRE, ICE, LIGHTNING)
     */
    public Spell(String name, int price, int requiredLevel, double damage,
                 double manaCost, SpellType type) {
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    /** @return the spell's name */
    public String getName() { return name; }

    /** @return the gold price of the spell */
    public int getPrice() { return price; }

    /** @return the level required to equip/use this spell */
    public int getRequiredLevel() { return requiredLevel; }

    /** @return the spell’s base damage before dexterity scaling */
    public double getDamage() { return damage; }

    /** @return mana required to cast this spell */
    public double getManaCost() { return manaCost; }

    /** @return the elemental category of this spell */
    public SpellType getType() { return type; }
}