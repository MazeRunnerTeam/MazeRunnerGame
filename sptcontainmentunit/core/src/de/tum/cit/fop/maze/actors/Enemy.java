package de.tum.cit.fop.maze.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.utility.ActorController;
import de.tum.cit.fop.maze.utility.PathFinder;
import de.tum.cit.fop.maze.utility.WorldGenerator;

import java.util.List;

/**
 * Enemy class is responsible for the enemies in the game.
 * Extends the actor class and implements enemy animations and movement.
 */
public class Enemy extends Actor {

    private Player player;

    private Animation<TextureRegion> currentAnimation;
    private ActorController.Direction lastDirection;
    private ActorController.ActorState actorState;

    private final Animation<TextureRegion> walkingDownAnimation, walkingUpAnimation, walkingLeftAnimation, walkingRightAnimation;
    private TextureRegion standingDownFrame, standingUpFrame, standingLeftFrame, standingRightFrame;

    private Animation<TextureRegion> animation;

    private Texture npcSheet;

    private List<int[]> path;
    private int pathIndex;

    private int goalX, goalY, distanceX, distanceY;

    private final int aggroRange =5;

    /**
     * Constructor for Enemy at the specified position in the given world.
     *
     * @param x initial x-coordinate of the enemy.
     * @param y initial y-coordinate of the enemy.
     * @param world WorldGenerator instance representing the game world.
     */
    public Enemy(int x, int y, WorldGenerator world) {
        super(x, y, world);

        player = world.getPlayer();

        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 3;

        npcSheet = new Texture(Gdx.files.internal("Sprites/Provided/mobs.png"));

        for (int col = 0; col < animationFrames; col++) {
            walkDownFrames.add(new TextureRegion(npcSheet, col * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
            walkLeftFrames.add(new TextureRegion(npcSheet, col * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
            walkRightFrames.add(new TextureRegion(npcSheet, col * frameWidth, 6* frameHeight, frameWidth, frameHeight));
            walkUpFrames.add(new TextureRegion(npcSheet, col * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
        }

        standingDownFrame = new TextureRegion(npcSheet, 1, 4 * frameHeight, frameWidth, frameHeight);
        standingLeftFrame = new TextureRegion(npcSheet, 1, 5 * frameHeight, frameWidth, frameHeight);
        standingRightFrame = new TextureRegion(npcSheet, 1, 6 * frameHeight, frameWidth, frameHeight);
        standingUpFrame = new TextureRegion(npcSheet, 1, 7 * frameHeight, frameWidth, frameHeight);


        walkingDownAnimation = new Animation<>(0.06f, walkDownFrames, Animation.PlayMode.LOOP);
        walkingUpAnimation = new Animation<>(0.06f, walkUpFrames, Animation.PlayMode.LOOP);
        walkingLeftAnimation = new Animation<>(0.06f, walkLeftFrames, Animation.PlayMode.LOOP);
        walkingRightAnimation = new Animation<>(0.06f, walkRightFrames, Animation.PlayMode.LOOP);

        currentAnimation = new Animation<>(Float.MAX_VALUE, standingDownFrame);
        path = null;
        pathIndex = 0;
        goalX = -1;
        goalY = -1;
    }

    /**
     * Updates the enemy's path based on the player's position.
     *
     * @param playerX x-coordinate of the player.
     * @param playerY y-coordinate of the player.
     */
    public void updatePath(int playerX, int playerY) {
        updateDistanceToPlayer();

        boolean isPlayerInRange = distanceX <= aggroRange && distanceY <= aggroRange;

        if (isPlayerInRange) {
            path = PathFinder.findPath(this.getX(), this.getY(), playerX, playerY, world);
            pathIndex = 0;
        } else  {
            setRandomGoal();
        }
    }

    /**
     * Makes the enemy follow its current path.
     *
     * @return The direction the enemy moved in, null if no movement occurred.
     */
    public ActorController.Direction followPath() {
        if (player == null) return null;

        if (path == null || pathIndex >= path.size()) {
            updatePath(player.getX(), player.getY());
        }

        if (path != null && pathIndex < path.size() && getState() == ActorController.ActorState.STANDING) {

            int[] nextStep = path.get(pathIndex);
            int dx = nextStep[0] - this.getX();
            int dy = nextStep[1] - this.getY();

            ActorController.Direction direction = null;
            if (dy > 0) direction = ActorController.Direction.W;
            else if (dy < 0) direction = ActorController.Direction.S;
            else if (dx < 0) direction = ActorController.Direction.A;
            else if (dx > 0) direction = ActorController.Direction.D;

            if (this.move(dx, dy, WorldGenerator.getMapWidth(), WorldGenerator.getMapHeight())) {
                pathIndex++;
                updateDistanceToPlayer();
                return direction;
            } else {
                path = null;
            }
        }
        return null;
    }

    /**
     * Sets a random goal for the enemy to move towards.
     */
    private void setRandomGoal() {
        do {
            goalX = (int) (Math.random() * WorldGenerator.getMapWidth());
            goalY = (int) (Math.random() * WorldGenerator.getMapHeight());
        } while (!world.isPassable(goalX, goalY));

        path = PathFinder.findPath(this.getX(), this.getY(), goalX, goalY, world);
        pathIndex = 0;
    }

    /**
     * Updates current distance to player
     */
    private void updateDistanceToPlayer() {
        if (player != null) {
            distanceX = Math.abs(player.getX() - this.getX());
            distanceY = Math.abs(player.getY() - this.getY());
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Sets the animation based on the direction and state of the enemy.
     *
     * @param direction direction the enemy is facing.
     * @param state current state of the enemy.
     * @param animation animation to be set (not used in this implementation.
     */
    @Override
    public void setAnimation(ActorController.Direction direction, ActorController.ActorState state, Animation<TextureRegion> animation) {
        this.lastDirection = direction;
        this.actorState = state;

        if (state == ActorController.ActorState.WALKING || state == ActorController.ActorState.RUNNING) {
            switch (direction) {
                case W -> currentAnimation = walkingUpAnimation;
                case S -> currentAnimation = walkingDownAnimation;
                case A -> currentAnimation = walkingLeftAnimation;
                case D -> currentAnimation = walkingRightAnimation;
            }
        } else if (state == ActorController.ActorState.STANDING) {
            switch (direction) {
                case W -> currentAnimation = new Animation<>(Float.MAX_VALUE, standingUpFrame);
                case S -> currentAnimation = new Animation<>(Float.MAX_VALUE, standingDownFrame);
                case A -> currentAnimation = new Animation<>(Float.MAX_VALUE, standingLeftFrame);
                case D -> currentAnimation = new Animation<>(Float.MAX_VALUE, standingRightFrame);
            }
        }
    }
}

