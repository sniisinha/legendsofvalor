/**
 * File: ConsoleUI.java
 * Package: legends.ui
 *
 * Purpose:
 *   Provides reusable console-based UI utilities with ANSI-safe formatting.
 *
 * Responsibilities:
 *   - Centralize ANSI color codes and text styling
 *   - Render boxed sections and aligned tables
 *   - Handle ANSI-safe padding, centering, and length calculations
 *   - Provide helper methods for consistent console output
 */
package legends.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class ConsoleUI {

    // Utility class; prevent instantiation
    private ConsoleUI() {}

    // ANSI escape codes for text styling and colors
    public static final String RESET = "\u001B[0m";
    public static final String BOLD  = "\u001B[1m";
    public static final String DIM   = "\u001B[2m";

    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Precompiled pattern for stripping ANSI escape codes
    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    /**
     * Clears stray ANSI artifacts that may appear due to cursor control output.
     */
    public static void clearLineJunk() {
        System.out.print(RESET);
    }

    /**
     * Prints a styled section title.
     */
    public static void sectionTitle(String title) {
        System.out.println();
        System.out.println(MAGENTA + BOLD + "══ " + safe(title) + " ══" + RESET);
    }

    /**
     * Renders a boxed UI section with dynamic width.
     * Width calculation is ANSI-safe.
     */
    public static void boxed(String title, List<String> lines) {
        if (lines == null) lines = Collections.emptyList();
        title = safe(title);

        int w = Math.max(0, visibleLen(title));
        for (String s : lines) {
            w = Math.max(w, visibleLen(s));
        }

        // Account for padding and borders
        w += 2;

        String top = "┏" + repeat("━", w + 2) + "┓";
        String mid = "┣" + repeat("━", w + 2) + "┫";
        String bot = "┗" + repeat("━", w + 2) + "┛";

        System.out.println(top);
        System.out.println("┃ " + padRight(title, w) + " ┃");
        System.out.println(mid);

        for (String s : lines) {
            System.out.println("┃ " + padRight(safe(s), w) + " ┃");
        }

        System.out.println(bot);
    }

    /**
     * Pads text on the right to the specified width (ANSI-safe).
     */
    public static String padRight(String s, int width) {
        s = safe(s);
        int len = visibleLen(s);
        if (len >= width) return s;
        return s + repeat(" ", width - len);
    }

    /**
     * Pads text on the left to the specified width (ANSI-safe).
     */
    public static String padLeft(String s, int width) {
        s = safe(s);
        int len = visibleLen(s);
        if (len >= width) return s;
        return repeat(" ", width - len) + s;
    }

    /**
     * Pads text evenly on both sides to center it (ANSI-safe).
     */
    public static String padCenter(String s, int width) {
        s = safe(s);
        int len = visibleLen(s);
        if (len >= width) return s;

        int total = width - len;
        int left = total / 2;
        int right = total - left;

        return repeat(" ", left) + s + repeat(" ", right);
    }

    /**
     * Builds an aligned text table from rows of string arrays.
     * Handles null and ragged rows safely.
     */
    public static List<String> table(List<String[]> rows) {
        if (rows == null || rows.isEmpty()) return Collections.emptyList();

        int cols = maxCols(rows);
        if (cols <= 0) return Collections.emptyList();

        int[] widths = new int[cols];

        // Compute maximum width for each column
        for (String[] r : rows) {
            for (int c = 0; c < cols; c++) {
                String cell = (r != null && c < r.length) ? safe(r[c]) : "";
                widths[c] = Math.max(widths[c], visibleLen(cell));
            }
        }

        // Build formatted table rows
        List<String> out = new ArrayList<String>();
        for (String[] r : rows) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                String cell = (r != null && c < r.length) ? safe(r[c]) : "";
                sb.append(padRight(cell, widths[c]));
                if (c < cols - 1) sb.append("  ");
            }
            out.add(sb.toString());
        }
        return out;
    }

    /**
     * Returns a non-null string.
     */
    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * Returns the visible length of a string with ANSI codes removed.
     */
    private static int visibleLen(String s) {
        return stripAnsi(safe(s)).length();
    }

    /**
     * Removes ANSI escape codes from a string.
     */
    public static String stripAnsi(String s) {
        return ANSI_PATTERN.matcher(safe(s)).replaceAll("");
    }

    /**
     * Repeats a string a fixed number of times (Java 8 compatible).
     */
    public static String repeat(String s, int count) {
        if (s == null) s = "";
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    /**
     * Returns the maximum number of columns across all rows.
     */
    private static int maxCols(List<String[]> rows) {
        int max = 0;
        for (String[] r : rows) {
            if (r != null && r.length > max) max = r.length;
        }
        return max;
    }
}
