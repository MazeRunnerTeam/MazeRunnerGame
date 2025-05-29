package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.actors.Boss;
import de.tum.cit.fop.maze.actors.Enemy;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.staticObjects.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * WorldGenerator is responsible for creating and managing the game world.
 * It loads the map, initializes game objects like enemies, the boss, and static objects,
 * and provides utility methods to interact with the world.
 */

public class WorldGenerator {

    private final TiledMap tiledMap;
    private final TMXParser tmxParser;
    private static int mapWidth, mapHeight;

    private final Set<String> impassableTiles = new HashSet<>();

    private List<StaticObject> staticObjects;
    private Exit exit;
    private final List<Enemy> enemies;
    private final Boss boss;
    private final Player player;
    private final Random random = new Random();
    private Key key;


    /**
     * WorldGenerator Constructor.
     * Loads the game world for a given level.
     *
     * @param game The main game instance.
     * @param level The level number to load.
     */
    public WorldGenerator(MazeRunnerGame game, int level) {
        String filePath = "maps/Level " + level + ".tmx";
        this.tiledMap = new TmxMapLoader().load(filePath);
        this.tmxParser = new TMXParser(filePath, Settings.TILE_SIZE);

        int[] startPosition = tmxParser.findPlayerStartingPosition(game);

        player = game.player;
        player.spawnPlayer(startPosition[0], startPosition[1], this);
        enemies = tmxParser.parseEnemies(this);
        boss = tmxParser.parseBoss(this);

        staticObjects = tmxParser.parseStaticObjects(game);

        for (StaticObject obj : staticObjects) {
            if (obj instanceof Key) {
                this.key = (Key) obj;
            } else if (obj instanceof Exit) {
                this.exit = (Exit) obj;
            }
        }

        initializeMapDimensions();
        updateImpassableTiles();
        switch (level) {
            case 1 -> generatePowerUps(game, 3, 2);
            case 2 -> generatePowerUps(game, 2, 2);
            case 3 -> generatePowerUps(game, 2, 1);
            case 4 -> generatePowerUps(game, 1, 1);
        }
    }

    /**
     * Getters
     */
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public static int getMapWidth() {
        return mapWidth;
    }

    public static int getMapHeight() {
        return mapHeight;
    }
    public List<StaticObject> getStaticObjects() {
        return staticObjects;
    }

    public Player getPlayer() { return player; }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Boss getBoss() {
        return boss;
    }

    public Exit getExit() {
        return exit;
    }

    /**
     * Initializes map dimensions from the tile layer.
     */
    private void initializeMapDimensions() {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer tileLayer) {
                mapWidth = tileLayer.getWidth();
                mapHeight = tileLayer.getHeight();
                return;
            }
        }
    }

    /**
     * Checks if a given position is passable.
     * It is used in other classes to calculate paths.
     *
     * @param x X-coordinate of the tile.
     * @param y Y-coordinate of the tile.
     * @return True if passable, false otherwise.
     */
    public boolean isPassable(int x, int y) {
        return !impassableTiles.contains(x + "," + y);
    }

    /**
     * Checks if a given position is occupied by an enemy.
     *
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return True if occupied by an enemy, false otherwise.
     */
    public boolean isOccupiedByEnemy(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the list of impassable tiles based on static objects.
     */
    public void updateImpassableTiles() {
        impassableTiles.clear();

        for (StaticObject obj : staticObjects) {
            if (!obj.isPassable()) {
                for (int x = obj.getGridX(); x < obj.getGridX() + obj.getWidth(); x++) {
                    for (int y = obj.getGridY(); y < obj.getGridY() + obj.getHeight(); y++) {
                        impassableTiles.add(x + "," + y);
                    }
                }
            }
        }
    }

    /**
     * Generates power-ups (hearts and shields) at random passable locations.
     *
     * @param game The game instance.
     * @param livesCount Number of heart power-ups to generate.
     * @param shieldCount Number of shield power-ups to generate.
     */
    public void generatePowerUps(MazeRunnerGame game, int livesCount, int shieldCount) {
        int randomX;
        int randomY;
        for (int i = 0; i < livesCount; i++) {
            do {
                randomX = random.nextInt(getMapWidth());
                randomY = random.nextInt(getMapHeight());
            } while  (!isValidPowerUpPosition(randomX, randomY));

            staticObjects.add(new Heart(game, randomX, randomY, 1, 1));
        }
        for (int i = 0; i < shieldCount; i++) {
            do {
                randomX = random.nextInt(getMapWidth());
                randomY = random.nextInt(getMapHeight());
            } while (!isValidPowerUpPosition(randomX, randomY));
            staticObjects.add(new Shield(game, randomX, randomY, 1, 1));
        }
    }


    /**
     * Checks if a power-up can be placed at a specific position.
     *
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return True if the position is valid, false otherwise.
     */
    private boolean isValidPowerUpPosition(int x, int y) {
        if (!isPassable(x, y)) {
            return false;
        }

        if (y > 0 && !isPassable(x, y - 1)) {
            return false;
        }

        for (StaticObject obj : staticObjects) {
            if (obj.getGridX() == x && obj.getGridY() == y) {
                return true;
            }
        }
        return true;
    }


    /**
     * Returns the static object at a given position.
     *
     * @param nextX X-coordinate.
     * @param nextY Y-coordinate.
     * @return The static object at the given position, or null if none exists.
     */
    public StaticObject getStaticObjectAt(int nextX, int nextY) {
        for (StaticObject obj : staticObjects) {
            if (obj.getGridX() == nextX && obj.getGridY() == nextY) {
                return obj;
            }
        }
        return null;
    }
}
