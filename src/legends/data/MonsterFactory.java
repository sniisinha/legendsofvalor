/**
 * File: MonsterFactory.java
 * Description: Loads monsters from text files and generates battle-appropriate monsters.
 */

package legends.data;

import legends.characters.*;
import java.util.*;
import java.util.stream.Collectors;

public class MonsterFactory {

    /**
     * Loads monsters of a specific type (Dragon/Spirit/Exoskeleton)
     * from a given data file. Each line represents one monster.
     */
    public static List<Monster> loadMonsters(String filename, MonsterType type) {

        List<Monster> monsters = new ArrayList<>();
        List<String> lines = FileUtils.readResource(filename);

        boolean skipHeader = true;

        for (String line : lines) {
            if (skipHeader) { skipHeader = false; continue; }
            if (line.trim().isEmpty()) continue;

            // Expected format: name level damage defense dodge%
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 5) continue;

            String name      = parts[0];
            int level        = Integer.parseInt(parts[1]);
            int damage       = Integer.parseInt(parts[2]);
            int defense      = Integer.parseInt(parts[3]);
            double dodgeChance = Integer.parseInt(parts[4]) / 100.0;

            Monster m;

            // Create the correct monster subclass
            switch (type) {
                case DRAGON:
                    m = new Dragon(name, level, damage, defense, dodgeChance);
                    break;
                case SPIRIT:
                    m = new Spirit(name, level, damage, defense, dodgeChance);
                    break;
                case EXOSKELETON:
                    m = new Exoskeleton(name, level, damage, defense, dodgeChance);
                    break;
                default:
                    continue;
            }

            monsters.add(m);
        }

        return monsters;
    }

    /**
     * Generates a list of monsters for a battle.
     * One monster per hero, scaled roughly to party's average level.
     */
    public static List<Monster> generateMonstersForParty(Party party) {

        List<Monster> all = DataLoader.globalMonsters;

        if (all == null || all.isEmpty()) {
            throw new RuntimeException(
                "ERROR: No monsters loaded. Did you call DataLoader.loadAllMonsters() first?"
            );
        }

        List<Monster> result = new ArrayList<>();

        int heroCount = party.getHeroes().size();

        // Use average hero level to choose appropriately leveled monsters
        int avgLevel = (int) Math.ceil(
                party.getHeroes().stream()
                        .mapToInt(Hero::getLevel)
                        .average()
                        .orElse(1)
        );

        Random rand = new Random();

        for (int i = 0; i < heroCount; i++) {

            // Choose monsters within ±2 levels of the party average
            List<Monster> candidates = all.stream()
                .filter(m -> Math.abs(m.getLevel() - avgLevel) <= 2)
                .collect(Collectors.toList());

            // If none match the range, fallback to any monster
            if (candidates.isEmpty()) {
                candidates = all;
            }

            // Copy monster so the original loaded stats are not modified
            Monster chosen = candidates.get(rand.nextInt(candidates.size()));
            result.add(chosen.copy());
        }

        return result;
    }
}