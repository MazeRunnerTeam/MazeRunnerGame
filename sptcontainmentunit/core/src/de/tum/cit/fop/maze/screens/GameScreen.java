package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import de.tum.cit.fop.maze.*;
import de.tum.cit.fop.maze.actors.Actor;
import de.tum.cit.fop.maze.actors.Boss;
import de.tum.cit.fop.maze.actors.Enemy;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.utility.GameLogic;
import de.tum.cit.fop.maze.staticObjects.*;
import de.tum.cit.fop.maze.utility.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private CameraHelper cameraHelper;

    private DialogueOverlay dialogueOverlay;

    private Player player;
    private List<Enemy> enemies;
    private Boss boss;

    private HUDScreen hudScreen;

    private final OrthogonalTiledMapRenderer mapRenderer;
    private ActorController playerController;
    private  List<ActorController> enemyController;

    private WorldGenerator worldGenerator;
    private List<StaticObject> staticObjects;

    private List<Actor> actorList = new ArrayList<>();

    private TiledMapTileLayer tileLayerTopWall;

    private GameLogic gameLogic;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;

        worldGenerator = new WorldGenerator(game, game.getCurrentLevel());

        staticObjects = worldGenerator.getStaticObjects();
        mapRenderer = new OrthogonalTiledMapRenderer(worldGenerator.getTiledMap(), 1 / Settings.SCALE);

        cameraHelper = new CameraHelper(Gdx.graphics.getWidth() / Settings.SCALE, Gdx.graphics.getHeight() / Settings.SCALE);

        cameraHelper.setMapBounds(
                worldGenerator.getTiledMap().getProperties().get("width", Integer.class) * Settings.SCALED_TILE_SIZE,
                worldGenerator.getTiledMap().getProperties().get("height", Integer.class) * Settings.SCALED_TILE_SIZE
        );

        player = worldGenerator.getPlayer();
        enemies = worldGenerator.getEnemies();
        boss = worldGenerator.getBoss();

        hudScreen = new HUDScreen(game);

        dialogueOverlay = new DialogueOverlay(game, game.getSkin());

        playerController = new ActorController(player, this);
        enemyController = new ArrayList<>();
        for (Enemy enemy : enemies ) {
            enemyController.add(new ActorController(enemy, this));
        }

        actorList.addAll(enemies);
        actorList.add(player);
        actorList.add(boss);

        tileLayerTopWall = (TiledMapTileLayer) worldGenerator.getTiledMap().getLayers().get("Top Wall Graphics");

        gameLogic = new GameLogic(this);
    }

    /**
     * Renders the game screen.
     *
     * @param delta time in seconds since last render.
     */
    @Override
    public void render(float delta) {
        if (dialogueOverlay.isActive()) {
            dialogueOverlay.render();
            return;
        }

        gameLogic.update(delta);
        gameLogic.render(delta);
    }

    /**
     * Handles the resizing of the game window.
     *
     * @param width new width of the game window.
     * @param height new height of the game window.
     */
    @Override
    public void resize(int width, int height) {
        cameraHelper.resize(width, height);
        hudScreen.resize(width, height);
    }


    /**
     * Pauses the game and shows the pause screen.
     */
    @Override
    public void pause() {
        playerController.resetMovement();
        game.setScreen(new PauseScreen(game, this));
    }

    /**
     * Resumes the game from the pause screen.
     */
    @Override
    public void resume() {
        game.setScreen(this);
        Gdx.input.setInputProcessor(playerController);
    }

    /**
     * Sets this screen as the current screen for the game.
     */
    @Override
    public void show() {
        game.playMusic("assets/Sounds/Background Music/GamePlayMusic.mp3");
        Gdx.input.setInputProcessor(playerController);
    }

    @Override
    public void hide() {}

    /**
     * Disposes resources used by the GameScreen.
     */
    @Override
    public void dispose() {
        mapRenderer.dispose();
        hudScreen.dispose();
    }

    /**
     * Teleports the player to a new position.
     *
     * @param teleporterCenterX The x-coordinate of the teleporter's center.
     */
    public void teleportPlayer(float teleporterCenterX) {
        playerController.setDisableMovement(true);
    }

    public MazeRunnerGame getGame() {
        return game;
    }

    public CameraHelper getCameraHelper() {
        return cameraHelper;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Boss getBoss() {
        return boss;
    }

    public HUDScreen getHudScreen() {
        return hudScreen;
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public ActorController getPlayerController() {
        return playerController;
    }

    public List<ActorController> getEnemyController() {
        return enemyController;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public List<StaticObject> getStaticObjects() {
        return staticObjects;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public TiledMapTileLayer getTileLayerTopWall() {
        return tileLayerTopWall;
    }

    public DialogueOverlay getDialogueOverlay() {
        return dialogueOverlay;
    }
}