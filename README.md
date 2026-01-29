# Legends of Valor

This project is a console-based, turn-based RPG built in Java using object-oriented design and classic design patterns. Players can choose to play two game modes, each offering a distinct strategic experience.

- **Legends: Monsters and Heroes** – Explore a world, assemble a party of heroes, visit markets, and engage in turn-based battles against monsters using items, spells, and strategic combat decisions.

- **Legends of Valor** – Lead a team of heroes in a lane-based, grid-style battlefield where terrain bonuses, coordinated hero actions, and tactical positioning are key to stopping waves of monsters and destroying the enemy Nexus before your own is overrun.

---

## File & Package Overview

Project is organized into the following top-level packages:

- `legends.game` – Core game loop and game states (intro, exploration, battle, inventory, market, etc.).
- `legends.characters` – Heroes, monsters, party and related character logic.
- `legends.items` – Items (weapons, armor, potions, spells) and their shared interface.
- `legends.market` – Market implementation for buying / selling items.
- `legends.world` – World map, tiles, positions, and map generation.
- `legends.valor` - Core game loop for Legends of Valor.
- `legends.stats` - Game stats and hero stats.
- `legends.ui` – Console UI helpers (colors, bars).
- `legends.leaderboard ` - Tracking, ranking and displaying leaderboard across game runs.
- `legends.persistence ` - Game save and load functionality to restore game state across sessions.
- `legends.data` – Data loading and factories for heroes, monsters, and items (from text files).

### Key Classes (by package)

#### `legends.game`

- **`Main`**  
  Entry point. Creates a `LegendsApp` instance and calls `run()`.

- **`LegendsApp`**
  Serves as the main entry point for the Legends application: holds `Game`.
  - Displays the top-level game selection menu.
  - Accepts and validates user input for game choice.
  - Launches the selected game mode.
  - Manages application-level control flow and exit behavior.

- **`LegendsGame`**  
  Orchestrates the whole game for Legends: Heroes & Monsters: holds the current `GameState`, the `WorldMap`, `Party`, and `Market`.  
  - Initializes map (`MapGenerator`), market (`Market`), and data (`DataLoader`).  
  - Runs the main game loop (`gameLoop`) which repeatedly calls `render`, reads input, `handleInput`, and `update` on the current state.  
  - Shows the intro screen and triggers hero selection before entering exploration.

- **`GameState` (interface)**  
  Defines the contract for all game states:
  - `void render()`
  - `void update(LegendsGame game)`
  - `void handleInput(String input)`
  - `boolean isFinished()`

- **`ExplorationState`**  
  Handles overworld exploration:
  - Shows the map and party position.
  - Accepts movement inputs (`W/A/S/D`), `I` for inventory, `Q` to quit.
  - Triggers markets when stepping on `M` tiles.
  - Randomly starts battles based on tile type.

- **`BattleState`**  
  Turn-based combat controller:
  - Manages heroes’ turn order and monsters’ actions.
  - Offers actions: Attack, Cast Spell, Use Potion, Change Equipment, Show Stats, Flee.
  - Applies damage, dodge chance, spell effects and end-of-round regeneration.
  - On victory: rewards XP and gold, revives fainted heroes, then returns to exploration.
  - On defeat: ends the game.

- **`InventoryState`**  
  Inventory management:
  - First screen: choose which hero’s inventory to view.
  - Second screen: per-hero inventory showing items with aligned table formatting.
  - Allows equipping weapons/armor and using potions.

- **`MarketState`**  
  Market interaction:
  - Main market menu: Buy, Sell, Back.
  - Buy menu: choose category (Weapons, Armor, Potions, Spells), then item and buyer hero.
  - Sell menu: choose hero, category, and item to sell at 50% value.
  - Uses `Market` getters and hero inventory.

- **`HeroSelection`**  
  Hero selection flow:
  - Loads Warriors, Paladins, Sorcerers via `DataLoader`.
  - Enforces PDF rules: HP = level × 100, MP = level × 50 at game start.
  - Prints grouped hero list with stats and ANSI colors.
  - Allows the player to choose between 1 and 3 heroes.
  - Returns a fully constructed `Party`.

#### `legends.valor`  *(only high-level summary here)*

- **`valor.combat - ValorCombat`** 
Implements core combat mechanics for Legends of Valor encounters. Initializes `ValorBoard` and `GameStats`.
- Determine valid targets within attack/cast range on the Valor board
- Execute hero and monster combat actions (attacks and spell casts)
- Apply damage, dodges, and spell debuffs using game rules
- Update per-hero statistics and emit combat log output

