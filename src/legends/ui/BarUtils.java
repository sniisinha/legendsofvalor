/**
 * BarUtils.java
 * Utility class for creating visual HP/MP style bars.
 * 
 * This class provides a helper method for rendering
 * progress bars using Unicode block characters. It is used
 * throughout the battle UI for displaying hero and monster
 * health and mana in a clear and consistent visual format.</p>
 */

package legends.ui;

public class BarUtils {

    /**
     * Creates a simple visual bar used for HP/MP displays.
     *
     * @param current the current value (e.g., current HP)
     * @param max the maximum value (e.g., max HP)
     * @param length the total width of the bar in characters
     * @return a formatted string representing the bar
     */
    public static String makeBar(double current, double max, int length) {

        double ratio = current / max;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        int filled = (int) Math.round(length * ratio);
        int empty = length - filled;

        StringBuilder bar = new StringBuilder();

        // Green filled section
        bar.append("\u001B[32m");
        for (int i = 0; i < filled; i++) bar.append("█");

        // Reset color
        bar.append("\u001B[0m");

        // Empty part of the bar
        for (int i = 0; i < empty; i++) bar.append("░");

        return bar.toString();
    }
}