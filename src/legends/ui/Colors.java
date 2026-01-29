/*
 * Colors.java:
 *
 * Utility class containing ANSI color codes used throughout the game.
 * <p>
 * These constants allow the game to print colored or styled text in the console.
 * They are used for highlighting messages, warnings, headers, and UI elements.
 * <p>
 * Note: ANSI colors may not render in some Windows terminals without ANSI support.
 */
package legends.ui;

public class Colors {

    /** Resets all formatting back to normal text. */
    public static final String RESET = "\u001B[0m";

    /** Red foreground color. */
    public static final String RED = "\u001B[31m";

    /** Green foreground color. */
    public static final String GREEN = "\u001B[32m";

    /** Yellow foreground color. */
    public static final String YELLOW = "\u001B[33m";

    /** Blue foreground color. */
    public static final String BLUE = "\u001B[34m";

    /** Purple/magenta foreground color. */
    public static final String PURPLE = "\u001B[35m";

    /** Cyan foreground color. */
    public static final String CYAN = "\u001B[36m";

    /** White foreground color. */
    public static final String WHITE = "\u001B[37m";

    /** Bold text modifier. */
    public static final String BOLD = "\u001B[1m";

    /** Underline text modifier. */
    public static final String UNDERLINE = "\u001B[4m";

    // Background colors (optional use)
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_BLUE = "\u001B[44m";
}