- **`valor.game - ValorGame`**
Acts as the entry point for the Legends of Valor game mode.
- Starts and runs a Valor match session
- Handles quit and replay flow for the game mode via `askPlayAgain`
- Delegates post-game processing to the post-game controller via `ValorPostGameController`
- Integrates persistence(`SaveManager`) and leaderboard services (`LeaderboardService`) for end-of-game actions

- **`valor.game - ValorIntroScreen`**
Displays the introductory instructions and rules for Legends of Valor.

- **`valor.game - ValorLaneSelector`**
Collects user input to assign each of the three heroes to a Valor lane.

- **`valor.game - ValorLaneState`**
 Implements the interactive lane gameplay state for Legends of Valor.

- **`valor.game - ValorMarketController`**
Controls market interactions during Legends of Valor gameplay.

- **`valor.game - ValorMatch`**
Orchestrates a full Legends of Valor match from setup through repeated rounds.

- **`valor.game - ValorMatchSetup`**
Builds and initializes all components required to start a Legends of Valor match.

- **`valor.game - ValorMonsterAI`**
Determines movement decisions for monsters during Legends of Valor gameplay.

- **`valor.game - ValorPostGameController`**
Manages end-of-match flow for Legends of Valor after a match concludes.

- **`valor.game - ValorSpawner`**
Handles initial placement and spawning of heroes and monsters on the Valor board.

- **`valor.game - ValorState`**
Defines a common interface for Legends of Valor gameplay states.


- **`valor.turn - ConsoleValorInput`**
Provides console-based input handling for Legends of Valor turn actions.

- **`valor.turn - HeroActionService`**
Provides a unified facade for hero actions during Legends of Valor turns.

- **`valor.turn - HeroCombatActions`**
Encapsulates all hero combat-related actions in Legends of Valor.

- **`valor.turn - HeroEquipmentActions`**
Encapsulates equipment, inventory usage, and market access actions for Valor heroes.

- **`valor.turn - HeroMovementActions`**
Encapsulates hero movement and board-rule actions for Legends of Valor turns.

- **`valor.turn - HeroTurnController`**
Controls the interactive turn flow for a single hero in Legends of Valor.

- **`valor.turn - HeroTurnMenuView`**
Renders the hero turn menu UI for Legends of Valor in the console.

- **`valor.turn - HeroTurnUIHelper`**
Provides reusable console-selection prompts for Legends of Valor hero turn actions.

- **`valor.turn - MonsterTurnController`**
Controls the monster phase of a Legends of Valor round.

- **`valor.turn - ValorInput`**
Abstraction over player input for Legends of Valor to decouple game logic from direct console I/O.

- **`valor.turn - ValorTurnManager`**
Coordinates turn execution for a Legends of Valor round.

- **`valor.ui - ValorCombatLogView`**
Renders formatted combat log messages for Legends of Valor encounters.

- **`valor.ui - ValorEndScreenRendering`**
Renders the Legends of Valor post-game end screen and loaded-save summaries.

- **`valor.ui - ValorRoundStatusView`**
Displays a per-round status snapshot of the Valor board.

- **`valor.world.terrain package`**
Creates terain instances via `TerrainFactory` for all the terrain-enabled board cells (`terrain.BushTerrain`, `terrain.CaveTerrain`, `terrain.KoulouTerrain`)

- **`valor.world - ValorBoard`**
Represents the 8x8 game board for Legends of Valor.
 * Handles board generation, lane structure, terrain placement,hero/monster positioning, movement validation, rendering,and win-condition checks.

- **`valor.world - ValorCellType`**
Enumerates all possible cell types (nexus, plain, inaccessible etc) on the Legends of Valor board. Each type defines its display symbol and basic accessibility rules.

- **`valor.world - ValorDirection`**
Represents the four cardinal movement directions used on the board. Each direction defines how it changes a unit’s row and column position.

- **`valor.world - ValorMovement`**
Applies movement rules for heroes and monsters on a `ValorBoard`. Handles standard movement, teleport placement checks, terrain enter/exit hooks, and lane-based "no bypass" constraints.

- **`valor.world - ValorTile`**
Represents a single cell on the board. A tile has a fixed cell type, optional terrain behavior, and may contain at most one Hero and one Monster.

#### `legends.characters`  *(only high-level summary here)*

