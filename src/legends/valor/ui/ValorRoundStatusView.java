/**
 * File: ValorRoundStatusView.java
 * Package: legends.valor.ui
 *
 * Purpose:
 *   Displays a per-round status snapshot of the Valor board.
 *
 * Responsibilities:
 *   - Print a round header and overall counts of heroes and monsters on the board
 *   - Render a formatted table of heroes with position, lane, and HP/MP status
 *   - Render a formatted table of monsters with position, lane, and HP status
 *   - Provide ANSI-safe formatting helpers to keep tables aligned with colored text
 */
package legends.valor.ui;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorTile;

public class ValorRoundStatusView {

    // ANSI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String DIM   = "\u001B[2m";
    private static final String RED   = "\u001B[31m";
    private static final String CYAN  = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String GREEN = "\u001B[32m";

    // Fixed column widths used by the status tables
    private static final int W_NAME = 20;
    private static final int W_LV   = 4;
    private static final int W_POS  = 9;
    private static final int W_HP   = 12;
    private static final int W_MP   = 10;
    private static final int W_LANE = 6;

    // Total width for headers and separator lines
    private static final int TABLE_WIDTH = 86;

    // Precomputed line separators to keep output consistent
    private static final String LINE = repeat("=", TABLE_WIDTH);
    private static final String DASH = repeat("-", TABLE_WIDTH);

    /**
     * Prints a formatted snapshot of the current round status, including
     * all heroes and monsters currently placed on the board.
     */
    public void printRoundStatus(ValorBoard board, int round) {
        if (board == null) return;

        int heroCount = countHeroes(board);
        int monsterCount = countMonsters(board);

        System.out.println();
        System.out.println(WHITE + BOLD + LINE + RESET);
        System.out.println(center("ROUND " + round + " STATUS", TABLE_WIDTH));
        System.out.println(WHITE + BOLD + LINE + RESET);

        // Hero table section (position, lane, HP/MP)
        System.out.println();
        System.out.println(CYAN + BOLD + "HEROES ON BOARD" + RESET + " " + DIM + "(" + heroCount + ")" + RESET);
        System.out.println(CYAN + DASH + RESET);

        String heroHeader =
                padCell(BOLD + "Name" + RESET, W_NAME) + " " +
                padCell(BOLD + "Lv"   + RESET, W_LV)   + " " +
                padCell(BOLD + "Pos"  + RESET, W_POS)  + " " +
                padCell(BOLD + "HP"   + RESET, W_HP)   + " " +
                padCell(BOLD + "MP"   + RESET, W_MP)   + " " +
                padCell(BOLD + "Lane" + RESET, W_LANE);

        System.out.println(heroHeader);
        System.out.println(DIM + DASH + RESET);

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile == null) continue;

                Hero h = tile.getHero();
                if (h == null) continue;

                String name = trimTo(h.getName(), W_NAME);
                String lv = "L" + h.getLevel();
                String pos = "(" + r + "," + c + ")";

                String hp = coloredHP((int) Math.round(h.getHP())) + "/" + (h.getLevel() * 100);
                String mp = (int) Math.round(h.getMP()) + "/" + (h.getLevel() * 50);
                String lane = laneName(board.getLane(c));

                String row =
                        padCell(name, W_NAME) + " " +
                        padCell(lv,   W_LV)   + " " +
                        padCell(pos,  W_POS)  + " " +
                        padCell(hp,   W_HP)   + " " +
                        padCell(mp,   W_MP)   + " " +
                        padCell(lane, W_LANE);

                System.out.println(row);
            }
        }

        // Monster table section (position, lane, HP)
        System.out.println();
        System.out.println(RED + BOLD + "MONSTERS ON BOARD" + RESET + " " + DIM + "(" + monsterCount + ")" + RESET);
        System.out.println(RED + DASH + RESET);

        String monsterHeader =
                padCell(BOLD + "Name" + RESET, W_NAME) + " " +
                padCell(BOLD + "Lv"   + RESET, W_LV)   + " " +
                padCell(BOLD + "Pos"  + RESET, W_POS)  + " " +
                padCell(BOLD + "HP"   + RESET, 10)     + " " +
                padCell(BOLD + "Lane" + RESET, W_LANE);

        System.out.println(monsterHeader);
        System.out.println(DIM + DASH + RESET);

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile == null) continue;

                Monster m = tile.getMonster();
                if (m == null) continue;

                String name = trimTo(m.getName(), W_NAME);
                String lv = "L" + m.getLevel();
                String pos = "(" + r + "," + c + ")";
                String hp = String.valueOf((int) Math.round(m.getHP()));
                String lane = laneName(board.getLane(c));

                String row =
                        padCell(name, W_NAME) + " " +
                        padCell(lv,   W_LV)   + " " +
                        padCell(pos,  W_POS)  + " " +
                        padCell(hp,   10)     + " " +
                        padCell(lane, W_LANE);

                System.out.println(row);
            }
        }

        System.out.println(WHITE + BOLD + LINE + RESET);
        System.out.println();
    }

    /**
     * Counts how many heroes are currently placed on the board.
     */
    private int countHeroes(ValorBoard board) {
        int cnt = 0;
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile t = board.getTile(r, c);
                if (t != null && t.getHero() != null) cnt++;
            }
        }
        return cnt;
    }

    /**
     * Counts how many monsters are currently placed on the board.
     */
    private int countMonsters(ValorBoard board) {
        int cnt = 0;
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile t = board.getTile(r, c);
                if (t != null && t.getMonster() != null) cnt++;
            }
        }
        return cnt;
    }

    /**
     * Converts a lane index into a short label for the table.
     */
    private static String laneName(int lane) {
        switch (lane) {
            case 0: return "TOP";
            case 1: return "MID";
            case 2: return "BOT";
            default: return "-";
        }
    }

    /**
     * Applies a simple color style to HP values for readability.
     */
    private static String coloredHP(int hp) {
        if (hp <= 0) return RED + "0" + RESET;
        return GREEN + hp + RESET;
    }

    /**
     * Trims a string to the given maximum length for fixed-width columns.
     */
    private static String trimTo(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }

    /**
     * Centers a string within the given width for section headers.
     */
    private static String center(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Repeats a string a fixed number of times (Java 8 compatible).
     */
    private static String repeat(String s, int count) {
        if (s == null) s = "";
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    /**
     * Pads a table cell to a fixed width using ANSI-safe printable length.
     */
    private static String padCell(String s, int width) {
        if (s == null) s = "";
        int printable = printableLength(s);
        if (printable >= width) return s;

        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < width - printable; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Computes the printable length by stripping ANSI sequences.
     */
    private static int printableLength(String s) {
        return stripAnsi(s).length();
    }

    /**
     * Removes ANSI color codes so alignment math uses visible characters only.
     */
    private static String stripAnsi(String s) {
        return (s == null) ? "" : s.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
