package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Responsible for the exit in the game.
 * Player needs to find the exit to complete the level.
 * Deals with player interaction with the exit.
 */
public class Exit extends StaticObject {
    private final Animation<TextureRegion> animation;
    private boolean isPlayingAnimation = false;
    private boolean hasPlayedAnimation = false;
    private float stateTime = 0f;
    private final Texture keyTexture;
    private Music teleportSound;

    /**
     * Constructor for an Exit object.
     * @param game main game instance.
     * @param x x-coordinate of the exit.
     * @param y y-coordinate of the exit.
     * @param width width of the exit.
     * @param height height of the exit.
     */
    public Exit(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "Exit");
        keyTexture = new Texture("assets/Sprites/teleporter1-Sheet.png");
        this.teleportSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/teleportSound.mp3"));

        int frameWidth = 144;
        int frameHeight = 144;
        int animationFrames = 4;

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);
        for (int i = 0; i < 4; i++) {
            for (int col = 0; col < animationFrames; col++) {
                for (int row = 0; row < animationFrames; row++) {
                    keyFrames.add(new TextureRegion(
                            keyTexture,
                            col * frameWidth,
                            col * frameHeight,
                            frameWidth,
                            frameHeight));

                }
            }
        }

        isPassable = true;

        animation = new Animation<>(30f, keyFrames, Animation.PlayMode.NORMAL);
    }

    /**
     * Checks if exit is passable.
     * @return true if player has found the key, if not false.
     */
    @Override
    public boolean isPassable() { return player != null && player.hasKey(); }

    /**
     * Handles player interaction with the exit.
     * Plays animation and teleports player if player is touching the exit.
     */
    @Override
    public void interact() {
        if (isPlayerTouching() && !isPlayingAnimation) {
            isPlayingAnimation = true;
            teleportSound.play();
            player.addLevelKeyToList(game.getCurrentLevel());
            game.getGameScreen().teleportPlayer(width/2f - 0.5f);
            stateTime = 0f;
        }
    }

    /**
     * Updates state of the exit.
     * Handles teleport animation.
     * @param delta Change in time(in seconds) since last frame.
     */
    public void update(float delta) {

        draw(batch);

        if (hasPlayedAnimation) {
            return;
        }
        if (isPlayingAnimation) {
            stateTime += 2*delta;

            if (animation.isAnimationFinished(stateTime)) {
                isPlayingAnimation = false;
                hasPlayedAnimation = true;
                player.removeKey();
                Gdx.app.postRunnable(() -> game.setLevel(game.getCurrentLevel() + 1));
            }
        } else {
            interact();
        }
    }

    /**
     * Draws the exit on the screen.
     * @param batch batch used for drawing the exit.
     */
    public void draw(Batch batch) {
        TextureRegion frame = isPlayingAnimation
                ? animation.getKeyFrame(stateTime)
                : animation.getKeyFrames()[0];
        batch.draw(frame,
                getGridX() * Settings.SCALED_TILE_SIZE,
                getGridY() * Settings.SCALED_TILE_SIZE,
                getWidth()* Settings.SCALED_TILE_SIZE ,
                getHeight()* Settings.SCALED_TILE_SIZE *1.3f);
    }

    /**
     * Disposes resources used by the exit class.
     */
    public void dispose() {
        keyTexture.dispose();
    }
}
