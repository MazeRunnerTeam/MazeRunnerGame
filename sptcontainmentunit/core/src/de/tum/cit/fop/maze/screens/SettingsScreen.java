package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The SettingsScreen class is responsible for the settings menu in the Maze Runner game.
 * Allows players to adjust volume and camera zoom.
 */
public class SettingsScreen implements Screen {

    private MazeRunnerGame game;
    private Stage stage;
    private Music backgroundMusic;
    private final Screen previousScreen;

    /**
     * Constructor for Settings Screen.
     * @param game main game instance used for global methods and resources.
     * @param previousScreen screen before the settings screen was opened.
     */
    public SettingsScreen(MazeRunnerGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.stage = new Stage(new ScreenViewport());
        this.backgroundMusic = game.getBackgroundMusic();
    }

    /**
     * Handles UI elements to control camera zoom and volume.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();

        Label volumeLabel = new Label("Volume: ", game.getSkin());
        Slider volumeSlider = new Slider(0, 1, 0.01f, false, game.getSkin());
        volumeSlider.setValue(backgroundMusic.getVolume());
        volumeSlider.addListener(event -> {
            backgroundMusic.setVolume(volumeSlider.getValue());
            return false;
        });

        if(game.getGameScreen() != null){
            Label cameraLabel = new Label("Camera Zoom: ", game.getSkin());
            Slider camZoomSlider = new Slider(0.25f, 1f, 0.01f, false, game.getSkin());
            camZoomSlider.setValue(1 - game.getGameScreen().getCameraHelper().getCamera().zoom);
            camZoomSlider.addListener(event -> {
                float zoom = 1 - camZoomSlider.getValue();
                game.getGameScreen().getCameraHelper().setZoom(zoom);
                return false;
            });

            table.add(cameraLabel).pad(10);
            table.add(camZoomSlider).pad(20);

            TextButton giveKeyButton = new TextButton("Give Key for this Level (For Tutors)", game.getSkin());
            giveKeyButton.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    game.player.collectKey();
                }
            });

            bottomTable.add(giveKeyButton).pad(60);
        }

        table.add(volumeLabel).pad(10);
        table.add(volumeSlider).pad(10).row();

        TextButton backSettingsButton = new TextButton("Back", game.getSkin());
        backSettingsButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(previousScreen);
            }
        });

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left();
        backTable.add(backSettingsButton).pad(60);

        stage.addActor(table);
        stage.addActor(bottomTable);
        stage.addActor(backTable);

    }

    /**
     * Renders the settings menu.
     * @param v Change in time(in seconds) since last frame.
     */
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
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
