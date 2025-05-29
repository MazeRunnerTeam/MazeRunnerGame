package de.tum.cit.fop.maze.staticObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Responsible for damage trap in the game.
 * Handles behavior for a trap that damages the player upon contact.
 */
public class TrapDamage extends StaticObject {

    private final TextureRegion inactiveFrame, activeFrame;
    private TextureRegion currentFrame;
    private boolean activated;
    private Music trapDamageSound;

    /**
     * Constructor for TrapDamage object.
     *
     * @param game   main game instance.
     * @param x      x-coordinate of the trap.
     * @param y      y-coordinate of the trap.
     * @param width  width of the trap.
     * @param height height of the trap.
     */
    public TrapDamage(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "TrapDamage");
        this.trapDamageSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/trapDamageSound.mp3"));


        Texture trapTexture = new Texture("assets/Sprites/Pixel Crawler - FREE - 1.8/Environment/Dungeon Prison/Assets/Tiles32.png");

        int frameWidth = 32;
        int frameHeight = 32;

        isPassable = true;

        inactiveFrame = new TextureRegion(trapTexture, 4 * frameWidth, 10 * frameHeight, frameWidth, frameHeight);
        activeFrame = new TextureRegion(trapTexture, 4 * frameWidth, 9 * frameHeight, frameWidth, frameHeight);

        currentFrame = inactiveFrame;
        activated = false;
    }

    /**
     * Updates the state of the trap including interaction and rendering.
     *
     * @param delta time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {

        interact();

        batch.draw(
                currentFrame,
                gridX * Settings.SCALED_TILE_SIZE,
                gridY * Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE
        );
    }

    /**
     * Handles interaction with the trap.
     * activates (if not already activated) and damages the player ff the player touches the trap.
     */
    @Override
    protected void interact() {
        if (isPlayerTouching()) {
            trapDamageSound.play();
            if (!activated) {
                activated = true;
                currentFrame = activeFrame;
            }
            player.loseLife();
        }
    }
}