- **`Hero` (abstract)**  
  Base class for all hero types (Warrior, Paladin, Sorcerer). Holds common stats (level, HP, MP, STR, DEX, AGI, gold, inventory, etc.) and combat helpers (attack damage, dodge chance, usePotion, equipWeapon/Armor, etc.).

- **`Warrior`, `Paladin`, `Sorcerer`**  
  Concrete hero types with different stat emphases (melee, balanced, magic).

- **`Monster`**  
  Represents an enemy; has HP, damage, defense, dodge chance, level, etc.

- **`Party`**  
  Contains the list of heroes and current `Position`.  
  - Movement (`moveTo`)  
  - Utility like `getAliveHeroes()`, `getRandomAliveHero()`, `allDead()`, `printStats()`.

- **`Inventory`**  
  Holds a hero’s items and provides `getItems()`, `addItem`, `removeItem`, and `printFormatted(int width)`.

#### `legends.items`

- **`Item` (interface)**  
  Common contract for all items:
  - `String getName()`
  - `int getPrice()`
  - `int getRequiredLevel()`

- **`Weapon`**  
  Adds `damage`, `handsRequired` and `getHands()`.  
  `toString()` prints level, damage and hands.

- **`Armor`**  
  Adds `reduction` (damage reduction).

- **`Potion`**  
  Adds `effectAmount` and `List<PotionAttribute>` describing which stats it affects.

- **`Spell`**  
  Adds `damage`, `manaCost`, and `SpellType` (`FIRE`, `ICE`, `LIGHTNING`).  
  BattleState applies type-specific debuffs (defense, damage, dodge).

- **`PotionAttribute` (enum)**  
  `HEALTH`, `MANA`, `STRENGTH`, `DEXTERITY`, `AGILITY`.

- **`SpellType` (enum)**  
  `FIRE`, `ICE`, `LIGHTNING`.

#### `legends.market`

- **`Market`**  
  Holds category-separated lists: `List<Weapon>`, `List<Armor>`, `List<Potion>`, `List<Spell>`.  
  - Constructor 1: takes separate lists.  
  - Constructor 2 (preferred): takes `List<Item>` and sorts into type-specific lists.

#### `legends.world`

- **`Tile` (abstract)**  
  Base tile; defines:
  - `boolean isAccessible()`
  - `String getSymbol()`

- **`CommonTile`**  
  Accessible, shown as `.` on the map.

- **`InaccessibleTile`**  
  Not accessible, shown as `X`.

- **`MarketTile`**  
  Accessible market tile, shown as `M`.

- **`Position`**  
  Simple row/col pair for the party’s location.

- **`WorldMap`**  
  2D grid of `Tile`s with helper methods:
  - `setTile`, `getTile`, `inBounds`, `canMove`.
  - `print(Position partyPos)` draws a colored grid with borders and party marker `P`.

- **`MapGenerator`**  
  Randomly generates a `WorldMap` with configurable size:
  - ~20% `InaccessibleTile`, ~15% `MarketTile`, remaining `CommonTile`.
  - Can be easily tweaked to adjust map density and difficulty.

#### `legends.ui`

- **`BarUtils`**  
  Builds colored HP/MP bars given current, max, and bar length.

- **`Colors`**  
  Centralized ANSI color constants (foregrounds, some backgrounds, bold, underline).

- **`ConsoleUI `**
  Centralized ANSI color codes and text styling with utilities for boxed sections, aligned tables, ANSI-safe padding/centering, and consistent console output formatting.

#### `legends.data`  *(high-level)*

- **`DataLoader`**  
  Reads text files for heroes, items, and monsters and constructs Java objects at startup.  
  Also exposes collections like `loadWarriors()`, `loadPaladins()`, etc.

- **`MonsterFactory`**  
  Utility for generating a set of monsters suitable for the current party (e.g., based on level and size).

---

## Compilation & Run Instructions (Terminal)

Assuming your project root is the directory that contains the `legends/` folder and all `.java` files.

**Compile all Java sources**

   ```
   cd path/to/project/root

   # Compile all .java files into a 'bin' directory
   javac -d bin $(find . -name "*.java") 
   ```
<br>

# I/O Example

