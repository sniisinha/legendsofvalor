/**
 * File: HeroMovementActions.java
 * Package: legends.valor.turn
 *
 * Purpose:
 *   Encapsulates hero movement and board-rule actions for Legends of Valor turns.
 *
 * Responsibilities:
 *   - Execute standard directional movement through ValorMovement
 *   - Bind and use a hero's home lane for recall behavior
 *   - Perform teleport actions with lane and destination constraints
 *   - Support obstacle removal interactions based on player input
 */
package legends.valor.turn;

import legends.characters.Hero;
import legends.valor.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeroMovementActions {

    // Board provides lane mapping, bounds checks, and tile access
    private final ValorBoard board;

    // Movement engine enforces movement legality and teleport checks
    private final ValorMovement movement;

    // UI helper used for interactive selections (heroes, positions)
    private final HeroTurnUIHelper ui;

    // Stores each hero's assigned lane for recall and lane-based rules
    private final Map<Hero, Integer> homeLane;

    public HeroMovementActions(ValorBoard board,
                               ValorMovement movement,
                               HeroTurnUIHelper ui,
                               Map<Hero, Integer> homeLane) {
        this.board = board;
        this.movement = movement;
        this.ui = ui;
        this.homeLane = homeLane;
    }

    /**
     * Attempts to move the hero one step in the given direction.
     */
    public boolean move(Hero hero, ValorDirection dir) {
        if (hero == null || movement == null) return false;

        boolean moved = movement.moveHero(hero, dir);
        if (!moved) System.out.println("Cannot move there!");
        return moved;
    }

    /**
     * Records the hero's home lane based on current board position if not already set.
     */
    public void bindHomeLaneIfMissing(Hero hero) {
        if (hero == null || homeLane == null || board == null || movement == null) return;
        if (homeLane.containsKey(hero)) return;

        int[] pos = movement.findHero(hero);
        if (pos == null) return;

        int lane = board.getLane(pos[1]);
        if (lane != -1) homeLane.put(hero, lane);
    }

    /**
     * Recalls the hero back to an available heroes' Nexus cell in their home lane.
     */
    public boolean recall(Hero hero) {
        if (hero == null || board == null || movement == null || homeLane == null) return false;

        Integer lane = homeLane.get(hero);
        if (lane == null) {
            System.out.println("Recall failed: home lane unknown.");
            return false;
        }

        int[] cols = board.getNexusColumnsForLane(lane);
        int r = ValorBoard.ROWS - 1;

        int[] dest = null;
        if (cols.length >= 1 && board.canHeroEnter(r, cols[0])) dest = new int[]{r, cols[0]};
        else if (cols.length >= 2 && board.canHeroEnter(r, cols[1])) dest = new int[]{r, cols[1]};

        if (dest == null) {
            System.out.println("Recall failed: your nexus cells are occupied.");
            return false;
        }

        movement.teleportHeroTo(hero, dest[0], dest[1]);
        System.out.println(hero.getName() + " recalled to Nexus.");
        return true;
    }

    /**
     * Teleports the hero near another living hero on a different lane,
     * selecting from valid adjacent destination cells.
     */
    public boolean teleport(Hero hero) {
        if (hero == null || board == null || movement == null) return false;

        int[] myPos = movement.findHero(hero);
        if (myPos == null) return false;

        int myLane = board.getLane(myPos[1]);

        List<Hero> others = getOtherAliveHeroesOnBoard(hero);
        if (others.isEmpty()) {
            System.out.println("No other heroes to teleport to.");
            return false;
        }

        Hero target = ui.pickHero(others);
        if (target == null) return false;

        int[] targetPos = movement.findHero(target);
        if (targetPos == null) return false;

        int targetLane = board.getLane(targetPos[1]);
        if (targetLane == myLane) {
            System.out.println("Teleport must be to a different lane. Choose a hero in another lane.");
            return false;
        }

        // Candidate destinations are adjacent to the target hero and must be legal teleport cells
        List<int[]> candidates = new ArrayList<int[]>();
        int r0 = targetPos[0];
        int c0 = targetPos[1];
        int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };

        for (int i = 0; i < dirs.length; i++) {
            int r = r0 + dirs[i][0];
            int c = c0 + dirs[i][1];
            if (!board.inBounds(r, c)) continue;
            if (board.getLane(c) == -1) continue;
            if (r < r0) continue;

            if (movement.canTeleportHeroTo(hero, r, c)) {
                candidates.add(new int[]{r, c});
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("No valid teleport destination near that hero.");
            return false;
        }

        int[] chosen = ui.pickPosition(candidates);
        if (chosen == null) return false;

        movement.teleportHeroTo(hero, chosen[0], chosen[1]);
        System.out.println(hero.getName() + " teleported!");
        return true;
    }

    /**
     * Removes an obstacle tile adjacent to the hero based on a chosen direction.
     */
    public boolean removeObstacle(Hero hero, ValorInput input) {
        if (hero == null || board == null || movement == null || input == null) return false;

        int[] pos = movement.findHero(hero);
        if (pos == null) return false;

        System.out.println(
                "Remove obstacle direction:\n" +
                "  W = north, A = west, S = south, D = east\n" +
                "  0 = cancel\n"
        );

        String line = input.readLine("Direction: ");
        if (line == null) return false;

        line = line.trim().toUpperCase();
        if ("0".equals(line) || line.isEmpty()) return false;

        int dr = 0, dc = 0;
        char d = line.charAt(0);
        if (d == 'W') dr = -1;
        else if (d == 'S') dr = 1;
        else if (d == 'A') dc = -1;
        else if (d == 'D') dc = 1;
        else {
            System.out.println("Invalid direction.");
            return false;
        }

        int r = pos[0] + dr;
        int c = pos[1] + dc;
        if (!board.inBounds(r, c)) {
            System.out.println("Out of bounds.");
            return false;
        }

        ValorTile t = board.getTile(r, c);
        if (t.getType() != ValorCellType.OBSTACLE) {
            System.out.println("That cell is not an obstacle.");
            return false;
        }

        // Directly updates tile type to represent obstacle removal
        t.setType(ValorCellType.PLAIN);
        System.out.println("Obstacle removed!");
        return true;
    }

    /**
     * Collects all other living heroes currently placed on the board.
     */
    private List<Hero> getOtherAliveHeroesOnBoard(Hero self) {
        List<Hero> out = new ArrayList<Hero>();
        for (int r = 0; r < ValorBoard.ROWS; r++) {
            for (int c = 0; c < ValorBoard.COLS; c++) {
                Hero h = board.getTile(r, c).getHero();
                if (h != null && h != self && h.getHP() > 0 && !out.contains(h)) {
                    out.add(h);
                }
            }
        }
        return out;
    }
}
