package legends;

import legends.characters.Dragon;
import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Warrior;
import legends.valor.turn.ValorInput;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorCellType;
import legends.valor.world.ValorTile;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/** Shared fixtures. Heroes and monsters default to zero dodge so combat is deterministic. */
public final class TestSupport {

    public static final double EPS = 1e-9;

    private TestSupport() {}

    /** Level 1 warrior: STR 700, DEX 600, AGI 0 (never dodges). */
    public static Hero hero(String name) {
        return new Warrior(name, 1, 100, 50, 700, 600, 0);
    }

    /** Level 1 dragon: 100 HP, 400 damage, 100 defense, never dodges. */
    public static Monster monster(String name) {
        return new Dragon(name, 1, 400, 100, 0.0);
    }

    /** A board with all lane terrain set to PLAIN, replacing the randomized layout. */
    public static ValorBoard plainBoard() {
        ValorBoard board = new ValorBoard();
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                if (!board.isWall(r, c) && !board.isNexus(r, c)) {
                    board.getTile(r, c).setType(ValorCellType.PLAIN);
                }
            }
        }
        return board;
    }

    public static ValorTile place(ValorBoard board, Hero hero, int row, int col) {
        ValorTile tile = board.getTile(row, col);
        tile.placeHero(hero);
        return tile;
    }

    public static ValorTile place(ValorBoard board, Monster monster, int row, int col) {
        ValorTile tile = board.getTile(row, col);
        tile.placeMonster(monster);
        return tile;
    }

    /** ValorInput that replays fixed answers, then returns null. */
    public static ValorInput scriptedInput(String... lines) {
        final Deque<String> queue = new ArrayDeque<String>(Arrays.asList(lines));
        return new ValorInput() {
            @Override
            public String readLine(String prompt) {
                return queue.poll();
            }
        };
    }
}
