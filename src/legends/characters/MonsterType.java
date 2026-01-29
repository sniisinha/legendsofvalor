/**
 * File: MonsterType.java
 * Description: Defines the different categories of monsters in the game.
 * Used by the DataLoader and MonsterFactory to create the correct monster type.
 */

package legends.characters;

public enum MonsterType {
    DRAGON,        // Monster with high base damage
    SPIRIT,        // Monster with strong dodge chance
    EXOSKELETON    // Monster with strong defense
}