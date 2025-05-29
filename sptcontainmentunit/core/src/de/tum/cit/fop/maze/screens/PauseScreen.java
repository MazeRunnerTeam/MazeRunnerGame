package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The PauseScreen class is responsible for the main game menu in the Maze Runner Game.
 * Has options to start/resume,restart,choose levels,change settings or quit the game.
 */
public class PauseScreen implements Screen {
    private MazeRunnerGame game;
    private Stage stage;
    private final Screen previousScreen;
    private final Texture background;

    /**
     * Constructor for the PauseScreen.
     * initializes all the attributes for the screen.
     * @param game main game instance to access global methods and resources.
     * @param previousScreen previous screen to return to when resuming the game.
     */
    public PauseScreen(MazeRunnerGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        background = new Texture(Gdx.files.internal("assets/Sprites/background_image.png"));

    }

    /**
     * Handles input processing and all the GUI elements along with its functioning.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        game.playMusic("assets/Sounds/Background Music/MainMenuMusic.mp3");


        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("SPT Containment Unit", game.getSkin());
        titleLabel.setFontScale(2f);

        TextButton resumeButton = new TextButton("Resume", game.getSkin());
        TextButton startNewGameButton = new TextButton("New Game", game.getSkin());
        TextButton restartButton = new TextButton("Restart", game.getSkin());
        TextButton levelButton = new TextButton("Levels", game.getSkin());
        TextButton settingsButton = new TextButton("Settings", game.getSkin());
        TextButton quitButton = new TextButton("Quit", game.getSkin());

        table.setBackground(game.getSkin().newDrawable("white",0,0,0,0.4f));

        table.add(titleLabel).colspan(2).pad(20).row();

        if (game.getGameScreen() != null) {
            table.add(resumeButton).pad(10).width(300);
            table.add(levelButton).pad(10).width(300).row();
            table.add(restartButton).pad(10).width(300);
            table.add(settingsButton).pad(10).width(300).row();
            table.add(quitButton).pad(10).colspan(2).width(300);
        } else {
            table.add(startNewGameButton).pad(10).width(300);
            table.add(levelButton).pad(10).width(300).row();
            table.add(settingsButton).pad(10).width(300);
            table.add(quitButton).pad(10).colspan(2).width(300);
        }

        resumeButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                resume();
            }
        });

        startNewGameButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new CutsceneScreen(game, 1));
            }
        });

        restartButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                dispose();
                restartGame();
            }
        });

        levelButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                LevelSelectScreen levelSelectScreen = new LevelSelectScreen(game, PauseScreen.this);
                game.setScreen(levelSelectScreen);
            }
        });

        settingsButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor ) {
                game.setScreen(new SettingsScreen(game,game.getScreen()));
            }
        });

        quitButton.addListener(new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(table);
    }

    /**
     * Renders the Menu Screen.
     * @param delta Change in time (in seconds) since the last frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float screenSize=(float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        float textureSize=(float)background.getWidth()/(float)background.getHeight();

        if(screenSize>textureSize){
            height = width/textureSize;
        }
        else {
            width = height*textureSize;
        }

        float screenX = (Gdx.graphics.getWidth()-width)/2;
        float screenY = (Gdx.graphics.getHeight()-height)/2;

        game.getSpriteBatch().draw(background, screenX, screenY, width, height);
        game.getSpriteBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { game.setScreen(previousScreen); }

        stage.act();
        stage.draw();
    }

    /**
     * Resizes viewport according to the size of the window
     * @param width new width of the window
     * @param height new height of the window
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    /**
     * Disposes resources used by this screen and resumes previous screen.
     */
    @Override
    public void resume() {
        dispose();
        previousScreen.resume();
    }

    /**
     * Hides this screen and clears input processing.
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * disposes resources used by this screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }

    /**
     * Restarts the current level by disposing existing resources and recreating them.
     */
    public void restartGame() {
        if(game.getGameScreen() != null) {
            game.getGameScreen().dispose();
        }

        GameScreen gameScreen = (new GameScreen(game));
        game.setGameScreen(gameScreen);
        game.setScreen(gameScreen);
        Gdx.input.setInputProcessor(gameScreen.getPlayerController());
    }
}
