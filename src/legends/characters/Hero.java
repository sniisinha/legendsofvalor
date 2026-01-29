/**
 * File: Hero.java
 * Description: Abstract parent class for all hero types (Warrior, Sorcerer, Paladin).
 * Contains shared attributes, inventory management, battle behavior, and leveling logic.
 * Every hero in the game inherits from this class.
 */

package legends.characters;

import legends.items.*;
import java.util.*;

public abstract class Hero extends Entity {

    // Core hero attributes
    protected double mp;
    protected double strength;
    protected double dexterity;
    protected double agility;

    // Currency and XP tracking
    protected int gold;
    protected int experience;

    // Equipped items
    protected Weapon weapon;
    protected Armor armor;

    // Each hero has their own inventory
    protected Inventory inventory;

    public Hero() {
        this.inventory = new Inventory();
        this.gold = 100;          // all heroes start with 100 gold
        this.experience = 0;      // fresh hero has no XP
    }

    // Getters for hero stats and equipment
    public double getMP() { return mp; }
    public double getStrength() { return strength; }
    public double getDexterity() { return dexterity; }
    public double getAgility() { return agility; }
    public int getGold() { return gold; }
    public int getExperience() { return experience; }

    public Inventory getInventory() { return inventory; }
    public Weapon getWeapon() { return weapon; }
    public Armor getArmor() { return armor; }

    // Setters for stats and progression
    public void setMP(double mp) { this.mp = mp; }
    public void setStrength(double strength) { this.strength = strength; }
    public void setAgility(double agility) { this.agility = agility; }
    public void setDexterity(double dexterity) { this.dexterity = dexterity; }
    public void setGold(int gold) { this.gold = gold; }
    public void setExperience(int exp) { this.experience = exp; }

    // Checks if hero can afford a given item in the market
    public boolean canAfford(Item item) {
        return gold >= item.getPrice();
    }

    // Spend or earn gold
    public void spendGold(int amount) { gold -= amount; }
    public void earnGold(int amount) { gold += amount; }

    // Add purchased / obtained items to inventory
    public void addItem(Item item) {
        inventory.addItem(item);
    }

    // Equipping a weapon updates the currently selected weapon
    public void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
        System.out.println(name + " equipped weapon: " + weapon.getName());
    }

    // Equipping armor replaces any previously equipped armor
    public void equipArmor(Armor armor) {
        this.armor = armor;
        System.out.println(name + " equipped armor: " + armor.getName());
    }

    // Calculates total attack damage using strength and weapon damage
    public double getAttackDamage() {
        double weaponDamage = (weapon != null) ? weapon.getDamage() : 0;
        return (strength + weaponDamage) * 0.31;
    }

    // Calculates hero dodge chance using agility (capped at 50%)
    public double getDodgeChance() {
        return Math.min(0.5, agility * 0.002);
    }

    // Hero taking damage (reduced if armor is equipped)
    public void takeDamage(double amount) {
        if (armor != null) {
            amount = Math.max(0, amount - armor.getReduction());
        }
        hp = Math.max(0, hp - amount);
    }

    // A hero is alive if HP > 0
    public boolean isAlive() {
        return hp > 0;
    }

    // Add XP and level up if XP threshold reached
    public void addExperience(int xp) {
        this.experience += xp;
        int requiredXP = level * 10; 

        while (experience >= requiredXP) {
            experience -= requiredXP;
            levelUp();               // subclasses implement actual stat scaling
            requiredXP = level * 10;
        }
    }

    // Each subclass must implement their own leveling bonuses
    public abstract void levelUp();

    // Potion use applies boosts to relevant stats
    public void usePotion(Potion p) {
        for (PotionAttribute attr : p.getAttributes()) {
            switch (attr) {
                case HEALTH:    this.hp       += p.getEffectAmount(); break;
                case MANA:      this.mp       += p.getEffectAmount(); break;
                case STRENGTH:  this.strength += p.getEffectAmount(); break;
                case AGILITY:   this.agility  += p.getEffectAmount(); break;
                case DEXTERITY: this.dexterity+= p.getEffectAmount(); break;
            }
        }
        System.out.println("\u001B[92m" + getName() + " feels stronger!\u001B[0m");
    }

    // String summary for debugging / displays
    @Override
    public String toString() {
        return getName()
                + " (Lvl " + getLevel() + ") "
                + "[HP: " + (int)getHP()
                + ", MP: " + (int)mp
                + ", STR: " + (int)strength
                + ", DEX: " + (int)dexterity
                + ", AGI: " + (int)agility
                + ", Gold: " + gold + "]";
    }

    public boolean canCast(Spell spell) { return spell != null && mp >= spell.getManaCost(); }
    public void spendMana(double amount) { mp = Math.max(0, mp - amount); }

}