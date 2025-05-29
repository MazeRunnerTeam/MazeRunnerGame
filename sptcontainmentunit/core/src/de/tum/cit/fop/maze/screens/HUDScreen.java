package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.staticObjects.Exit;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * The HUDScreen class is responsible for the player's stats and the game information.
 * Implements{@link Screen} interface of libGdx
 * HUDScreen class displays :-
 * -Player lives
 * -Shield availability
 * -Arrow pointing to the exit
 * -Key Counter
 * -Game Timer
 * {@link Stage} manages GUI elements
 * {@link OrthographicCamera} handles the view for HUD elements
 * {@link BitmapFont} used for rendering text
 * {@link Texture} objects are used to render icons and HUD elements
 */
public class HUDScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Player player;

    private OrthographicCamera hudCamera;

    private final BitmapFont font;

    private Texture objectsTexture;
    private TextureRegion heartTexture;
    private TextureRegion emptyHeartTexture;

    private Texture arrow;

    private Texture shieldTexture;

    private Texture keyTexture;

    private Exit exit;

    private float gameTimer;

    /**
     * Constructor for HUDScreen.
     * Initializes all the attributes necessary.
     * @param game Main game instance, used to get global methods and resources
     */
    public HUDScreen(MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.player = game.player;
        this.font = game.getSkin().getFont("font");


        objectsTexture = new Texture(Gdx.files.internal("Sprites/Provided/objects.png"));

        gameTimer = 0;

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        arrow = new Texture(Gdx.files.internal("assets/Sprites/arrows/red-right arrow.png"));

        shieldTexture = new Texture(Gdx.files.internal("assets/Sprites/single_shield.png"));

        keyTexture = new Texture(Gdx.files.internal("assets/Sprites/single_key.png"));
    }

    /**
     * called when HUDScreen becomes current screen.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders all the HUD elements
     * @param delta Change in time(in seconds) since last frame.
     */
    @Override
    public void render(float delta) {

        if (exit == null && game.getCurrentLevel() != 0 && game.getCurrentLevel() != 5) {
            exit = game.getGameScreen().getWorldGenerator().getExit();
        }

        hudCamera.update();
        game.getSpriteBatch().setProjectionMatrix(hudCamera.combined);

        gameTimer += delta;

        game.getSpriteBatch().begin();
        renderLives();
        renderTimer();
        renderShields();
        renderKeyTracker();
        if (exit != null) {
            renderArrow();
        }
        game.getSpriteBatch().end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Renders the player lives on the top left of the screen
     */
    private void renderLives() {
        int lives = player.getLives();
        int maxLives = player.getMaxLives();

        float heartSize =16f;
        float renderSize = heartSize * 2f;
        float spacing = 4f;
        float startX = 20f;
        float startY = Gdx.graphics.getHeight() - renderSize - 20f;

        heartTexture = new TextureRegion(objectsTexture, 4 * 16, 0, (int) heartSize, (int) heartSize);
        emptyHeartTexture = new TextureRegion(objectsTexture, 8 * 16, 0, (int) heartSize, (int) heartSize);

        for (int i = 0; i < maxLives; i++) {
            TextureRegion heart = (i < lives) ? heartTexture : emptyHeartTexture;
            game.getSpriteBatch().draw(heart, startX + i * (renderSize + spacing), startY, renderSize, renderSize);
        }
    }

    /**
     * Renders the game timer in the top middle of the screen.
     */
    private void renderTimer() {
        String time = timeFormat(gameTimer);
        float timerX = (Gdx.graphics.getWidth() - font.getRegion().getRegionWidth())/2;
        font.draw(game.getSpriteBatch(),"Time: " + time, timerX, Gdx.graphics.getHeight()-20);
    }

    /**
     * renders the arrow pointing to the exit.
     */
    private void renderArrow() {
        if (exit == null) {
            return;
        }

        float exitWorldX = exit.getGridX() * Settings.SCALED_TILE_SIZE;
        float exitWorldY = exit.getGridY() * Settings.SCALED_TILE_SIZE;

        Vector3 exitScreenPos = game.getGameScreen().getCameraHelper().getCamera().project(new Vector3(exitWorldX, exitWorldY, 0));

        float arrowWidth = 64;
        float arrowHeight = 32;

        float arrowX = Gdx.graphics.getWidth() / 2f - arrowWidth / 2;
        float arrowY = Gdx.graphics.getHeight() - 100;

        float angle = (float) Math.toDegrees(Math.atan2(exitScreenPos.y - arrowY, exitScreenPos.x - arrowX));


        game.getSpriteBatch().draw(
                arrow, arrowX, arrowY, arrowWidth / 2, arrowHeight / 2, arrowWidth, arrowHeight,
                1, 1, angle, 0, 0, arrow.getWidth(), arrow.getHeight(), false, false
        );
    }

    /**
     * Renders a shield icon if player has a shield active.
     */
    private void renderShields(){
        if(player.hasTemporaryShield()){

            float heartSize = 16f;
            float renderSize = heartSize * 2f;
            float spacing = 4f;
            float shieldX = 20f+(renderSize + spacing)* player.getMaxLives();
            float shieldY = Gdx.graphics.getHeight()-20f-renderSize;

            game.getSpriteBatch().draw(shieldTexture,shieldX,shieldY,renderSize, renderSize);
        }
    }

    /**
     * Renders the key tracker under the health bar.
     */
    private void renderKeyTracker(){

        float keySize = 32f;
        float keyX = 20f;
        float keyY = Gdx.graphics.getHeight()-100f;

        game.getSpriteBatch().draw(keyTexture, keyX, keyY, keySize, keySize);
        String count = "x" + (player.hasKey() ? 1 : 0);
        font.draw(game.getSpriteBatch(),count,keyX+keySize,keyY+keySize/2);
    }

    /**
     * Formats the game timer to the hh:mm:ss format.
     * @param gameTimer current game time in seconds.
     * @return a string with the formatted time.
     */
    private String timeFormat(float gameTimer) {
        int hours = (int) (gameTimer / 3600);
        int minutes = (int) ((gameTimer % 3600)/60);
        int seconds = (int) (gameTimer % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Resizes HUD elements according to window size.
     * @param width new window width.
     * @param height new window height.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
    }

    /**
     * method of interface Screen
     */
    @Override
    public void pause() {}

    /**
     * method of interface Screen
     */
    @Override
    public void resume() {}

    /**
     * method of interface Screen
     */
    @Override
    public void hide() {}

    /**
     * disposes resources used by the screen.
     */
    @Override
    public void dispose() {
        objectsTexture.dispose();
        arrow.dispose();
        shieldTexture.dispose();
    }
}