```
══════════════════════════════════════════════
          WELCOME TO MONSTERS & HEROES
══════════════════════════════════════════════

Choose which adventure you want to play:

  [1] Legends: Monsters & Heroes
      Classic exploration with random battles,
      markets, and turn-based combat.

  [2] Legends of Valor
      3-lane tactical battle for control of the Nexuses.

  [Q] Quit

Enter your choice (1 / 2 / Q): 2

Launching Legends of Valor...


════════════════════════ LEGENDS OF VALOR ════════════════════════

Welcome to Legends of Valor, a 3-lane tactical battle between
your party of Heroes and waves of Monsters.

Goal
 - Enter the enemy (top) Nexus to win.
 - If any Monster reaches your (bottom) Nexus, you lose.

Team Setup
 - You must select exactly 3 Heroes (one per lane).

Terrain Bonuses (Heroes only)
 - Bush   → +10% Dexterity
 - Cave   → +10% Agility
 - Koulou → +10% Strength

Turn Order
 - Each round:
     1) Hero 1 acts, Hero 2 acts, Hero 3 acts
     2) Monster 1 acts, Monster 2 acts, Monster 3 acts

Attack Range
 - Same tile OR one tile N/S/E/W from attacker.

Controls
 - W/A/S/D = move
 - F = basic attack
 - N = wait/skip
 - Q = quit to menu

Press ENTER to begin...


Now choose your heroes for Legends of Valor...

=================================================
            🧙 HERO SELECTION 🛡️
=================================================
Choose from Warriors, Paladins, Sorcerers.

⚔️  WARRIORS — Strong melee fighters
No   Name                 Lvl  HP     MP     STR    DEX    AGI   
--------------------------------------------------------
1    Gaerdal_Ironhand     1    100    50     700    600    500   
2    Sehanine_Monnbow     1    100    50     700    500    800   
3    Muamman_Duathall     1    100    50     900    750    500   
4    Flandal_Steelskin    1    100    50     750    700    650   
5    Undefeated_Yoj       1    100    50     800    700    400   
6    Eunoia_Cyn           1    100    50     700    600    800   

🛡️  PALADINS — Holy warriors with balanced stats
No   Name                 Lvl  HP     MP     STR    DEX    AGI   
--------------------------------------------------------
7    Parzival             1    100    50     750    700    650   
8    Sehanine_Moonbow     1    100    50     750    700    700   
9    Skoraeus_Stonebones  1    100    50     650    350    600   
10   Garl_Glittergold     1    100    50     600    400    500   
11   Amaryllis_Astra      1    100    50     500    500    500   
12   Caliber_Heist        1    100    50     400    400    400   

✨ SORCERERS — Powerful magic users
No   Name                 Lvl  HP     MP     STR    DEX    AGI   
--------------------------------------------------------
13   Rillifane_Rallathil  1    100    50     750    500    450   
14   Segojan_Earthcaller  1    100    50     800    650    500   
15   Reign_Havoc          1    100    50     800    800    800   
16   Reverie_Ashels       1    100    50     800    400    700   
17   Kalabar              1    100    50     850    600    400   
18   Skye_Soar            1    100    50     700    500    400   


You must choose exactly 3 heroes.

Select by number, or 0 to finish.

Select hero 1 of 3 (0 = done): 4
✔ Added Flandal_Steelskin
Select hero 2 of 3 (0 = done): 11
✔ Added Amaryllis_Astra
Select hero 3 of 3 (0 = done): 14
✔ Added Segojan_Earthcaller

You have reached the maximum (3).

=============== YOUR PARTY ===============
 → Flandal_Steelskin (Level 1)
    HP: 100 / 100
    MP: 50 / 50
    STR: 750   DEX: 700   AGI: 650   Gold: 2500

 → Amaryllis_Astra (Level 1)
    HP: 100 / 100
    MP: 50 / 50
    STR: 500   DEX: 500   AGI: 500   Gold: 2500

 → Segojan_Earthcaller (Level 1)
    HP: 100 / 100
    MP: 50 / 50
    STR: 800   DEX: 650   AGI: 500   Gold: 2500

===========================================


==============================================================
                    === LANE SELECTION ===
==============================================================
Assign each hero to a lane:
  0 = TOP (cols 0–1)    1 = MID (cols 3–4)    2 = BOT (cols 6–7)
--------------------------------------------------------------
Choose lane for Flandal_Steelskin (0/1/2): 2
✔ Flandal_Steelskin -> BOT (6-7)
Choose lane for Amaryllis_Astra (0/1/2): 0
✔ Amaryllis_Astra -> TOP (0-1)
Choose lane for Segojan_Earthcaller (0/1/2): 1
✔ Segojan_Earthcaller -> MID (3-4)
--------------------------------------------------------------
Lane assignment complete.

┌────┬──────────────────────┬────────┬──────────┐
│ #  │ Hero                 │ Lane   │ Columns  │
├────┼──────────────────────┼────────┼──────────┤
│ 1  │ Segojan_Earthcaller  │ MID    │ (3-4)    │
│ 2  │ Amaryllis_Astra      │ TOP    │ (0-1)    │
│ 3  │ Flandal_Steelskin    │ BOT    │ (6-7)    │
└────┴──────────────────────┴────────┴──────────┘

✔ Flandal_Steelskin spawned in BOT lane (cols 6–7) at (7,6)
✔ Amaryllis_Astra spawned in TOP lane (cols 0–1) at (7,0)
✔ Segojan_Earthcaller spawned in MID lane (cols 3–4) at (7,3)
✔ Casper spawned in TOP lane (cols 0–1) at (0,1)
✔ Blinky spawned in MID lane (cols 3–4) at (0,4)
✔ Casper spawned in BOT lane (cols 6–7) at (0,7)

======================================================================================
                                    ROUND 1 STATUS                                    
======================================================================================

HEROES ON BOARD (3)
--------------------------------------------------------------------------------------
Name                 Lv   Pos       HP           MP         Lane  
--------------------------------------------------------------------------------------
Amaryllis_Astra      L1   (7,0)     100/100      50/50      TOP   
Segojan_Earthcaller  L1   (7,3)     100/100      50/50      MID   
Flandal_Steelskin    L1   (7,6)     100/100      50/50      BOT   

MONSTERS ON BOARD (3)
--------------------------------------------------------------------------------------
Name                 Lv   Pos       HP         Lane  
--------------------------------------------------------------------------------------
Casper               L1   (0,1)     100        TOP   
Blinky               L1   (0,4)     100        MID   
Casper               L1   (0,7)     100        BOT   
======================================================================================

===  LEGENDS OF VALOR MAP  ===

  ┏━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━┓
  ┃ N ┃ M ┃ X ┃ N ┃ M ┃ X ┃ N ┃ M ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ . ┃ O ┃ X ┃ K ┃ B ┃ X ┃ K ┃ K ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ B ┃ B ┃ X ┃ . ┃ C ┃ X ┃ . ┃ K ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ C ┃ . ┃ X ┃ K ┃ C ┃ X ┃ C ┃ O ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ K ┃ C ┃ X ┃ K ┃ B ┃ X ┃ C ┃ B ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ . ┃ C ┃ X ┃ B ┃ C ┃ X ┃ C ┃ K ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ B ┃ C ┃ X ┃ B ┃ . ┃ X ┃ B ┃ C ┃
  ┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫
  ┃ H ┃ N ┃ X ┃ H ┃ N ┃ X ┃ H ┃ N ┃
  ┗━━━┻━━━┻━━━┻━━━┻━━━┻━━━┻━━━┻━━━┛

┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                 HERO 1 TURN                  ┃
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃ Flandal_Steelskin  (7,6)  BOT LANE           ┃
┃ Lv 1 | HP 100/100 | MP 50/50 | Gold 2500     ┃
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃ [W/A/S/D] Move   [F] Attack                  ┃
┃ [C] Cast Spell [P] Use Potion                ┃
┃ [E] Equip     [T] Teleport                   ┃
┃ [R] Recall    [O] Remove Obstacle            ┃
┃ [M] Market    [I] Inventory                  ┃
┃ [Z] Status                                   ┃
┃ [N] Wait      [Q] Quit                       ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
Enter command: W
[Terrain Bonus] Flandal_Steelskin entered Bush: Dexterity +70.00 (700.00 → 770.00)
```

