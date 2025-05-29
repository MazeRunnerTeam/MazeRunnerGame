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
 * Responsible for the Shield Item in the game.
 * Handles the animation and interaction.
 */
public class Shield extends StaticObject {
    private final Music shieldSound;

    private final Animation<TextureRegion> animation;
    private boolean collected;

    /**
     * Constructor for Shield object.
     *
     * @param game   main game instance.
     * @param x      x-coordinate of the shield.
     * @param y      y-coordinate of the shield.
     * @param width  width of the shield.
     * @param height height of the shield.
     */
    public Shield(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "Shield");
        this.shieldSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/shieldSound.mp3"));

        Texture heartTexture = new Texture("Sprites/SHIELD.png");

        int frameWidth = 32;
        int frameHeight = 32;
        int animationFrames = 15;

        isPassable = true;
        collected = false;

        Array<TextureRegion> heartFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            heartFrames.add(new TextureRegion(
                    heartTexture,
                    col * frameWidth,
                    0*frameHeight,
                    frameWidth,
                    frameHeight
            ));
        }

        animation = new Animation<>(0.15f, heartFrames, Animation.PlayMode.LOOP);

    }

    /**
     * Checks if the shield has been collected.
     *
     * @return true if the shield has been collected, if not false.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Updates the state of the shield, including interaction and rendering.
     * The shield is only rendered and interactive if it hasn't been collected yet.
     *
     * @param delta time in seconds since the last frame.
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

    /**
     * Handles interaction with the shield.
     * Shield is collected and activated when player is touching the shield and hasn't been collected yet.
     */
    @Override
    protected void interact() {
        if (isPlayerTouching() && !collected) {
            shieldSound.play();
            player.addPoints(10);
            player.activateTemporaryShield();
            collected = true;
        }
    }


}
