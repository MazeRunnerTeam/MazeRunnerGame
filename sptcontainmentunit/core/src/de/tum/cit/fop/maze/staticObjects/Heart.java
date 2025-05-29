package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Responsible for the Heart item in the game.
 * Handles heart animation and interaction with the player.
 */
public class Heart extends StaticObject {

    private Music heartSound;

    private final Animation<TextureRegion> animation;
    private boolean collected;

    /**
     * Constructor for the Heart object.
     * Initializes the texture and animation for the heart.
     * @param game main game instance.
     * @param x x-coordinate of the heart.
     * @param y y-coordinate of the heart.
     * @param width width of the heart.
     * @param height image of the height.
     */
    public Heart(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "Heart");
        this.heartSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/heartSound.mp3"));

        Texture heartTexture = new Texture("Sprites/Provided/objects.png");

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 4;

        isPassable = true;
        collected = false;

        Array<TextureRegion> heartFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            heartFrames.add(new TextureRegion(
                    heartTexture,
                    col * frameWidth,
                    3*frameHeight,
                    frameWidth,
                    frameHeight
            ));
        }

        animation = new Animation<>(0.15f, heartFrames, Animation.PlayMode.LOOP);

    }

    /**
     * Checks if heart has been collected by the player.
     * @return true if the heart has been collected, if not false.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Responsible for interactions with the heart.
     * Player collects the heart if:
     * -player is touching the heart
     * -heart isnt collected yet
     * -player health is not full
     */
    protected void interact() {
        if (isPlayerTouching() && !collected && player.getLives() < player.getMaxLives()) {
            heartSound.play();
            player.addPoints(10);
            player.gainLife();
            collected = true;
        }
    }

    /**
     * Updates the state of the heart.
     * checks interaction
     * handles animation and rendering
     * @param delta change in time (in seconds) since last frame.
     */
    @Override
    public void update(float delta) {
        if (!collected) {
            interact();
            batch.draw(
                    animation.getKeyFrame(delta, true),
                    gridX * Settings.SCALED_TILE_SIZE,
                    gridY * Settings.SCALED_TILE_SIZE,
                    Settings.SCALED_TILE_SIZE,
                    Settings.SCALED_TILE_SIZE
            );
        }
    }
}