# Design Patterns Used

- State Pattern <br>
	- GameState interface + concrete ExplorationState, BattleState, InventoryState, MarketState, HeroSelection. <br>
	- LegendsGame & ValorGame hold a GameState reference and delegate render, handleInput, and update. <br>
	- Makes it easy to add more states (e.g., PauseState, SettingsState) without touching existing ones.

- Factory Pattern/ Factory Method<br>
	- MonsterFactory, HeroParty, TerrainFactory encapsulate logic to create an appropriate set of monsters/heroes/terrains based on the party.<br>
	- MapGenerator.generate(size) centralizes world creation and randomness.<br>
	- DataLoader acts as a factory for heroes, items, and monsters from text files.

- Strategy Pattern<br>
	- Monster behavior (advance, attack decisions, movement direction) is encapsulated inside ValorMonsterAI. <br>
  - In MonsterTurnController, the turn controller does not hardcode monster logic; it delegates decisions to the AI strategy. <br>
  - Allows monster behavior to be changed or extended without modifying turn logic, and makes it easy to add new AI strategies (e.g., aggressive, defensive, random).

- Facade Pattern<br>
  - Used in HeroActionService, where the subsystems HeroCombatActions, HeroMovementActions etc, are hidden behind a facade.<br>
  - HeroTurnController interacts with one class (HeroActionService) instead of many low-level action classes, which delegates work to the appropriate specialized action class.<br>
  - Reduces coupling between the turn controller and action implementations, and simplifies hero turn logic.

