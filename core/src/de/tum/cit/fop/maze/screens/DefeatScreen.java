package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The DefeatScreen class is responsible for rendering the screen when the player loses the game.
 * Gives the player the option to quit or restart the game.
 */
public class DefeatScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;

    /**
     * Defeat Screen Constructor. Sets up the stage for GUI.
     * @param game Main game instance, used to access global methods and resources.
     */
    public DefeatScreen(MazeRunnerGame game) {
        this.game = game;
        stage = new Stage();
    }

    /**
     * Called when the Defeat Screen becomes the current screen.
     * Handles input process and GUI elements.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        game.playMusic("assets/Sounds/Background Music/GameOverMusic.mp3");


        Table table = new Table();
        table.setFillParent(true);
        table.center();


        Label gameOverLabel = new Label("GAME OVER", game.getSkin());
        gameOverLabel.setFontScale(2f);

        TextButton retryButton = new TextButton("RESTART GAME", game.getSkin());
        TextButton exitButton = new TextButton("QUIT", game.getSkin());

        retryButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.restartGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(gameOverLabel).padBottom(50).row();
        table.add(retryButton).pad(50);
        table.add(exitButton).pad(50);
        stage.addActor(table);
    }

    /**
     * Renders the Defeat Screen.
     * @param v Change in time (in seconds) since the last frame.
     */
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /**
     * Resizes Viewport according to window size.
     * @param i new window width.
     * @param i1 new window height.
     */
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update( i, i1, true );
    }

    /**
     * method of interface Screen
     */
    @Override
    public void pause() {

    }
    /**
     * method of interface Screen
     */
    @Override
    public void resume() {

    }

    /**
     * method of interface Screen
     */
    @Override
    public void hide() {

    }

    /**
     * Disposes resources used for the screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
