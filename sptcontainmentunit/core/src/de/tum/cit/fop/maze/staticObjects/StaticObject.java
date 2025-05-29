package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.actors.Player;

/**
 * Abstract class for all static objects in the game.
 * Has common properties and methods for objects that don't move but can interact with the player.
 */
public abstract class StaticObject {

    protected final Batch batch;
    protected final MazeRunnerGame game;
    protected final int gridX, gridY, width, height;
    protected final String objectType;

    protected boolean isPassable;

    protected Player player;

    /**
     * Constructor for StaticObject.
     *
     * @param game       main game instance.
     * @param x          x-coordinate of the object.
     * @param y          y-coordinate of the object.
     * @param width      width of the object.
     * @param height     height of the object.
     * @param objectType type of the object.
     */
    public StaticObject(MazeRunnerGame game, int x, int y, int width, int height, String objectType) {
        this.game = game;
        this.gridX = x;
        this.gridY = y;
        this.width = width;
        this.height = height;
        this.objectType = objectType;
        this.batch = game.getSpriteBatch();
        this.player = game.player;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Determines if the player is touching this object.
     * @return true if player is touching the object, false if not.
     */
    protected boolean isPlayerTouching() {
        return(player.getX() >= gridX) &&
                (player.getX() < gridX + width) &&
                (player.getY() >= gridY) &&
                (player.getY() < gridY + height);
    }

    /**
     * Updates the state of the object.
     * @param delta time in seconds since the last frame.
     */
    public abstract void update(float delta);

    /**
     * Checks if the object is passable.
     * @return true if the object is passable, false if not.
     */
    public boolean isPassable() { return isPassable; }

    /**
     * Handles interaction with the object.
     * method implemented by subclasses to define interaction with player.
     */
    protected abstract void interact();
}