- Encapsulation & Information Hiding<br>
	- Each package hides its implementation details; other packages only use public APIs (Party, Market, WorldMap, etc.), making future changes safer.


# Object Design Qualities

- Scalability <br>
	- Clear separation between:
	- World (WorldMap, Tile, MapGenerator)
	- Characters (Party, Hero, Monster)
	- Items (Item hierarchy)
	- 	Game Flow (GameState + LegendsGame)
	-	To add:
	-	New hero classes → add a subclass of Hero and load from data files.
	-	New tiles → extend Tile and optionally tweak MapGenerator.
	-	New items → implement Item and plug into Market/DataLoader.

- Extendibility <br>
	-	GameState interface makes adding new screens simple.
	-	Enums (SpellType, PotionAttribute) make it easy to add new behavior cases in one place.
	-	Market works off Item interface; any new item implementing Item can be bought/sold with minimal changes.
	-	WorldMap.print and BarUtils centralize UI formatting, so UI style changes don’t require touching game logic.

<br>


# Implementation Qualities

## Usability
- Clear text menus, color coding, and boxed layouts help the player easily understand the interface.
- Consistent keybindings throughout the game:
  - **W / A / S / D** → Movement
  - **I** → Inventory
  - **Q** → Quit or Flee (context-dependent)
- The Hero Selection screen clearly communicates how many heroes can be chosen and shows detailed stats for decision-making.

## Readability
- Classes are short and focused on a single responsibility  
  (e.g., `MarketState` only handles the market flow).
- Helper methods such as:
  - `chooseHeroForTransaction`
  - `buyFromList`
  - `sellFromList`
  - `printItemTable`
  - `printHeroMenu`  
  help reduce duplication and make the code self-describing.
- Javadoc-style comments were added throughout the codebase to explain higher-level components such as game states, transitions, and UI systems.

## Best Practices
- Use of interfaces (`GameState`, `Item`) and enums (`SpellType`, `PotionAttribute`) prevents hard-coded strings and improves type safety.
- **Separation of concerns**:
  - Data loading is handled by `DataLoader`
  - Gameplay flow is handled using different `GameState` classes
  - Presentation (UI printing, bars, colors, etc.) is centralized via helpers like `BarUtils` and `WorldMap.print`
- **Avoiding duplication**:
  - Shared formatting helpers for tables and menus.
  - Shared input-safety functions using `try/catch`.
- **Robust input handling**:
  - All numeric inputs are validated
  - Invalid inputs do not crash the game
  - The game falls back gracefully with error messages
- **Reset-to-default behavior**:
  - All stats are reloaded from the data files at the start of each run (per assignment requirement), ensuring clean game state.

---

# Key Highlights & Features

- **Randomly Generated World Map**  
  Inaccessible tiles, market tiles, and common tiles each appear with configurable probabilities.

- **Party-Based Gameplay**  
  Choose **1–3 heroes** from:
  - Warriors  
  - Paladins  
  - Sorcerers  

- **Turn-Based Battle System**
  - Heroes take turns sequentially
  - Monsters attack afterward as a group
  - Wide range of actions: attacks, spells, potions, equipment changes
  - Dodge mechanics and spell-type debuffs  
    (Fire lowers DEF, Ice lowers DMG, Lightning lowers Dodge)

- **Market & Inventory System**
  - Buy/sell weapons, armor, spells, and potions
  - Level requirements enforced
  - Per-hero gold and inventory
  - Equip and consume items directly from the inventory screen

- **Regeneration & Revival Mechanics**
  - Heroes regenerate **10% HP & MP** between battle rounds
  - Fallen heroes revive at **50% HP/MP** after a win
  - XP and gold rewards scale with number and level of monsters

- **File-Driven Game Data**
  - Heroes, monsters, weapons, armor, spells, potions loaded from external `.txt` files
  - Makes balancing and modifying game content extremely easy without code changes

- **Styled Console UI**
  - Color-coded text
  - Unicode box drawings
  - Smooth HP/MP bar rendering
  - Clear section headers for readability

<br>

---