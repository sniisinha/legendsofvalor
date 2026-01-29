/**
 * ValorMovement.java
 *
 * Applies Legends of Valor movement rules for heroes and monsters on a ValorBoard.
 * Handles standard movement, teleport placement checks, terrain enter/exit hooks,
 * and lane-based "no bypass" constraints.
 */
package legends.valor.world;

import legends.characters.Hero;
import legends.characters.Monster;

public class ValorMovement {

    // Board dependency used for bounds checks, lane queries, and tile occupancy updates
    private final ValorBoard board;

    public ValorMovement(ValorBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("ValorBoard cannot be null");
        }
        this.board = board;
    }

    public boolean moveHero(Hero hero, ValorDirection dir) {
        if (hero == null || dir == null) return false;

        // Locate the hero on the board before attempting movement
        int[] pos = findHero(hero);
        if (pos == null) return false;

        int fromR = pos[0], fromC = pos[1];
        int toR = fromR + dir.deltaRow();
        int toC = fromC + dir.deltaCol();

        if (!board.inBounds(toR, toC)) return false;

        // Destination must be empty and enterable for a hero
        ValorTile dest = board.getTile(toR, toC);
        if (!dest.isEmptyForHero()) return false;

        // Lane rule: heroes cannot move past the closest blocking monster in the same lane
        if (wouldHeroBypassMonster(fromR, fromC, toR, toC)) return false;

        // Notify the current tile that the hero is leaving (terrain effect cleanup)
        ValorTile fromTile = board.getTile(fromR, fromC);
        fromTile.onExit(hero);

        // Perform the actual board occupancy update
        board.moveHero(hero, fromR, fromC, toR, toC);

        // Notify the destination tile that the hero entered (terrain effect apply)
        dest.onEnter(hero);

        return true;
    }

    public boolean moveMonster(Monster monster, ValorDirection dir) {
        if (monster == null || dir == null) return false;

        // Locate the monster on the board before attempting movement
        int[] pos = findMonster(monster);
        if (pos == null) return false;

        int fromR = pos[0], fromC = pos[1];
        int toR = fromR + dir.deltaRow();
        int toC = fromC + dir.deltaCol();

        if (!board.inBounds(toR, toC)) return false;

        // Destination must be empty and enterable for a monster
        ValorTile dest = board.getTile(toR, toC);
        if (!dest.isEmptyForMonster()) return false;

        // Lane rule: monsters cannot move past the closest blocking hero in the same lane
        if (wouldMonsterBypassHero(fromR, fromC, toR, toC)) return false;

        // Notify the current tile that the monster is leaving (safe even if no bonuses apply)
        ValorTile fromTile = board.getTile(fromR, fromC);
        fromTile.onExit(monster);

        // Perform the actual board occupancy update
        board.moveMonster(monster, fromR, fromC, toR, toC);

        // Notify the destination tile that the monster entered
        dest.onEnter(monster);

        return true;
    }

    /**
     * Checks whether a hero could legally teleport to (toR,toC).
     * This applies lane and occupancy rules without performing the move.
     */
    public boolean canTeleportHeroTo(Hero hero, int toR, int toC) {
        if (hero == null) return false;

        int[] pos = findHero(hero);
        if (pos == null) return false;

        int fromR = pos[0], fromC = pos[1];

        if (!board.inBounds(toR, toC)) return false;
        if (!board.getTile(toR, toC).isEmptyForHero()) return false;

        int fromLane = board.getLane(fromC);
        int toLane   = board.getLane(toC);
        if (fromLane == -1 || toLane == -1) return false;

        // Same-lane teleport must still respect the normal "no bypass monster" rule
        if (fromLane == toLane) {
            return !wouldHeroBypassMonster(fromR, fromC, toR, toC);
        }

        // Cross-lane teleport cannot place the hero behind the foremost monster of the target lane
        int blockRowDest = closestBlockingMonsterRow(ValorBoard.ROWS, toLane);
        return blockRowDest == Integer.MIN_VALUE || toR >= blockRowDest;
    }

    /**
     * Teleports a hero to a destination cell if it is in-bounds and unoccupied for heroes.
     * Terrain exit/enter hooks are triggered to keep bonuses consistent.
     */
    public void teleportHeroTo(Hero hero, int toR, int toC) {
        if (hero == null) return;
        if (!board.inBounds(toR, toC)) return;

        // Do not teleport into an occupied or illegal cell
        if (!board.getTile(toR, toC).isEmptyForHero()) return;

        int[] pos = findHero(hero);
        if (pos == null) return;

        // Notify current tile that the hero is leaving
        ValorTile fromTile = board.getTile(pos[0], pos[1]);
        fromTile.onExit(hero);

        // Update board occupancy
        board.moveHero(hero, pos[0], pos[1], toR, toC);

        // Notify destination tile that the hero entered
        board.getTile(toR, toC).onEnter(hero);
    }

    // Determines whether a hero move would bypass a blocking monster in the same lane
    private boolean wouldHeroBypassMonster(int fromR, int fromC, int toR, int toC) {
        int laneFrom = board.getLane(fromC);
        int laneTo   = board.getLane(toC);

        // If either coordinate is outside a lane, treat it as illegal for lane-based movement rules
        if (laneFrom == -1 || laneTo == -1) return true;

        // Bypass rule only applies within the same lane columns
        if (laneFrom != laneTo) return false;

        int blockRow = closestBlockingMonsterRow(fromR, laneFrom);
        return blockRow != Integer.MIN_VALUE && toR < blockRow;
    }

    /**
     * Finds the closest monster "ahead" of a hero within a lane (toward the enemy nexus).
     * Returns Integer.MIN_VALUE when no blocking monster exists.
     */
    private int closestBlockingMonsterRow(int referenceRow, int lane) {
        int[] cols = board.getNexusColumnsForLane(lane);
        int best = Integer.MIN_VALUE;

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int i = 0; i < cols.length; i++) {
                int c = cols[i];
                Monster m = board.getTile(r, c).getMonster();
                if (!isAlive(m)) continue;

                if (r < referenceRow) {
                    best = Math.max(best, r);
                }
            }
        }
        return best;
    }

    // Determines whether a monster move would bypass a blocking hero in the same lane
    private boolean wouldMonsterBypassHero(int fromR, int fromC, int toR, int toC) {
        int laneFrom = board.getLane(fromC);
        int laneTo   = board.getLane(toC);

        if (laneFrom == -1 || laneTo == -1) return true;
        if (laneFrom != laneTo) return false;

        int blockRow = closestBlockingHeroRow(fromR, laneFrom);
        return blockRow != Integer.MAX_VALUE && toR > blockRow;
    }

    /**
     * Finds the closest hero "ahead" of a monster within a lane (toward the heroes' nexus).
     * Returns Integer.MAX_VALUE when no blocking hero exists.
     */
    private int closestBlockingHeroRow(int referenceRow, int lane) {
        int[] cols = board.getNexusColumnsForLane(lane);
        int best = Integer.MAX_VALUE;

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int i = 0; i < cols.length; i++) {
                int c = cols[i];
                Hero h = board.getTile(r, c).getHero();
                if (!isAlive(h)) continue;

                if (r > referenceRow) {
                    best = Math.min(best, r);
                }
            }
        }
        return best;
    }

    // Hero is considered alive if it exists and has positive HP
    private boolean isAlive(Hero h) {
        return h != null && h.getHP() > 0;
    }

    // Monster is considered alive if it exists and has positive HP
    private boolean isAlive(Monster m) {
        return m != null && m.getHP() > 0;
    }

    /**
     * Finds the current board position of a hero.
     * @return {row, col} if found, otherwise null
     */
    public int[] findHero(Hero hero) {
        if (hero == null) return null;

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile.getHero() == hero) return new int[]{r, c};
            }
        }
        return null;
    }

    /**
     * Finds the current board position of a monster.
     * @return {row, col} if found, otherwise null
     */
    public int[] findMonster(Monster monster) {
        if (monster == null) return null;

        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                ValorTile tile = board.getTile(r, c);
                if (tile.getMonster() == monster) return new int[]{r, c};
            }
        }
        return null;
    }
}
