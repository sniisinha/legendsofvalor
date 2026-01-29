/**
 * File: ValorInput.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Minimal abstraction over player input for Legends of Valor.
 *
 * Why this exists:
 *   - Decouples game logic from direct console I/O
 *   - Allows swapping console input with scripted input, AI bots,
 *     or automated tests without touching gameplay code
 *
 * Design notes:
 *   - Intentionally small: a single readLine method
 *   - Higher-level parsing (commands, menus, validation) lives
 *     in controllers and UI helpers, not here
 */
package legends.valor.turn;

public interface ValorInput {

    /**
     * Reads a single line of input from the player.
     *
     * @param prompt message shown before reading input
     * @return the raw input line (may be empty, but never modified)
     */
    String readLine(String prompt);
}
