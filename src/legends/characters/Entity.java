/**
 * File: Entity.java
 * Description: Base abstract class for any world entity (heroes or monsters).
 * Provides shared attributes such as name, level, and HP.
 * Part of: legends.characters package.
 */

package legends.characters;

/**
 * Entity is the base class for all characters in the game.
 * It stores shared fields such as name, level, and HP.
 */
public abstract class Entity {

    protected String name;   // Character's name
    protected int level;     // Character's level
    protected double hp;     // Current HP value

    // ---------- Setters ----------
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { this.level = level; }
    public void setHP(double hp) { this.hp = hp; }

    // ---------- Status Helpers ----------
    public boolean isAlive() { return hp > 0; }

    // ---------- Getters ----------
    public String getName() { return name; }
    public int getLevel() { return level; }
    public double getHP() { return hp; }
}