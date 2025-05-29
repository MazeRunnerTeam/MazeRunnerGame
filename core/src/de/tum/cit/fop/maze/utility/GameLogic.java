package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.actors.Actor;
import de.tum.cit.fop.maze.actors.Boss;
import de.tum.cit.fop.maze.actors.Enemy;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.screens.GameScreen;
import de.tum.cit.fop.maze.screens.HUDScreen;
import de.tum.cit.fop.maze.staticObjects.Heart;
import de.tum.cit.fop.maze.staticObjects.Key;
import de.tum.cit.fop.maze.staticObjects.Shield;
import de.tum.cit.fop.maze.staticObjects.StaticObject;

import java.util.List;

/**
 * Responsible for the game logic for the game.
 * Handles updates and rendering of game elements.
 */
public class GameLogic {
    private GameScreen gameScreen;
    private float stateTime = 0f;

    private final MazeRunnerGame game;
    private CameraHelper cameraHelper;

    private Player player;
    private Boss boss;

    private HUDScreen hudScreen;

    private final OrthogonalTiledMapRenderer mapRenderer;
    private ActorController playerController;
    private  List<ActorController> enemyController;

    private WorldGenerator worldGenerator;
    private List<StaticObject> staticObjects;

    private List<Actor> actorList;

    private TiledMapTileLayer tileLayerTopWall;

    /**
     * Constructor for GameLogic instance.
     * @param gameScreen GameScreen instance this logic is used for.
     */
    public GameLogic(GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        this.game = gameScreen.getGame();
        this.cameraHelper = gameScreen.getCameraHelper();
        this.player = gameScreen.getPlayer();
        this.boss = gameScreen.getBoss();
        this.hudScreen = gameScreen.getHudScreen();
        this.mapRenderer = gameScreen.getMapRenderer();
        this.playerController = gameScreen.getPlayerController();
        this.enemyController = gameScreen.getEnemyController();
        this.worldGenerator = gameScreen.getWorldGenerator();
        this.staticObjects = gameScreen.getStaticObjects();
        this.actorList = gameScreen.getActorList();
        this.tileLayerTopWall = gameScreen.getTileLayerTopWall();
    }

    /**
     * Updates the game state.
     * Handles input, updates actors, objects and game elements.
     * @param delta time in seconds since last update.
     */
    public void update(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { gameScreen.pause(); }

        stateTime += delta;

        playerController.update(delta);
        for (ActorController enemyController : enemyController) {
            enemyController.update(delta);
        }

        for (Actor actor : actorList){
            if (actor != null) {
                actor.update(delta);
            }
        }

        staticObjects.removeIf(obj ->
                (obj instanceof Heart && ((Heart) obj).isCollected()) ||
                (obj instanceof Shield && ((Shield) obj).isCollected())
        );


        cameraHelper.update(player);

        if (game.getCurrentLevel() != 0 && game.getCurrentLevel() != 5) {
            actorList.sort((a, b) -> {
                if (a.getY() != b.getY()) {
                    return Float.compare(b.getY(), a.getY());
                } else {
                    if (a instanceof Enemy && b instanceof Player) {
                        return 1;
                    } else if (a instanceof Player && b instanceof Enemy) {
                        return -1;
                    }
                    return 0;
                }
            });
        }

        worldGenerator.updateImpassableTiles();
    }

    /**
     * Renders the game state.
     * Draws the map, actors and GUI elements.
     * @param delta time in seconds since  last render.
     */
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        mapRenderer.setView(cameraHelper.getCamera());
        mapRenderer.render();

        game.getSpriteBatch().setProjectionMatrix(cameraHelper.getCamera().combined);
        game.getSpriteBatch().begin();

        for (StaticObject obj : staticObjects) {
            if (obj.getClass() == Key.class && boss.isAlive()) {
                continue;
            }
            obj.update(stateTime);
        }

        if (game.getCurrentLevel() != 0 && game.getCurrentLevel() != 5) {
            for (Actor actor : actorList) {
                if (actor.getClass() == Boss.class && !boss.isAlive()) {
                    continue;
                }
                actor.render(game, actor.getStateTime());
            }
        } else {
            player.render(game, stateTime);
        }


        game.getSpriteBatch().end();

        if (game.getCurrentLevel() != 5) {
            mapRenderer.getBatch().begin();
            mapRenderer.renderTileLayer(tileLayerTopWall);
            mapRenderer.getBatch().end();
        }

        if (game.getCurrentLevel() != 0) {
            hudScreen.render(delta);
        }
    }

}