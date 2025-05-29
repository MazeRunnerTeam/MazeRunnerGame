Maze Runner Game
Description
Maze Runner is a 2D top-down game where the player must navigate through procedurally generated mazes, avoid enemies, and reach the exit to progress through levels. The game features different obstacles, interactive objects like keys and shields, and a boss battle on each level. It is built using LibGDX, a Java-based game development framework.

Requirements
* Java 17 or later
* Gradle 
* LibGDX dependencies (will be fetched automatically by Gradle)
When running the game through IntelliJ, ensure that -XonStartThread VM option is removed if the operating system is Windows or Linux.

How to Play
* Navigate using W, A, S, D keys or arrows.
* During cutscenes press SPACE to go next  or ENTER to skip
* Defeat the Boss, it will spawn a Key somewhere on the map.
* Pick up a Key to unlock the Teleporter.
* Collect Shields to become temporarily invulnerable.
* Collect extra lives.
* Avoid Enemies, on contact with them you lose life.
* Follow an arrow on the top to reach a Teleporter, if you have a Key.
* Avoid Traps, they can damage you or reduce your speed.


Point System
You get 
* 5 points for every picked up life,
* 10 points for every picked up shield,
* 100 points for collecting the key,
* 500 points for defeating the boss

You lose
* 50 points for any sort of damage taken.


Maze Generation

* The world is created using the WorldGenerator class.
* Maps are loaded from TMX files.
* All objects and actors starting position are also loaded from TMX files.
* Collectable items are loaded randomly.


Controls

Action           | Key
------------------------
Move Up          | W
Move Down        | S
Move Left        | A
Move Right       | D
Pause Game       | ESC




Class Hierarchy & UML Diagram
To better understand the structure of the game, find the the UML diagram attached (UML.png)


Class Organization
* Actor.java - Base abstract class for all moving entities like Player, Enemy or Boss.
* ActorController.java - Handles movement logic for actors and processes player’s input.
* BattleScreen.java - Handles the boss battle scene.
* Boss.java - Defines the boss enemy.
* CutsceneScreen - Shown on the cutscenes, tells the story.
* CameraHelper.java - Manages the in-game camera.
* DefeatScreen.java - Shown when the player loses.
* DialogueOverlay.java -  Responsible for the dialogue overlay in the game.
* Enemy.java - Controls enemy movement and behavior.
* Entry.java - Marks the player's starting point and provides animation from teleportation.
* Exit.java - Defines the exit point for each level, e.g. Teleporter.
* GameScreen.java - Handles rendering and game state.
* GameLogic - stores the game logic.
* Heart.java - Provides collectable lives.
* HUDScreen.java - Manages the user interface.
* Key.java - Provides collectible keys used to unlock Teleporter (Exit).
* LevelSelectScreen.java - Allows selection of game levels.
* MazeRunnerGame.java - The main game class, responsible for initializing game.
* Node.java - Used in the pathfinding algorithm.
* PathFinder.java - Implements A* pathfinding algorithm.
* PauseScreen.java - Menu that displays when the game is paused.
* Player.java - Represents the player, handles movement and interactions.
* Settings.java - Stores TileSize settings and Scale.
* SettingsScreen.java - Provides options to customize game settings like volume or camera zoom.
* Shield.java - Provides temporary invulnerability.
* StaticObject.java - A base abstract class for all in-game static/collectable objects.
* TMXParser.java - Parses the TMX map files and loads all static objects and actors positions.
* TrapDamage.java - A type of trap that damages the player.
* TrapDebuff.java - A type of trap that temporarily slows down the player.
* VictoryScreen.java - Displayed when the player wins the game.
* Wall.java - Represents impassable walls of the maze in the game.
* WorldGenerator.java - Handles map generation.


Credits
* Developers: Tair Kezdekbayev, Pranin Edward Prejith, Sergey Sarkisyan.
* Art & Sound: Free assets from OpenGameArt, Kenney, and other sources.

License
This project is licensed under the CC BY.