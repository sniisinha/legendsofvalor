/**
 * File: BattleView.java
 * Package: legends.game.battle
 *
 * Description:
 * Responsible for rendering the battle screen to the console.
 * This view prints:
 * - The party's hero panel (HP/MP bars)
 * - The monsters panel (HP bars)
 * - The action menu for the currently acting hero
 */

package legends.game.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.ui.BarUtils;

import java.util.List;

public class BattleView {

    /**
     * Width of the ASCII UI panels used for heroes/monsters/actions.
     * This constant centralizes layout tuning and prevents magic numbers scattered in code.
     */
    private static final int BOX_WIDTH = 52;

    /**
     * Renders the full battle UI: hero stats, monster stats, and the action menu.
     *
     * @param party    the player's party (source of hero list)
     * @param monsters list of monsters in the battle
     * @param acting   the hero whose turn it is (may be null in edge cases)
     */
    public void render(Party party, List<Monster> monsters, Hero acting) {

        System.out.println("\n████████████████ 🛡️  BATTLE  🗡️ ████████████████\n");

        renderHeroesBox(party);
        renderMonstersBox(monsters);
        renderActionsBox(acting);
    }

    /**
     * Prints the hero panel, including name and HP/MP bars for each hero in the party.
     * Uses level-based max HP/MP to keep the UI consistent with your battle rules.
     */
    private void renderHeroesBox(Party party) {
        System.out.println("╔" + repeat("═", BOX_WIDTH) + "╗");
        System.out.println("║" + center("HEROES", BOX_WIDTH) + "║");
        System.out.println("╠" + repeat("═", BOX_WIDTH) + "╣");

        for (Hero h : party.getHeroes()) {
            double maxHP = h.getLevel() * 100;
            double maxMP = h.getLevel() * 50;

            String hpBar = "HP: " + BarUtils.makeBar(h.getHP(), maxHP, 12)
                    + " (" + (int) h.getHP() + "/" + (int) maxHP + ")";

            String mpBar = "MP: " + BarUtils.makeBar(h.getMP(), maxMP, 12)
                    + " (" + (int) h.getMP() + "/" + (int) maxMP + ")";

            System.out.println("║ " + pad(h.getName(), BOX_WIDTH - 2) + " ║");
            System.out.println("║ " + pad(hpBar, BOX_WIDTH + 7) + " ║");
            System.out.println("║" + repeat(" ", BOX_WIDTH) + "║");
            System.out.println("║ " + pad(mpBar, BOX_WIDTH + 7) + " ║");
            System.out.println("║" + repeat(" ", BOX_WIDTH) + "║");
        }

        System.out.println("╚" + repeat("═", BOX_WIDTH) + "╝\n");
    }

    /**
     * Prints the monster panel, including name and HP bar for each monster.
     * Only rendering is performed; living/dead logic is handled elsewhere.
     */
    private void renderMonstersBox(List<Monster> monsters) {
        System.out.println("╔" + repeat("═", BOX_WIDTH) + "╗");
        System.out.println("║" + center("MONSTERS", BOX_WIDTH) + "║");
        System.out.println("╠" + repeat("═", BOX_WIDTH) + "╣");

        for (Monster m : monsters) {
            double maxHP = m.getLevel() * 100;

            String hpBar = "HP: " + BarUtils.makeBar(m.getHP(), maxHP, 12)
                    + " (" + (int) m.getHP() + "/" + (int) maxHP + ")";

            System.out.println("║ " + pad(m.getName(), BOX_WIDTH - 2) + " ║");
            System.out.println("║ " + pad(hpBar, BOX_WIDTH + 7) + " ║");
            System.out.println("║" + repeat(" ", BOX_WIDTH) + "║");
        }

        System.out.println("╚" + repeat("═", BOX_WIDTH) + "╝\n");
    }

    /**
     * Prints the action menu and prompts the user for the next command.
     * The caller is responsible for reading/handling the input.
     */
    private void renderActionsBox(Hero acting) {
        System.out.println("╔" + repeat("═", BOX_WIDTH) + "╗");
        System.out.println("║" + center("ACTIONS", BOX_WIDTH) + "║");
        System.out.println("╠" + repeat("═", BOX_WIDTH) + "╣");

        String turnLine = (acting != null) ? ("Turn: " + acting.getName()) : "Turn: (no hero)";
        System.out.println("║ " + pad(turnLine, BOX_WIDTH - 2) + " ║");

        System.out.println("╠" + repeat("─", BOX_WIDTH) + "╣");

        System.out.println("║ 1. Attack" + pad("", BOX_WIDTH - 10) + "║");
        System.out.println("║ 2. Cast Spell" + pad("", BOX_WIDTH - 14) + "║");
        System.out.println("║ 3. Use Potion" + pad("", BOX_WIDTH - 14) + "║");
        System.out.println("║ 4. Change Equipment" + pad("", BOX_WIDTH - 20) + "║");
        System.out.println("║ 5. Show Party Stats" + pad("", BOX_WIDTH - 20) + "║");
        System.out.println("║ Q. Flee" + pad("", BOX_WIDTH - 8) + "║");

        System.out.println("╚" + repeat("═", BOX_WIDTH) + "╝");

        if (acting != null) {
            System.out.print("\nChoose action for " + acting.getName() + ": ");
        } else {
            System.out.print("\nChoose action: ");
        }
    }

    /**
     * Pads a string to a fixed width using spaces.
     * This keeps the ASCII UI columns aligned.
     */
    private String pad(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        return text + repeatChar(' ', width - text.length());
    }

    /**
     * Repeats a string count times.
     * Implemented using StringBuilder to remain compatible with Java 8.
     */
    private String repeat(String s, int count) {
        if (s == null) s = "";
        StringBuilder sb = new StringBuilder(s.length() * Math.max(0, count));
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    /**
     * Repeats a single character count times.
     * Used for efficient generation of padding and borders.
     */
    private String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder(Math.max(0, count));
        for (int i = 0; i < count; i++) sb.append(c);
        return sb.toString();
    }

    /**
     * Centers a text label within a fixed width.
     * Used for titles like HEROES/MONSTERS/ACTIONS.
     */
    private String center(String text, int width) {
        if (text == null) text = "";
        int pad = (width - text.length()) / 2;
        if (pad < 0) pad = 0;
        return repeatChar(' ', pad) + text + repeatChar(' ', Math.max(0, width - pad - text.length()));
    }
}