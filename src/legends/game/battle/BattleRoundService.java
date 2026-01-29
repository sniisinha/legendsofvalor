/**
 * File: BattleRoundService.java
 * Package: legends.game.battle
 *
 * Description:
 * Provides round-level battle rules and lifecycle operations, including:
 * - Checking win/lose conditions
 * - Handling battle completion and state transition back to exploration
 * - Applying between-round regeneration
 * - Awarding post-battle rewards (XP, gold) and reviving fallen heroes
 */

package legends.game.battle;

import legends.characters.Hero;
import legends.characters.Monster;
import legends.characters.Party;
import legends.game.ExplorationState;
import legends.game.LegendsGame;

import java.util.List;

public class BattleRoundService {

    /**
     * Checks whether all monsters in the current battle are defeated.
     *
     * @param monsters list of monsters participating in battle
     * @return true if there are no monsters or all have HP <= 0, false otherwise
     */
    public boolean areAllMonstersDead(List<Monster> monsters) {
        if (monsters == null || monsters.isEmpty()) return true;
        for (Monster m : monsters) {
            if (m.getHP() > 0) return false;
        }
        return true;
    }

    /**
     * Evaluates battle termination conditions and performs the appropriate side effects.
     *
     * Win:
     * - Rewards heroes (XP + gold, revives fallen heroes at reduced values)
     * - Transitions the game back to exploration state
     *
     * Loss:
     * - Ends the program (current behavior preserved)
     *
     * @param game     main game controller used to switch states on battle completion
     * @param party    the player's party used to evaluate hero survival and distribute rewards
     * @param monsters monsters used to check victory and determine reward scaling
     * @return true if the battle ended and a terminal action was performed, false if battle continues
     */
    public boolean checkBattleEndAndTransition(LegendsGame game, Party party, List<Monster> monsters) {
        boolean allMonstersDead = areAllMonstersDead(monsters);
        boolean allHeroesDead = party.allDead();

        if (allMonstersDead) {
            System.out.println("\n\u001B[92mYOU WON THE BATTLE!\u001B[0m");
            rewardHeroes(party, monsters);

            // State transition: battle ends -> return to exploration
            game.setState(new ExplorationState(game.getParty(), game.getMap(), game));
            return true;
        }

        if (allHeroesDead) {
            System.out.println("\n\u001B[91mYour party has fallen...\u001B[0m");

            // Current design exits immediately on loss (behavior preserved).
            System.exit(0);
            return true;
        }

        return false;
    }

    /**
     * Applies between-round regeneration to all living heroes.
     * Current rule: alive heroes recover 10% HP and 10% MP (capped at level-based max).
     *
     * @param party party whose heroes will be regenerated
     */
    public void regenerateBetweenRounds(Party party) {
        System.out.println("\nSome of your heroes recover a bit of HP and mana...");

        for (Hero h : party.getHeroes()) {
            if (h.getHP() <= 0) continue; // fainted heroes do not regenerate between rounds

            double maxHP = h.getLevel() * 100;
            double maxMP = h.getLevel() * 50;

            double newHP = Math.min(maxHP, h.getHP() * 1.1);
            double newMP = Math.min(maxMP, h.getMP() * 1.1);

            // Stat mutation is applied through Hero setters (encapsulation of hero state).
            h.setHP(newHP);
            h.setMP(newMP);
        }
    }

    /**
     * Distributes post-battle rewards and revives fallen heroes.
     *
     * Reward rules (preserved from original BattleState):
     * - XP gained per alive hero: (party size * 2)
     * - Gold gained per alive hero: (monsterLevel * 100)
     * - Fallen heroes are revived at 50% HP/MP and do not receive XP or gold
     *
     * @param party    the player's party receiving rewards
     * @param monsters monsters defeated, used for reward scaling (level-based)
     */
    public void rewardHeroes(Party party, List<Monster> monsters) {

        // Determine representative monster level for reward scaling.
        int monsterLevel = 1;
        for (Monster m : monsters) {
            if (m.getLevel() > 0) {
                monsterLevel = m.getLevel();
                break;
            }
        }

        // Reward scaling based on party size and monster level.
        int numMonsters = party.getHeroes().size();
        int xpGain = numMonsters * 2;
        int goldGain = monsterLevel * 100;

        System.out.println("\n--- Rewards ---");

        // Reward only heroes who survived the battle.
        for (Hero h : party.getHeroes()) {
            if (h.getHP() > 0) {
                h.addExperience(xpGain);
                h.earnGold(goldGain);
                System.out.println(h.getName() + ": +" + xpGain + " XP, +" + goldGain + " Gold");
            }
        }

        // Revive fallen heroes with reduced values (no rewards).
        for (Hero h : party.getHeroes()) {
            if (h.getHP() <= 0) {
                double maxHP = h.getLevel() * 100;
                double maxMP = h.getLevel() * 50;

                h.setHP(maxHP * 0.5);
                h.setMP(maxMP * 0.5);

                System.out.println(h.getName()
                        + " is revived with half HP and MP (no XP or gold).");
            }
        }
    }
}