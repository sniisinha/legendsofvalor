/**
 * File: ValorMatchSetup.java
 * Package: legends.valor.game
 *
 * Purpose:
 *   Builds and initializes all components required to start a Legends of Valor match.
 *
 * Responsibilities:
 *   - Load items and monsters needed for the match
 *   - Drive hero selection and construct the player's party
 *   - Create core match systems (board, movement, combat, stats, market)
 *   - Perform lane assignment and initial placement/spawning
 *   - Inject initialized components into the ValorMatch instance
 */
package legends.valor.game;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.data.DataLoader;
import legends.items.Item;
import legends.market.Market;
import legends.stats.GameStats;
import legends.valor.combat.ValorCombat;
import legends.valor.world.ValorBoard;
import legends.valor.world.ValorMovement;

public class ValorMatchSetup {

    /**
     * Configures the provided match instance with all required state and systems.
     *
     * @return true if setup completes successfully, false if the match should abort
     */
    public boolean setup(ValorMatch match) {
        // Load game content required for Valor mode (items, market inventory, monster pool)
        DataLoader loader = new DataLoader();
        List<Item> items = loader.loadAllItems();
        Market market = new Market(items);
        DataLoader.globalMonsters = loader.loadAllMonsters();

        // Collect exactly three heroes for Valor lane-based gameplay
        System.out.println();
        System.out.println("Now choose your heroes for Legends of Valor...");
        legends.game.HeroSelection selector = new legends.game.HeroSelection(loader, 3, 3);
        Party party = selector.selectHeroes();

        // Abort if hero selection fails or returns an empty party
        if (party == null || party.getHeroes().isEmpty()) {
            System.out.println("No heroes selected. Exiting Legends of Valor mode.");
            return false;
        }

        // Create core match systems (board, movement, runtime stats, combat rules)
        ValorBoard board = new ValorBoard();
        ValorMovement movement = new ValorMovement(board);

        GameStats stats = new GameStats(GameStats.GameMode.LEGENDS_OF_VALOR, party.getHeroes());
        ValorCombat combat = new ValorCombat(board, stats);

        // Choose lanes and place heroes accordingly, then perform initial monster spawns
        Scanner in = new Scanner(System.in);

        ValorLaneSelector laneSelector = new ValorLaneSelector(in);
        Map<Hero, Integer> lanes = laneSelector.chooseLanes(party);

        ValorSpawner spawner = new ValorSpawner(board);
        spawner.placeHeroesOnBoard(party, lanes);

        List<Monster> laneMonsters = spawner.spawnLaneMonsters(party);

        // Inject all initialized state into the match for execution by the match loop
        match.setMarket(market);
        match.setParty(party);
        match.setBoard(board);
        match.setMovement(movement);
        match.setGameStats(stats);
        match.setCombat(combat);
        match.setLaneMonsters(laneMonsters);

        return true;
    }
}
