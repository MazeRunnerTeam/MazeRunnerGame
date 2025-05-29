package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.actors.Boss;
import de.tum.cit.fop.maze.actors.Enemy;
import de.tum.cit.fop.maze.staticObjects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TMXParser is responsible for loading and parsing TMX map files. It loads corresponding graphics.
 * It extracts game objects and actor's starting positions, including static objects, player, enemy etc.
 * from the tile-based map.
 */

public class TMXParser {

    private final TiledMap tiledMap;
    private final int tileSize;

    /**
     * Creates a new TMXParser instance and loads a tiled map from the given file path.
     *
     * @param tmxFilePath the path to the TMX map file.
     * @param tileSize the size of a single tile in pixels.
     */
    public TMXParser(String tmxFilePath, int tileSize) {
        this.tiledMap = new TmxMapLoader().load(tmxFilePath);
        this.tileSize = tileSize;
    }

    /**
     * Parses static objects from the TMX map and returns a list of them.
     * Static objects include walls, keys, traps, teleporter, DialogueTile.
     *
     * @param game the MazeRunnerGame instance needed for object creation.
     * @return a list of parsed static objects.
     */
    public List<StaticObject> parseStaticObjects(MazeRunnerGame game) {
        List<StaticObject> objects = new ArrayList<>();

        MapLayers mapLayers = tiledMap.getLayers();

        for (MapLayer layer : mapLayers) {
            if (layer.getObjects() != null) {
                for (MapObject obj : layer.getObjects()) {
                    if (obj instanceof RectangleMapObject rectObj) {
                        Rectangle rect = rectObj.getRectangle();
                        int startX = (int) (rect.x / tileSize);
                        int startY = (int) (rect.y / tileSize);
                        int width = (int) (rect.width / tileSize);
                        int height = (int) (rect.height / tileSize);
                        String type = layer.getName();

                        switch (type) {
                            case "Walls" -> objects.add(new Wall(game, startX, startY, width, height));
                            case "Key" -> objects.add(new Key(game, startX, startY, width, height));
                            case "Entry" -> objects.add(new Entry(game, startX, startY, width, height));
                            case "Trap Damage" -> objects.add(new TrapDamage(game, startX, startY, width, height));
                            case "Trap Debuff" -> objects.add(new TrapDebuff(game, startX, startY, width, height));
                            case "Teleporter" -> objects.add(new Exit(game, startX, startY, width, height));
                            case "DialogueTile" -> objects.add(new DialogueTile(game, startX, startY, width, height));
                            case "CutsceneTile" -> objects.add(new CutsceneTile(game, startX, startY, width, height));
                        }
                    }
                }
            }
        }
        return objects;
    }

    /**
     * Parses enemies from the TMX map and returns a list of them.
     *
     * @param worldGenerator the WorldGenerator instance for enemy instantiation.
     * @return a list of parsed enemies.
     */
    public List<Enemy> parseEnemies(WorldGenerator worldGenerator) {
        List<Enemy> enemies = new ArrayList<>();
        MapLayers mapLayers = tiledMap.getLayers();

        for (MapLayer layer : mapLayers) {
            if (layer.getName().equals("Enemy") && layer.getObjects() != null) {
                for (MapObject obj : layer.getObjects()) {
                    if (obj instanceof RectangleMapObject rectObj) {
                        Rectangle rect = rectObj.getRectangle();
                        int startX = (int) (rect.x / tileSize);
                        int startY = (int) (rect.y / tileSize);
                        enemies.add(new Enemy(startX, startY, worldGenerator));
                    }
                }
            }
        }
        return enemies;
    }

    /**
     * Parses the boss character from the TMX map and returns it.
     *
     * @param worldGenerator the WorldGenerator instance for boss creation.
     * @return the Boss instance or null if not found.
     */
    public Boss parseBoss(WorldGenerator worldGenerator) {
        Boss boss = null;
        MapLayers mapLayers = tiledMap.getLayers();

        for (MapLayer layer : mapLayers) {
            if (layer.getName().equals("Boss") && layer.getObjects() != null) {
                for (MapObject obj : layer.getObjects()) {
                    if (obj instanceof RectangleMapObject rectObj) {
                        Rectangle rect = rectObj.getRectangle();
                        int startX = (int) (rect.x / tileSize);
                        int startY = (int) (rect.y / tileSize);
                        boss = new Boss(startX, startY, worldGenerator);
                    }
                }
            }
        }
        return boss;
    }

    /**
     * Returns the player's starting position (Entry).
     * If no entry point is found, defaults to (1,1).
     *
     * @param game the MazeRunnerGame instance.
     * @return an array containing the x and y coordinates of the player's starting position.
     */
    public int[] findPlayerStartingPosition(MazeRunnerGame game) {
        for (StaticObject obj : parseStaticObjects(game)) {
            if (obj instanceof Entry) {
                return new int[]{obj.getGridX(), obj.getGridY()};
            }
        }
        return new int[]{1, 1};
    }

    /**
     * Disposes of the tiled map resources to free up memory.
     */
    public void dispose() {
        tiledMap.dispose();
    }
}
