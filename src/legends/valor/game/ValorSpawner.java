/**
 * File: ValorSpawner.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Handles initial placement and spawning of heroes and monsters on the Valor board.
 *
 * Responsibilities:
 *   - Place heroes onto the heroes' Nexus row using lane-based spawn rules
 *   - Support both default lane placement and explicit hero-to-lane assignment
 *   - Spawn lane monsters at configured monster spawn cells
 *   - Provide consistent console feedback for spawn events
 */
package legends.valor.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.data.MonsterFactory;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorTile;

public class ValorSpawner {

    // Board used to resolve lane spawn coordinates and access tiles
    private final ValorBoard board;

    // ANSI colors for spawn feedback output
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[92m";
    private static final String CYAN  = "\u001B[96m";
    private static final String YELL  = "\u001B[93m";

    public ValorSpawner(ValorBoard board) {
        this.board = board;
    }

    /**
     * Default hero placement policy:
     * hero0->lane0, hero1->lane1, hero2->lane2.
     */
    public void placeHeroesOnBoard(Party party) {
        if (party == null) return;

        List<Hero> heroes = party.getHeroes();
        if (heroes == null || heroes.isEmpty()) return;

        int lane = 0;
        for (int i = 0; i < heroes.size() && lane < 3; i++) {
            Hero h = heroes.get(i);
            if (h == null) continue;
            placeHeroInLane(h, lane);
            lane++;
        }
    }

    /**
     * Places heroes using an explicit lane assignment mapping.
     * Falls back to default placement when no mapping is provided.
     */
    public void placeHeroesOnBoard(Party party, Map<Hero, Integer> heroToLane) {
        if (party == null) return;
        if (heroToLane == null || heroToLane.isEmpty()) {
            placeHeroesOnBoard(party);
            return;
        }

        List<Hero> heroes = party.getHeroes();
        if (heroes == null || heroes.isEmpty()) return;

        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h == null) continue;

            // Use explicit assignment when present; otherwise fallback by index
            Integer lane = heroToLane.get(h);
            if (lane == null) {
                lane = Integer.valueOf(Math.min(i, 2));
            }

            placeHeroInLane(h, lane.intValue());
        }
    }

    /**
     * Places a single hero into the specified lane using board spawn rules.
     * If the primary spawn cell is occupied, attempts the alternate nexus cell in the same lane.
     */
    private void placeHeroInLane(Hero hero, int lane) {
        if (hero == null) return;
        if (lane < 0 || lane > 2) lane = 1;

        int[] spawn = board.getHeroSpawnCell(lane);
        if (spawn == null || spawn.length < 2) {
            System.out.println("No spawn cell found for lane " + lane);
            return;
        }

        int row = spawn[0];
        int colUsed = spawn[1];

        ValorTile tile = board.getTile(row, colUsed);

        // If occupied, try the other heroes' nexus column for the same lane
        if (tile.getHero() != null) {
            int[] cols = board.getNexusColumnsForLane(lane);
            if (cols != null && cols.length == 2) {

                int altCol = cols[1];
                ValorTile alt = board.getTile(row, altCol);

                if (alt.getHero() == null) {
                    tile = alt;
                    colUsed = altCol;
                } else {
                    System.out.println("Both hero nexus cells occupied in lane " + lane
                            + ". Cannot place " + hero.getName());
                    return;
                }
            }
        }

        // Commit placement on the selected tile
        tile.placeHero(hero);

        // Spawn confirmation output for debugging and player feedback
        System.out.println(GREEN + "✔ " + RESET
                + hero.getName()
                + " spawned in " + CYAN + laneName(lane) + RESET
                + " lane " + YELL + laneCols(lane) + RESET
                + " at (" + row + "," + colUsed + ")");
    }

    /**
     * Spawns up to one monster per lane at the configured monster spawn cell.
     *
     * @return list of monsters successfully spawned onto the board
     */
    public List<Monster> spawnLaneMonsters(Party party) {
        List<Monster> laneMonsters = new ArrayList<Monster>();

        // Generate a candidate pool sized for the current party strength
        List<Monster> generated = MonsterFactory.generateMonstersForParty(party);
        if (generated == null || generated.isEmpty()) {
            System.out.println("No monsters generated for Legends of Valor.");
            return laneMonsters;
        }

        // Randomize selection so lanes don't always get the same monsters
        Collections.shuffle(generated);

        int genIdx = 0;
        for (int lane = 0; lane < 3; lane++) {

            int[] spawn = board.getMonsterSpawnCell(lane);
            if (spawn == null || spawn.length < 2) continue;

            int row = spawn[0];
            int col = spawn[1];

            ValorTile tile = board.getTile(row, col);
            if (tile.getMonster() != null) continue;

            // Skip any null entries in the generated pool defensively
            while (genIdx < generated.size() && generated.get(genIdx) == null) genIdx++;
            if (genIdx >= generated.size()) break;

            Monster m = generated.get(genIdx++);
            tile.placeMonster(m);
            laneMonsters.add(m);

            System.out.println(GREEN + "✔ " + RESET
                    + m.getName()
                    + " spawned in " + CYAN + laneName(lane) + RESET
                    + " lane " + YELL + laneCols(lane) + RESET
                    + " at (" + row + "," + col + ")");

            if (laneMonsters.size() >= 3) break;
        }

        return laneMonsters;
    }

    /**
     * Returns the lane label for display.
     */
    private String laneName(int lane) {
        switch (lane) {
            case 0: return "TOP";
            case 1: return "MID";
            case 2: return "BOT";
            default: return "?";
        }
    }

    /**
     * Returns the board column range associated with a lane.
     */
    private String laneCols(int lane) {
        switch (lane) {
            case 0: return "(cols 0–1)";
            case 1: return "(cols 3–4)";
            case 2: return "(cols 6–7)";
            default: return "(cols ?)";
        }
    }
}
