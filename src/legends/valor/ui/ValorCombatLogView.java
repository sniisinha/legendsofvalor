/**
 * File: ValorCombatLogView.java
 * Package: legends.valor.ui
 *
 * Purpose:
 *   Renders formatted combat log messages for Legends of Valor encounters.
 *
 * Responsibilities:
 *   - Display boxed combat events (attacks, spells, kills, fallen, respawn, info)
 *   - Group repeated dodge events to reduce log spam during a phase
 *   - Provide ANSI-safe padding so boxed layouts stay aligned with colored text
 *   - Flush any pending grouped log output at safe points in the turn flow
 */
package legends.valor.ui;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.items.Spell;

import java.util.regex.Pattern;

public class ValorCombatLogView {

    // ANSI escape codes used for styled combat output
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String DIM   = "\u001B[2m";
    private static final String RED   = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW= "\u001B[33m";
    private static final String BLUE  = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN  = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // Pattern used to strip ANSI codes for width/alignment calculations
    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    // Tracks pending dodge events so repeated dodges can be grouped into one box
    private String pendingDodgeName = null;
    private double pendingDodgeChance = 0.0;
    private int pendingDodgeCount = 0;

    /**
     * Flushes any pending grouped dodge log output.
     * Intended to be called before non-dodge events and at the end of phases.
     */
    public void flush() {
        if (pendingDodgeCount <= 0 || pendingDodgeName == null) return;

        printHeader("DODGE", YELLOW);

        String titleLine = " " + BOLD + pendingDodgeName + RESET + " dodged!";
        if (pendingDodgeCount > 1) {
            titleLine = " " + BOLD + pendingDodgeName + RESET + " dodged attacks!";
        }

        System.out.println(titleLine);
        printDetails("Result", YELLOW + "DODGE" + RESET);

        if (pendingDodgeCount > 1) {
            printDetails("Attempts dodged", String.valueOf(pendingDodgeCount));
        }

        int pct = (int) Math.round(pendingDodgeChance * 100.0);
        printDetails("Chance", pct + "%");

        printFooter();

        pendingDodgeName = null;
        pendingDodgeChance = 0.0;
        pendingDodgeCount = 0;
    }

    /**
     * Logs a hero basic attack event with damage and target HP change.
     */
    public void heroAttack(Hero hero, Monster target, int damage, double hpBefore, double hpAfter) {
        flush();
        printHeader("ATTACK", CYAN);
        System.out.println(" " + BOLD + hero.getName() + RESET + " attacks " + BOLD + target.getName() + RESET);
        printDetails("Result", GREEN + "HIT" + RESET);
        printDetails("Damage", YELLOW + String.valueOf(damage) + RESET);
        printDetails("Target HP", formatHP(hpBefore) + "  →  " + formatHP(hpAfter));
        printFooter();
    }

    /**
     * Logs a monster attack event with damage and hero HP change.
     */
    public void monsterAttack(Monster monster, Hero hero, int damage, double hpBefore, double hpAfter) {
        flush();
        printHeader("MONSTER ATTACK", RED);
        System.out.println(" " + BOLD + monster.getName() + RESET + " attacks " + BOLD + hero.getName() + RESET);
        printDetails("Result", GREEN + "HIT" + RESET);
        printDetails("Damage", YELLOW + String.valueOf(damage) + RESET);
        printDetails("Hero HP", formatHP(hpBefore) + "  →  " + formatHP(hpAfter));
        printFooter();
    }

    /**
     * Logs a hero spell cast event, including spell type and target HP change.
     */
    public void spellCast(Hero hero, Spell spell, Monster target, int damage, double hpBefore, double hpAfter) {
        flush();
        printHeader("SPELL", MAGENTA);
        System.out.println(" " + BOLD + hero.getName() + RESET + " casts " + MAGENTA + spell.getName() + RESET
                + " on " + BOLD + target.getName() + RESET);
        printDetails("Type", CYAN + String.valueOf(spell.getType()) + RESET);
        printDetails("Damage", YELLOW + String.valueOf(damage) + RESET);
        printDetails("Target HP", formatHP(hpBefore) + "  →  " + formatHP(hpAfter));
        printFooter();
    }

