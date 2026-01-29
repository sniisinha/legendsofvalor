
/**
 * ValorBoard.java
 *
 * Represents the 8x8 game board for Legends of Valor.
 * Handles board generation, lane structure, terrain placement,
 * hero/monster positioning, movement validation, rendering,
 * and win-condition checks.
 */
package legends.valor.world;

import java.util.Random;

import legends.characters.Hero;
import legends.characters.Monster;


public class ValorBoard {

    public static final int ROWS = 8;
    public static final int COLS = 8;

    // Fixed wall columns separating the three lanes
    private static final int WALL_COL_1 = 2;
    private static final int WALL_COL_2 = 5;

    // Board tiles store cell type + current occupants (hero/monster)
    private final ValorTile[][] grid;

    // RNG used for randomized lane terrain generation
    private final Random rng = new Random();

    // ANSI colors used for board rendering
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String RED     = "\u001B[31m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN    = "\u001B[36m";
    private static final String WHITE   = "\u001B[37m";

    public ValorBoard() {
        this.grid = new ValorTile[ROWS][COLS];
        generateLayout();
    }

    public ValorTile getTile(int row, int col) {
        if (!inBounds(row, col)) return null;
        return grid[row][col];
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    // Builds initial board tiles: walls, nexus rows, and randomized lane terrain
    private void generateLayout() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                if (isWall(r, c)) {
                    grid[r][c] = new ValorTile(ValorCellType.INACCESSIBLE);
                    continue;
                }

                if (isNexus(r, c)) {
                    grid[r][c] = new ValorTile(ValorCellType.NEXUS);
                    continue;
                }

                grid[r][c] = new ValorTile(randomLaneType());
            }
        }
    }

    // Randomly selects a lane cell type with a weighted distribution
    private ValorCellType randomLaneType() {
        int v = rng.nextInt(20);

        if (v < 8)           return ValorCellType.PLAIN;
        else if (v < 12)     return ValorCellType.BUSH;
        else if (v < 16)     return ValorCellType.CAVE;
        else if (v < 19)     return ValorCellType.KOULOU;
        else                 return ValorCellType.OBSTACLE;
    }

    // Nexus rows are the first and last board rows
    public boolean isNexus(int row, int col) {
        return row == 0 || row == ROWS - 1;
    }

    public boolean isHeroesNexus(int row, int col) {
        return row == ROWS - 1;
    }

    public boolean isMonstersNexus(int row, int col) {
        return row == 0;
    }

    /**
     * Returns the lane index for a board column.
     * 0 = top lane (cols 0–1), 1 = mid lane (cols 3–4), 2 = bot lane (cols 6–7), -1 = not a lane.
     */
    public int getLane(int col) {
        if (col == 0 || col == 1) return 0;
        if (col == 3 || col == 4) return 1;
        if (col == 6 || col == 7) return 2;
        return -1;
    }

    // Wall columns are always inaccessible
    public boolean isWall(int row, int col) {
        return col == WALL_COL_1 || col == WALL_COL_2;
    }

    // Checks whether a hero can enter a destination tile (bounds + occupancy + accessibility handled by tile)
    public boolean canHeroEnter(int row, int col) {
        if (!inBounds(row, col)) return false;
        ValorTile t = grid[row][col];
        return t != null && t.isEmptyForHero();
    }

    // Checks whether a monster can enter a destination tile (bounds + occupancy + accessibility handled by tile)
    public boolean canMonsterEnter(int row, int col) {
        if (!inBounds(row, col)) return false;
        ValorTile t = grid[row][col];
        return t != null && t.isEmptyForMonster();
    }

    // Moves a hero between two board coordinates (caller must ensure legality)
    public void moveHero(Hero hero, int fromR, int fromC, int toR, int toC) {
        if (hero == null) return;
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return;

        ValorTile from = grid[fromR][fromC];
        ValorTile to   = grid[toR][toC];
        if (from == null || to == null) return;

        from.removeHero();
        to.placeHero(hero);
    }

    // Moves a monster between two board coordinates (caller must ensure legality)
    public void moveMonster(Monster monster, int fromR, int fromC, int toR, int toC) {
        if (monster == null) return;
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return;

        ValorTile from = grid[fromR][fromC];
        ValorTile to   = grid[toR][toC];
        if (from == null || to == null) return;

        from.removeMonster();
        to.placeMonster(monster);
    }

    // Returns the two nexus columns that belong to a lane (used for spawns/respawns)
    public int[] getNexusColumnsForLane(int lane) {
        switch (lane) {
            case 0: return new int[]{0, 1};
            case 1: return new int[]{3, 4};
            case 2: return new int[]{6, 7};
            default: return new int[0];
        }
    }

    // Default hero spawn is the first nexus column in the lane on the bottom row
    public int[] getHeroSpawnCell(int lane) {
        int[] cols = getNexusColumnsForLane(lane);
        if (cols.length == 0) return new int[]{ROWS - 1, 0};
        return new int[]{ROWS - 1, cols[0]};
    }

    // Default monster spawn is the second nexus column in the lane on the top row
    public int[] getMonsterSpawnCell(int lane) {
        int[] cols = getNexusColumnsForLane(lane);
        if (cols.length == 0) return new int[]{0, 0};
        return (cols.length < 2) ? new int[]{0, cols[0]} : new int[]{0, cols[1]};
    }

    // Renders the full board grid using box characters and colored cell symbols
    public void print() {
        System.out.println();
        System.out.println(MAGENTA + BOLD + "===  LEGENDS OF VALOR MAP  ===" + RESET);
        System.out.println();

        printTopBorder();

        for (int r = 0; r < ROWS; r++) {
            System.out.print("  ");
            System.out.print("┃");
            for (int c = 0; c < COLS; c++) {
                String sym = getCellSymbol(grid[r][c], r);
                System.out.print(" " + sym + " ");
                if (c < COLS - 1) {
                    System.out.print("┃");
                }
            }
            System.out.println("┃");

            if (r < ROWS - 1) {
                printMiddleBorder();
            }
        }

        printBottomBorder();
    }

    private void printTopBorder() {
        System.out.print("  ");
        System.out.print("┏");
        for (int c = 0; c < COLS; c++) {
            System.out.print("━━━");
            if (c < COLS - 1) System.out.print("┳");
        }
        System.out.println("┓");
    }

    private void printMiddleBorder() {
        System.out.print("  ");
        System.out.print("┣");
        for (int c = 0; c < COLS; c++) {
            System.out.print("━━━");
            if (c < COLS - 1) System.out.print("╋");
        }
        System.out.println("┫");
    }

    private void printBottomBorder() {
        System.out.print("  ");
        System.out.print("┗");
        for (int c = 0; c < COLS; c++) {
            System.out.print("━━━");
            if (c < COLS - 1) System.out.print("┻");
        }
        System.out.println("┛");
    }

    // Returns the display symbol for a cell based on occupancy first, then terrain type
    private String getCellSymbol(ValorTile tile, int row) {
        if (tile == null) return color(WHITE, "?");

        if (tile.hasHero() && tile.hasMonster()) {
            return color(YELLOW, "*");
        }

        if (tile.hasHero()) {
            return color(CYAN, "H");
        }

        if (tile.hasMonster()) {
            return color(RED, "M");
        }

        ValorCellType type = tile.getType();
        if (type == null) return color(WHITE, "?");

        switch (type) {
            case NEXUS:
                return (row == 0) ? color(RED, "N") : color(BLUE, "N");
            case INACCESSIBLE:
                return color(WHITE, "X");
            case OBSTACLE:
                return color(MAGENTA, "O");
            case BUSH:
                return color(GREEN, "B");
            case CAVE:
                return color(CYAN, "C");
            case KOULOU:
                return color(YELLOW, "K");
            case PLAIN:
            default:
                return color(WHITE, ".");
        }
    }

    private String color(String code, String text) {
        return code + text + RESET;
    }

    // Heroes win when any hero reaches the top nexus row
    public boolean heroesReachedEnemyNexus() {
        int monstersRow = 0;
        for (int c = 0; c < COLS; c++) {
            ValorTile t = grid[monstersRow][c];
            if (t != null && t.hasHero()) return true;
        }
        return false;
    }

    // Monsters win when any monster reaches the bottom nexus row
    public boolean monstersReachedHeroesNexus() {
        int heroesRow = ROWS - 1;
        for (int c = 0; c < COLS; c++) {
            ValorTile t = grid[heroesRow][c];
            if (t != null && t.hasMonster()) return true;
        }
        return false;
    }
}
