package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;

/**
 * Responsible for the debuff trap in the game.
 * Handles behavior for a trap that applies a debuff to the player upon contact.
 */
public class TrapDebuff extends StaticObject {

    private TextureRegion currentFrame;
    private Music trapDebuffSound;

    /**
     * Constructor TrapDebuff object.
     *
     * @param game   main game instance.
     * @param x      x-coordinate of the trap.
     * @param y      y-coordinate of the trap.
     * @param width  width of the trap.
     * @param height height of the trap.
     */
    public TrapDebuff(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "TrapDebuff");

        this.trapDebuffSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/trapDebuffSound.mp3"));

        Texture trapTexture = new Texture("assets/Sprites/Provided/basictiles.png");

        int frameWidth = 16;
        int frameHeight = 16;

        isPassable = true;

        currentFrame = new TextureRegion(trapTexture, 3 * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
    }

    /**
     * Updates the state of the trap, including interaction and rendering.
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
     * If the player touches the trap, it applies a debuff to the player and plays a sound.
     */
    @Override
    protected void interact() {
        if (isPlayerTouching()) {
            trapDebuffSound.play();
            player.setDebuffed(true);

        }
    }
}
