/**
 * File: HeroTurnMenuView.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Renders the hero turn menu UI for Legends of Valor in the console.
 *
 * Responsibilities:
 *   - Display the active hero's turn header and current position/lane
 *   - Show hero status values (level, HP/MP, gold) in a readable format
 *   - Present available command keys and actions for the current turn
 *   - Provide small formatting helpers for consistent console alignment
 */
package legends.valor.turn;

import legends.characters.Hero;

public class HeroTurnMenuView {

    // ANSI color codes for menu styling
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String CYAN  = "\u001B[36m";
    private static final String YELLOW= "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String WHITE = "\u001B[37m";

    /**
     * Prints the formatted turn menu for a specific hero.
     */
    public void renderTurnMenu(int heroNumber, Hero hero, int[] pos, int lane) {
        System.out.println();
        System.out.println(CYAN + BOLD + "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓" + RESET);

        // Lane/position context helps the player plan movement and actions
        String laneName = laneName(lane);
        String where = (pos == null) ? "(?,?)" : "(" + pos[0] + "," + pos[1] + ")";
        String title = " HERO " + heroNumber + " TURN ";
        String nameLine = hero.getName() + "  " + WHITE + where + RESET + "  " + YELLOW + laneName + RESET;

        System.out.println(CYAN + "┃" + RESET + padCenter(title, 46) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫" + RESET);

        // Display key hero stats using project max HP/MP conventions
        double maxHP = hero.getLevel() * 100.0;
        double maxMP = hero.getLevel() * 50.0;
        String stats = "Lv " + hero.getLevel()
                + " | HP " + (int) hero.getHP() + "/" + (int) maxHP
                + " | MP " + (int) hero.getMP() + "/" + (int) maxMP
                + " | Gold " + hero.getGold();

        System.out.println(CYAN + "┃ " + RESET + padRight(nameLine, 63) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + padRight(stats, 45) + CYAN + "┃" + RESET);

        System.out.println(CYAN + "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫" + RESET);

        // Command legend shown in a compact two-column style
        System.out.println(CYAN + "┃ " + RESET + formatKey("W/A/S/D", "Move") + "   " + formatKey("F", "Attack") + padRight("", 18) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("C", "Cast Spell") + " " + formatKey("P", "Use Potion") + padRight("", 16) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("E", "Equip") + "     " + formatKey("T", "Teleport") + padRight("", 19) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("R", "Recall") + "    " + formatKey("O", "Remove Obstacle") + padRight("", 12) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("M", "Market") + "    " + formatKey("I", "Inventory") + padRight("", 18) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("Z", "Status") + "    " + GREEN + RESET + padRight("", 6) + padRight("", 25) + CYAN + "┃" + RESET);
        System.out.println(CYAN + "┃ " + RESET + formatKey("N", "Wait") + "      " + formatKey("Q", "Quit") + padRight("", 23) + CYAN + "┃" + RESET);

        System.out.println(CYAN + BOLD + "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛" + RESET);
    }

    /**
     * Converts a lane index into a human-readable label.
     */
    private String laneName(int lane) {
        switch (lane) {
            case 0: return "TOP LANE";
            case 1: return "MID LANE";
            case 2: return "BOT LANE";
            default: return "UNKNOWN";
        }
    }

    /**
     * Formats a command key label consistently for the menu legend.
     */
    private String formatKey(String key, String action) {
        return MAGENTA + "[" + key + "]" + RESET + " " + action;
    }

    /**
     * Pads a string on the right to a fixed width for alignment.
     */
    private String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    /**
     * Centers a string within a fixed width for menu titles.
     */
    private String padCenter(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }
}