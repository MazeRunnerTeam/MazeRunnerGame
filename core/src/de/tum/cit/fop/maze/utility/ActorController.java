package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import de.tum.cit.fop.maze.actors.Actor;
import de.tum.cit.fop.maze.actors.Enemy;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.screens.BattleScreen;
import de.tum.cit.fop.maze.screens.GameScreen;
import de.tum.cit.fop.maze.staticObjects.DialogueTile;
import de.tum.cit.fop.maze.staticObjects.StaticObject;

/**
 * Controls the movement and state of actors in the game.
 * Handles input processing, movement and entity collision.
 */
public class ActorController extends InputAdapter {

    private Actor actor;
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private float moveDelay = 0f;
    private float timeSinceLastMove = 0f;

    private Direction lastDirection = Direction.S;
    private ActorState state = ActorState.STANDING;
    private boolean isRunning = false;

    private boolean disableMovement = false;

    private final GameScreen gameScreen;

    /**
     * Enum for the directions an actor can face.
     */
    public enum Direction {
        W,
        S,
        A,
        D
    }

    /**
     * Enum for the states of an actor.
     */
    public enum ActorState {
        STANDING,
        WALKING,
        RUNNING
    }

    /**
     * Constructor for ActorController.
     * @param actor actor to be controlled.
     * @param gameScreen current game screen.
     */
    public ActorController(Actor actor, GameScreen gameScreen) {
        this.actor = actor;
        this.gameScreen = gameScreen;
    }


    /**
     * Handles key press cases.
     * @param keycode code of the key pressed.
     * @return true if the input was pressed, if not false.
     */
    @Override
    public boolean keyDown(int keycode) {

        if (disableMovement) {
            return false;
        }

        if (!(actor instanceof Enemy)) {
            switch (keycode) {
                case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> isRunning = true;
                case Input.Keys.W, Input.Keys.UP  -> {
                    moveUp = true;
                    lastDirection = Direction.W;
                }
                case Input.Keys.S, Input.Keys.DOWN-> {
                    moveDown = true;
                    lastDirection = Direction.S;
                }
                case Input.Keys.A, Input.Keys.LEFT -> {
                    moveLeft = true;
                    lastDirection = Direction.A;
                }
                case Input.Keys.D, Input.Keys.RIGHT -> {
                    moveRight = true;
                    lastDirection = Direction.D;
                }
            }
            updateState();
        }
        return true;
    }

    /**
     * Handles key release cases.
     * @param keycode code of the key released.
     * @return true if the input was processed, if not false.
     */
    @Override
    public boolean keyUp(int keycode) {

        if (actor instanceof Player) {
            switch (keycode) {
                case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> isRunning = false;
                case Input.Keys.W, Input.Keys.UP -> moveUp = false;
                case Input.Keys.S, Input.Keys.DOWN -> moveDown = false;
                case Input.Keys.A, Input.Keys.LEFT -> moveLeft = false;
                case Input.Keys.D, Input.Keys.RIGHT -> moveRight = false;
            }

            if (moveUp) {
                lastDirection = Direction.W;
            } else if (moveDown) {
                lastDirection = Direction.S;
            } else if (moveLeft) {
                lastDirection = Direction.A;
            } else if (moveRight) {
                lastDirection = Direction.D;
            } else {
                state = ActorState.STANDING; 
            }

            updateState();
        }
        return true;
    }

    /**
     * Updates the state of the actor
     */
    private void updateState() {
        if (actor instanceof Enemy) {
            state = ActorState.WALKING;
        }

        if (actor instanceof Player player) {
            if (player.isDebuffed()) {
                isRunning = false;
                state = ActorState.WALKING;
                actor.setAnimation(lastDirection, state, actor.getCurrentAnimation());
                return;
            }
        }

        if (moveUp || moveDown || moveLeft || moveRight) {
            if (isRunning) {
                state = ActorState.RUNNING;
            } else {
                state = ActorState.WALKING;
            }
        } else {
            state = ActorState.STANDING;
        }

        actor.setAnimation(lastDirection, state, actor.getCurrentAnimation());
    }

    /**
     * Updates actor's position and state.
     * @param delta time in seconds since last frame.
     */
    public void update(float delta) {

        timeSinceLastMove += delta;
        actor.updateAnimationTime(state);

        if (timeSinceLastMove >= moveDelay) {
            if (actor instanceof Enemy enemy) {
                ActorController.Direction newDirection = enemy.followPath();
                if (newDirection != null) {
                    lastDirection = newDirection;
                    state = ActorState.WALKING;
                } else {
                    state = ActorState.STANDING;
                }
                actor.setAnimation(lastDirection, state, actor.getCurrentAnimation());

            } else {
                updatePlayerMovement();
                checkCollisions();
            }
            timeSinceLastMove = 0f;
        }
    }

    /**
     * Updates player's movement based on last input.
     */
    private void updatePlayerMovement() {

        int dx = 0, dy = 0;
        if (moveUp && lastDirection == Direction.W) dy += 1;
        if (moveDown && lastDirection == Direction.S) dy -= 1;
        if (moveLeft && lastDirection == Direction.A) dx -= 1;
        if (moveRight && lastDirection == Direction.D) dx += 1;

        actor.move(dx, dy, WorldGenerator.getMapWidth(), WorldGenerator.getMapHeight());
    }

    /**
     * Checks for tile specific actions.
     */
    private void checkTileTriggers() {
        int px = actor.getX();
        int py = actor.getY();

        StaticObject object = gameScreen.getWorldGenerator().getStaticObjectAt(px, py);

        if (object instanceof DialogueTile dialogueTile && !dialogueTile.isTriggered()) {
            dialogueTile.setTriggered(true);
            gameScreen.getDialogueOverlay().startDialogue();
        }
    }

    /**
     * Checks for collisions with enemies and triggers actions accordingly.
     */
    private void checkCollisions() {
        if (actor instanceof Player player) {
            for (Enemy enemy : player.world.getEnemies()) {
                if (player.getX() == enemy.getX() && player.getY() == enemy.getY()) {
                    player.loseLife();
                }
            }
            if (player.world.getBoss() != null && player.getX() == player.world.getBoss().getX() && player.getY() == player.world.getBoss().getY() && gameScreen.getBoss().isAlive()) {
                gameScreen.getPlayerController().resetMovement();
                gameScreen.getGame().setScreen(new BattleScreen(gameScreen.getGame(), gameScreen.getPlayer(), gameScreen.getBoss()));
            }
            checkTileTriggers();
        }
    }

    public ActorState getState() {
        return state;
    }

    public void setDisableMovement(boolean disableMovement) {
        this.disableMovement = disableMovement;
    }

    /**
     * Resets all movement flags and changes actor state to standing.
     */
    public void resetMovement() {
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        isRunning = false;
        state = ActorState.STANDING;
        actor.setAnimation(lastDirection, state, actor.getCurrentAnimation());
    }
}

