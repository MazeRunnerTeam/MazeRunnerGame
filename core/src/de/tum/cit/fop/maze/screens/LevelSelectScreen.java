package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The LevelSelectScreen class is responsible for the screen where the player can choose which level to play.
 * Has buttons for each level from 1-5.
 * handles the logic for level selection and unlocking the next levels.
 */
public class LevelSelectScreen implements Screen {
    private MazeRunnerGame game;
    private Stage stage;

    private final Screen previousScreen;

    /**
     * Contructor for Level select screen.
     * @param game main game instance, used for global resources and methods.
     * @param previousScreen screen to return to when back button is pressed.
     */
    public LevelSelectScreen(MazeRunnerGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());
    }

    /**
     * Handles user input processing.
     * Sets up and handles the logic for GUI elements.
     * Handles logic for unlocking future levels based on player progress.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        for(int i = 0; i < 5; i++){
            int level = i + 1;
            TextButton button;
            button = new TextButton("Level " + level, game.getSkin());
            button.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    previousScreen.dispose();
                    dispose();
                    game.setLevel(level);
                }
            });
            table.add(button).pad(10).row();
        }
        TextButton backButton = new TextButton("Back", game.getSkin());
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(previousScreen);
            }
        });

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left();
        backTable.add(backButton).pad(60);

        stage.addActor(backTable);
        stage.addActor(table);
    }

    /**
     * Renders the level select screen.
     * @param v Change in time(in seconds) since last render.
     */
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(v);
        stage.draw();
    }

    /**
     * Resizes viewport according to window size.
     * @param i new window width.
     * @param i1 new window height.
     */
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update( i, i1, true);
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