    /**
     * Records a dodge event and groups consecutive identical dodges for cleaner logs.
     */
    public void dodge(String dodgerName, double dodgeChance) {
        if (pendingDodgeName != null
                && pendingDodgeName.equals(dodgerName)
                && Math.abs(pendingDodgeChance - dodgeChance) < 0.000001) {
            pendingDodgeCount++;
            return;
        }

        flush();
        pendingDodgeName = dodgerName;
        pendingDodgeChance = dodgeChance;
        pendingDodgeCount = 1;
    }

    /**
     * Logs a kill confirmation message for a defeated monster.
     */
    public void slain(String name) {
        flush();
        printHeader("KILL", GREEN);
        System.out.println(" " + GREEN + BOLD + "✔ " + name + " has been slain!" + RESET);
        printFooter();
    }

    /**
     * Logs a hero fainting event and communicates end-of-round respawn behavior.
     */
    public void fallen(String name) {
        flush();
        printHeader("DOWN", RED);
        System.out.println(" " + RED + BOLD + "✖ " + name + " has fallen!" + RESET);
        printDetails("Next", CYAN + "Respawns at Nexus (end of round)" + RESET);
        printFooter();
    }

    /**
     * Logs a hero respawn event, including lane placement and restored stats.
     */
    public void respawned(String heroName, String laneName, int row, int col, int hp, int mp) {
        flush();
        printHeader("RESPAWN", BLUE);

        System.out.println(" " + BOLD + heroName + RESET + " returns to Nexus");
        printDetails("Lane", CYAN + laneName + RESET);
        printDetails("Position", "(" + row + "," + col + ")");
        printDetails("HP", GREEN + String.valueOf(hp) + RESET);
        printDetails("MP", GREEN + String.valueOf(mp) + RESET);

        printFooter();
    }

    /**
     * Logs a generic informational event in a consistent boxed format.
     */
    public void info(String title, String msg) {
        flush();
        printHeader(title, BLUE);
        System.out.println(" " + msg);
        printFooter();
    }

    /**
     * Prints the top portion of a boxed combat log message.
     */
    private void printHeader(String title, String color) {
        System.out.println();
        System.out.println(color + BOLD + "┌──────────────────────────────────────────────┐" + RESET);
        String line = " " + title + " ";
        System.out.println(color + "│" + RESET + padCenter(line, 46) + color + "│" + RESET);
        System.out.println(color + BOLD + "├──────────────────────────────────────────────┤" + RESET);
    }

    /**
     * Prints a key/value detail line aligned for boxed output.
     */
    private void printDetails(String k, String v) {
        String left = DIM + k + RESET + ": " + v;
        System.out.println(" " + padRight(left, 46));
    }

    /**
     * Prints the bottom portion of a boxed combat log message.
     */
    private void printFooter() {
        System.out.println(WHITE + BOLD + "└──────────────────────────────────────────────┘" + RESET);
    }

    /**
     * Formats HP values for display, clamping dead targets to zero.
     */
    private String formatHP(double hp) {
        return (hp <= 0) ? RED + "0" + RESET : String.valueOf((int) Math.round(hp));
    }

    /**
     * Pads a string to a width using printable length (ANSI-safe).
     */
    private String padRight(String s, int width) {
        s = safe(s);
        int printable = printableLength(s);
        if (printable >= width) return s;

        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < width - printable; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Centers a string within a width using printable length (ANSI-safe).
     */
    private String padCenter(String s, int width) {
        s = safe(s);
        int printable = printableLength(s);
        if (printable >= width) return trimPrintableToWidth(s, width);

        int total = width - printable;
        int left = total / 2;
        int right = total - left;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Returns a non-null string to avoid null handling scattered throughout formatting code.
     */
    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * Computes printable width by stripping ANSI sequences.
     */
    private int printableLength(String s) {
        return stripAnsi(safe(s)).length();
    }

    /**
     * Removes ANSI escape sequences for width calculations.
     */
    private String stripAnsi(String s) {
        return ANSI_PATTERN.matcher(safe(s)).replaceAll("");
    }

    /**
     * Trims a string to the requested printable width in a defensive way.
     * Used only when a centered header would overflow the box width.
     */
    private String trimPrintableToWidth(String s, int width) {
        if (printableLength(s) <= width) return s;

        String plain = stripAnsi(s);
        return (plain.length() <= width) ? plain : plain.substring(0, width);
    }
}
