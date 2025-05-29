package de.tum.cit.fop.maze.staticObjects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Entry class is responsible for the spawn point in the maze.
 * Handles player spawn animation.
 */
public class Entry extends StaticObject {

    private final Animation<TextureRegion> animation;
    private boolean animationPlayed = false;

    /**
     * Constructor for Entry object.
     * Initialized the entry and the texture for spawn animation.
     * @param game main game instance.
     * @param x x-coordinate of spawn point.
     * @param y y-coordinate of spawn point.
     * @param width width of the entry.
     * @param height height of the entry.
     */
    public Entry(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "Entry");
        Texture keyTexture = new Texture("Sprites/Fog/577.png");

        int frameWidth = 64;
        int frameHeight = 64;
        int animationFrames = 13;

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            keyFrames.add(new TextureRegion(
                    keyTexture,
                    col * frameWidth,
                    3 * frameHeight,
                    frameWidth,
                    frameHeight
            ));
        }
        animation = new Animation<>(0.1f, keyFrames, Animation.PlayMode.NORMAL);

        isPassable = true;
    }

    /**
     * Checks if entry animation has been pleyed.
     * @return true if animation has been played, false if not.
     */
    public boolean isAnimationPlayed() {
        return animationPlayed;
    }

    /**
     * updates the state of the entry and checks if entry animation is played.
     * @param delta change in time(in seconds) since last frame.
     */
    @Override
    public void update(float delta) {
        if (!isAnimationPlayed()) {
            TextureRegion frame = animation.getKeyFrame(delta, false);
            batch.draw(
                    frame,
                    (gridX - 1) * Settings.SCALED_TILE_SIZE,
                    (gridY + 0.25f) * Settings.SCALED_TILE_SIZE,
                    Settings.SCALED_TILE_SIZE * 3f,
                    Settings.SCALED_TILE_SIZE * 3f
            );
            if (animation.isAnimationFinished(delta)) {
                animationPlayed = true;
            }
        }
    }

    /**
     *Handles player interaction with the entry.
     * Does nothing in this case.
     */
    @Override
    protected void interact() {}
}





