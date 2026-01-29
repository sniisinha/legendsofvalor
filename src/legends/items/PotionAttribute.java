/**
 * PotionAttribute.java
 * Represents the different attributes that a potion can affect.
 * These attributes determine what stat will be increased when the potion is used.
 */

package legends.items;

public enum PotionAttribute {

    /**
     * Restores or increases the hero's health points (HP).
     */
    HEALTH,

    /**
     * Restores or increases the hero's mana points (MP).
     */
    MANA,

    /**
     * Increases the hero's physical strength.
     * This may improve attack damage depending on hero type.
     */
    STRENGTH,

    /**
     * Increases the hero's dexterity.
     * This impacts things such as spell damage and accuracy.
     */
    DEXTERITY,

    /**
     * Increases the hero's agility.
     * Agility can influence dodge chance or similar stats.
     */
    AGILITY
}