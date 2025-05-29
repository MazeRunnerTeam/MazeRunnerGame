package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {

    private GameScreen gameScreen;

    private SpriteBatch spriteBatch;

    private Skin skin;

    private Music backgroundMusic;

    private int currentLevel = 0;

    public Player player;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));



        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Background Music/MainMenuMusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        player = new Player(this);

        this.setScreen(new PauseScreen(this, null));
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide();
        getScreen().dispose();
        spriteBatch.dispose();
        skin.dispose();
    }


    /**
     * Plays background music for the game.
     * If there's already music playing, it stops and disposes it before starting the new music.
     *
     * @param filePath The file path of the music to be played.
     */
    public void playMusic(String filePath) {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    /**
     * Restarts the game by creating a new player and setting the game to the first level.
     */
    public void restartGame(){
        this.player = new Player(this);
        this.currentLevel = 1;
        this.setScreen(new GameScreen(this));
    }

    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public GameScreen getGameScreen() { return gameScreen; }

    public void setGameScreen(GameScreen newGameScreen) { gameScreen = newGameScreen; }


    public void setLevel(int level) {
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }

        this.currentLevel = level;
        this.gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    public int getCurrentLevel() { return currentLevel; }


}
