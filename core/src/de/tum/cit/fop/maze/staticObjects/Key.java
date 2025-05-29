package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Responsible for the Key item in the game.
 * Handles animation and interaction with the player.
 */
public class Key extends StaticObject {

    private final Animation<TextureRegion> animation;

    /**
     * Contructor for Key object.
     * Initializes the texture and the animation.
     * @param game main game instance.
     * @param x x-coordinate of the key.
     * @param y y-coordinate of the key.
     * @param width width of the key.
     * @param height height of the key.
     */
    public Key(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "Key");

        Texture keyTexture = new Texture("Sprites/key_32x32_24f.png");

        int frameWidth = 32;
        int frameHeight = 32;
        int animationFrames = 24;

        isPassable = true;

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            keyFrames.add(new TextureRegion(
                    keyTexture,
                    col * frameWidth,
                    0,
                    frameWidth,
                    frameHeight
            ));
        }

        animation = new Animation<>(0.1f, keyFrames, Animation.PlayMode.LOOP);

    }

    /**
     * Handles interaction with the key.
     * If player touches the key, it is collected.
     */
    protected void interact() {
        if (isPlayerTouching()) {
            player.collectKey();
        }
    }

    /**
     * Updates the state of the key.
     * Handles interaction and rendering.
     * Key is only rendered if player hasn't collected it.
     * @param delta change in time(in seconds) since last frame.
     */
    @Override
    public void update(float delta) {
        if (!player.hasKey()) {
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
