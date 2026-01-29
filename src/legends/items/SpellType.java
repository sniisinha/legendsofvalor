/**
 * SpellType.java
 * Represents the different elemental types of spells available in the game.
 * <p>
 * Each spell type applies a different secondary effect when cast:
 * <ul>
 *   <li><b>FIRE</b> – reduces the target's defense</li>
 *   <li><b>ICE</b> – reduces the target's damage output</li>
 *   <li><b>LIGHTNING</b> – reduces the target's dodge chance</li>
 * </ul>
 * This enum is used by the {@link Spell} class to determine which effect
 * should be applied when the spell is cast.
 */

package legends.items;

public enum SpellType {
    FIRE,
    ICE,
    LIGHTNING
}