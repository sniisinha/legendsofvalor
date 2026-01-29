/**
 * File: ValorLaneSelector.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Collects user input to assign each of the three heroes to a Valor lane.
 *
 * Responsibilities:
 *   - Validate that exactly three heroes are provided for lane assignment
 *   - Prompt the user to choose a unique lane for each hero
 *   - Return a mapping from hero to lane index for match setup
 *   - Display formatted lane selection instructions and summary output
 */
package legends.valor.game;

import legends.characters.Hero;
import legends.characters.Party;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ValorLaneSelector {

    // Lane identifiers used throughout the Valor game mode
    public static final int TOP = 0;
    public static final int MID = 1;
    public static final int BOT = 2;

    // Console input source for lane selection
    private final Scanner in;

    // ANSI (same style you use elsewhere)
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String CYAN  = "\u001B[96m";
    private static final String GREEN = "\u001B[92m";
    private static final String YELL  = "\u001B[93m";
    private static final String RED   = "\u001B[91m";
    private static final String MAG   = "\u001B[95m";

    // Fixed width used for the lane-selection UI box
    private static final int BOX_W = 62;

    public ValorLaneSelector(Scanner in) {
        this.in = in;
    }

    /**
     * Prompts the user to assign each hero in the party to a distinct lane.
     *
     * @return mapping from hero to lane constant (TOP/MID/BOT)
     */
    public Map<Hero, Integer> chooseLanes(Party party) {
        Map<Hero, Integer> result = new HashMap<Hero, Integer>();

        // Lane selection is only defined for a party of exactly three heroes
        if (party == null) return result;
        List<Hero> heroes = party.getHeroes();
        if (heroes == null || heroes.size() != 3) {
            System.out.println(RED + "Lane selection requires exactly 3 heroes." + RESET);
            return result;
        }

        // Explain lane numbering and corresponding board columns
        printHeaderBox();

        // Tracks which lanes have already been assigned
        boolean[] used = new boolean[3];

        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h == null) continue;

            while (true) {
                System.out.print(CYAN + "Choose lane for " + BOLD + h.getName() + RESET + CYAN + " (0/1/2): " + RESET);
                String line = in.nextLine().trim();

                Integer lane = parseLane(line);
                if (lane == null) {
                    System.out.println(RED + "Invalid input. Enter 0, 1, or 2." + RESET);
                    continue;
                }

                // Enforce one hero per lane
                if (used[lane.intValue()]) {
                    System.out.println(YELL + "That lane is already taken. Pick a different lane." + RESET);
                    continue;
                }

                used[lane.intValue()] = true;
                result.put(h, lane);

                // Immediate confirmation for user feedback
                System.out.println(GREEN + "✔ " + RESET + h.getName() + " -> " + laneName(lane.intValue()) + laneCols(lane.intValue()));
                break;
            }
        }

        // Display final assignment summary table
        printSummary(result);
        return result;
    }

    private void printHeaderBox() {
        System.out.println();
        System.out.println(repeat("=", BOX_W));
        System.out.println(center(MAG + BOLD + "=== LANE SELECTION ===" + RESET, BOX_W));
        System.out.println(repeat("=", BOX_W));
        System.out.println("Assign each hero to a lane:");
        System.out.println("  " + BOLD + "0" + RESET + " = TOP " + dim("(cols 0–1)") +
                "    " + BOLD + "1" + RESET + " = MID " + dim("(cols 3–4)") +
                "    " + BOLD + "2" + RESET + " = BOT " + dim("(cols 6–7)"));
        System.out.println(repeat("-", BOX_W));
    }

    private void printSummary(Map<Hero, Integer> heroToLane) {
        System.out.println(repeat("-", BOX_W));
        System.out.println(GREEN + "Lane assignment complete." + RESET);

        System.out.println();
        System.out.println("┌────┬──────────────────────┬────────┬──────────┐");
        System.out.println("│ #  │ Hero                 │ Lane   │ Columns  │");
        System.out.println("├────┼──────────────────────┼────────┼──────────┤");

        int idx = 1;
        for (Map.Entry<Hero, Integer> e : heroToLane.entrySet()) {
            Hero h = e.getKey();
            int lane = e.getValue().intValue();

            String heroName = pad(trimTo(h.getName(), 20), 20);
            String laneName = pad(laneNamePlain(lane), 6);
            String cols = pad(laneColsPlain(lane), 8);

            System.out.println("│ " + pad(String.valueOf(idx), 2) + " │ " + heroName + " │ " + laneName + " │ " + cols + " │");
            idx++;
        }

        System.out.println("└────┴──────────────────────┴────────┴──────────┘");
        System.out.println();
    }

    /**
     * Parses a lane input string and validates it as 0, 1, or 2.
     *
     * @return Integer lane value, or null if invalid
     */
    private Integer parseLane(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() == 0) return null;

        int v;
        try {
            v = Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
        if (v < 0 || v > 2) return null;
        return Integer.valueOf(v);
    }

    /**
     * Returns a colored lane label for immediate user feedback.
     */
    private String laneName(int lane) {
        switch (lane) {
            case TOP: return CYAN + "TOP" + RESET;
            case MID: return CYAN + "MID" + RESET;
            case BOT: return CYAN + "BOT" + RESET;
            default:  return CYAN + "?" + RESET;
        }
    }

    /**
     * Returns a plain lane label for summary table formatting.
     */
    private String laneNamePlain(int lane) {
        switch (lane) {
            case TOP: return "TOP";
            case MID: return "MID";
            case BOT: return "BOT";
            default:  return "?";
        }
    }

    /**
     * Returns a dimmed column-range string associated with a lane.
     */
    private String laneCols(int lane) {
        return dim(" " + laneColsPlain(lane));
    }

    /**
     * Returns the board column range represented by a lane.
     */
    private String laneColsPlain(int lane) {
        switch (lane) {
            case TOP: return "(0-1)";
            case MID: return "(3-4)";
            case BOT: return "(6-7)";
            default:  return "(?)";
        }
    }

    /**
     * Repeats a string a fixed number of times (Java 8 compatible).
     */
    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    /**
     * Centers a string within a given width using ANSI-stripped length.
     */
    private String center(String text, int width) {
        String clean = stripAnsi(text);
        if (clean.length() >= width) return text;
        int pad = (width - clean.length()) / 2;
        return repeat(" ", pad) + text;
    }

    /**
     * Pads a string on the right for consistent table alignment.
     */
    private String pad(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s;
        return s + repeat(" ", width - s.length());
    }

    /**
     * Trims a string to a maximum length for fixed-width formatting.
     */
    private String trimTo(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }

    /**
     * Applies a simple dimmed style for secondary UI hints.
     */
    private String dim(String s) {
        return YELL + s + RESET;
    }

    /**
     * Removes ANSI sequences so visible width calculations are accurate.
     */
    private String stripAnsi(String s) {
        return s.replaceAll("\\u001B\\[[;\\d]*m", "");
    }
}
