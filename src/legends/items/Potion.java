/**
 * Potion.java
 * Represents a consumable potion item.
 * A potion provides a fixed effect amount and applies one or more
 * stat-based attributes (e.g., Health, Mana, Strength boosts).
 *
 * Each potion also has:
 *  - a name
 *  - a purchase price
 *  - a required level to use
 *  - a list of attributes indicating what stats it affects
 */

package legends.items;
import java.util.List;

public class Potion implements Item {

    private String name;
    private int price;
    private int requiredLevel;
    private int effectAmount;
    private List<PotionAttribute> attributes;

    /**
     * Creates a new Potion with the specified properties.
     *
     * @param name          potion name
     * @param price         gold cost to purchase
     * @param level         minimum hero level required to use it
     * @param effectAmount  the amount of stat increase the potion provides
     * @param attributes    list of attributes that the potion affects
     */
    public Potion(String name, int price, int level,
                  int effectAmount, List<PotionAttribute> attributes) {
        this.name = name;
        this.price = price;
        this.requiredLevel = level;
        this.effectAmount = effectAmount;
        this.attributes = attributes;
    }

    /** @return the potion name */
    @Override
    public String getName() { return name; }

    /** @return the potion purchase price */
    @Override
    public int getPrice() { return price; }

    /** @return the minimum required hero level */
    @Override
    public int getRequiredLevel() { return requiredLevel; }

    /** @return how strong the potion's effect is */
    public int getEffectAmount() { return effectAmount; }

    /** @return list of attributes modified by this potion */
    public List<PotionAttribute> getAttributes() { return attributes; }
}