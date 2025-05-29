package de.tum.cit.fop.maze.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import de.tum.cit.fop.maze.utility.ActorController;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.Settings;
import de.tum.cit.fop.maze.utility.WorldGenerator;

import java.util.HashMap;

/**
 * Abstract class responsible for an Actor in the game.
 * Deals with position, movement and animation.
 */
public abstract class Actor {

    protected int x;
    protected int y;
    protected float gridX;
    protected float gridY;

    protected int origX;
    protected int origY;
    protected int destX;
    protected int destY;

    protected float animation_timer;
    protected float animation_time = 0.15f;
    protected ActorController.ActorState actorState;

    protected float stateTime = 0f;

    protected HashMap<String, Animation<TextureRegion>> animations;
    protected Animation<TextureRegion> currentAnimation;
    public WorldGenerator world;

    /**
     * Constructor for creating an Actor at a certain position.
     * @param x  initial  x-coordinate
     * @param y initial y-coordinate
     * @param world world generator instance
     */
    public Actor(int x, int y, WorldGenerator world) {
        this.x = x;
        this.y = y;
        this.gridX = x;
        this.gridY = y;
        this.actorState = ActorController.ActorState.STANDING;
        this.animations = new HashMap<>();
        this.world = world;
    }

    /**
     * Default constructor for creating an actor.
     */
    public Actor() {
        this.actorState = ActorController.ActorState.STANDING;
        this.animations = new HashMap<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    protected ActorController.ActorState getState() {
        return actorState;
    }

    public float getStateTime() {
        return stateTime;
    }

    /**
     * Abstract method to set animation for a certain direction and state.
     * @param direction Direction the actor is facing.
     * @param state State of the actor.
     * @param animation Animation which has to be set.
     */
    public abstract void setAnimation(ActorController.Direction direction, ActorController.ActorState state, Animation<TextureRegion> animation);

    /**
     * Gets the animation of an actor for a specific direction and state.
     * @param direction Direction the actor is facing.
     * @param state State of the actor.
     * @return the respective animation, null if no animation is found.
     */
    public Animation<TextureRegion> getAnimation(ActorController.Direction direction, String state) {
        String key = direction.name() + "_" + state;
        return animations.getOrDefault(key, null);
    }

    /**
     * Updates the animation time based on the actor's type and state.
     * @param state Current state of the actor.
     */
    public void updateAnimationTime(ActorController.ActorState state) {
        if (this instanceof Enemy) {
            animation_time = 0.25f;
        } else if (this instanceof Player player) {
            if (player.isDebuffed()) {
                animation_time = 0.15f * 2f;
            } else {
                if (player.getGame().getCurrentLevel() != 0 && player.getGame().getCurrentLevel() != 5) {
                    animation_time = (state == ActorController.ActorState.RUNNING) ? 0.1f : 0.2f;
                } else {
                    animation_time = (state == ActorController.ActorState.RUNNING) ? 0.1f : 0.15f;
                }
            }
        }
    }

    /**
     * Renders the actor on the screen.
     * @param game instance of the maze runner game.
     * @param stateTime current state time for the animation.
     */
    public void render(MazeRunnerGame game, float stateTime) {
        float actorSize;
        if (this instanceof Enemy) {
            actorSize = 2f;
        } else
            actorSize = 1f;
        game.getSpriteBatch().draw(
                getCurrentAnimation().getKeyFrame(stateTime, true),
                gridX * Settings.SCALED_TILE_SIZE,
                gridY * Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE * actorSize
        );
    }

    /**
     * Checks if actor can move in the specified direction.
     * @param dx change in x-coordinate.
     * @param dy change in y-coordinate.
     * @param mapWidth width of the map.
     * @param mapHeight height of the map.
     * @return true if move was successful, false if actor can't move in that direction.
     */
    public boolean move(int dx, int dy, int mapWidth, int mapHeight) {

        int newX = x + dx;
        int newY = y + dy;


        if (newX >= mapWidth || newX < 0 || newY >= mapHeight || newY < 0) {
            return false;
        }

        if (!world.isPassable(newX, newY)) {
            return false;
        }

        if (dx!=0 || dy != 0 ) {
            if (this instanceof Enemy) {
                if (world.isOccupiedByEnemy(newX, newY)) {
                    return false;
                }
            }
        }

        if (actorState != ActorController.ActorState.STANDING) {
            return false;
        }

        origX = x;
        origY = y;
        destX = newX;
        destY = newY;

        x += dx;
        y += dy;

        animation_timer = 0f;
        actorState = ActorController.ActorState.WALKING;

        return true;
    }

    /**
     * Updates the actor's state and position.
     * @param delta Change in time(in seconds) since last update.
     */
    public void update(float delta) {
        if (actorState == ActorController.ActorState.WALKING || actorState == ActorController.ActorState.RUNNING) {
            stateTime += delta;
            animation_timer += delta;

            gridX = Interpolation.linear.apply(origX, destX, animation_timer / animation_time);
            gridY = Interpolation.linear.apply(origY, destY, animation_timer / animation_time);

            if (animation_timer >= animation_time) {
                finishMove();
            } else {
                actorState = ActorController.ActorState.WALKING;
            }
        }
    }

    /**
     * Finalizes the move of the actor.
     */
    private void finishMove() {
        gridX = destX;
        gridY = destY;
        actorState = ActorController.ActorState.STANDING;
        stateTime = 0f;
    }
}