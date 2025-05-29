package de.tum.cit.fop.maze.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.screens.DefeatScreen;
import de.tum.cit.fop.maze.utility.ActorController;
import de.tum.cit.fop.maze.utility.Settings;
import de.tum.cit.fop.maze.utility.WorldGenerator;

/**
 * Responsible for the main character in the game.
 * Extends the actor class.
 * Implements player attributes, movement and animation.
 */
public class Player extends Actor{

    private MazeRunnerGame game;

    private Animation<TextureRegion> currentAnimation;
    private ActorController.Direction lastDirection;
    private ActorController.ActorState actorState;

    private final Animation<TextureRegion> walkingDownAnimation, walkingUpAnimation, walkingLeftAnimation, walkingRightAnimation;
    private final Animation<TextureRegion>  runningDownAnimation, runningUpAnimation, runningLeftAnimation,runningRightAnimation;
    private final TextureRegion standingDownFrame, standingUpFrame, standingLeftFrame, standingRightFrame;

    int frameWidth = 64;
    int frameHeight = 60;
    int animationFrames = 12;

    private int lives = 10;
    private int maxLives = 10;

    private int iFrames = 0;
    private int points = 0;

    private boolean hasKey = false;
    private Array<String> keyList = new Array<>();
    private boolean temporaryShieldActive = false;
    private float shieldTimer = 0;
    private boolean isDebuffed = false;
    private float debuffTimer = 0;

    private Music hurtSound, keySound;

    /**
     * Constructs the player in the game.
     * Initializes the texture and the animations.
     * @param game main game instance.
     */
    public Player(MazeRunnerGame game) {

        this.game = game;

        this.lastDirection = ActorController.Direction.S;
        this.actorState = ActorController.ActorState.STANDING;

        this.hurtSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/hurtSound.mp3"));
        this.keySound =  Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/Key_collection.mp3"));

        Texture walkSheet = new Texture(Gdx.files.internal("Sprites/walkcyclevarious.png"));

        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            if (col < 3) {walkUpFrames.add(new TextureRegion(walkSheet, col * frameWidth + 20, 6 * frameHeight , frameWidth - 20, frameHeight));}
            else if (col >=3 && col < 6) {walkRightFrames.add(new TextureRegion(walkSheet, col * frameWidth + 20, 6 * frameHeight, frameWidth - 20, frameHeight));}
            else if (col >=6 && col < 9) {walkDownFrames.add(new TextureRegion(walkSheet, col * frameWidth + 20, 6 * frameHeight , frameWidth - 20, frameHeight));}
            else {walkLeftFrames.add(new TextureRegion(walkSheet, col * frameWidth + 20, 6 * frameHeight, frameWidth - 20, frameHeight));}
        }

        walkingDownAnimation = new Animation<>(0.1f, walkDownFrames, Animation.PlayMode.LOOP);
        walkingUpAnimation = new Animation<>(0.1f, walkUpFrames, Animation.PlayMode.LOOP);
        walkingLeftAnimation = new Animation<>(0.1f, walkLeftFrames, Animation.PlayMode.LOOP);
        walkingRightAnimation = new Animation<>(0.1f, walkRightFrames, Animation.PlayMode.LOOP);

        runningDownAnimation = new Animation<>(0.06f, walkDownFrames, Animation.PlayMode.LOOP);
        runningUpAnimation = new Animation<>(0.06f, walkUpFrames, Animation.PlayMode.LOOP);
        runningLeftAnimation = new Animation<>(0.06f, walkLeftFrames, Animation.PlayMode.LOOP);
        runningRightAnimation = new Animation<>(0.06f, walkRightFrames, Animation.PlayMode.LOOP);

        standingDownFrame = new TextureRegion(walkSheet, 7*frameWidth + 20, 6*frameHeight, frameWidth - 20, frameHeight);
        standingRightFrame = new TextureRegion(walkSheet, 4*frameWidth + 20, 6*frameHeight, frameWidth - 20, frameHeight);
        standingUpFrame = new TextureRegion(walkSheet, 1*frameWidth + 20, 6*frameHeight, frameWidth - 20, frameHeight);
        standingLeftFrame = new TextureRegion(walkSheet, 10*frameWidth + 20, 6 * frameHeight, frameWidth - 20, frameHeight);

        currentAnimation = new Animation<>(Float.MAX_VALUE, standingDownFrame);
    }

    /**
     * Sets player animation depending on the direction and state of the player.
     * @param direction Direction the actor is facing.
     * @param state State of the actor.
     * @param animation Animation which has to be set.
     */
    public void setAnimation(ActorController.Direction direction, ActorController.ActorState state, Animation<TextureRegion> animation) {
        this.lastDirection = direction;
        this.actorState = state;

        if (state == ActorController.ActorState.WALKING) {
            switch (direction) {
                case W -> currentAnimation = walkingUpAnimation;
                case S -> currentAnimation = walkingDownAnimation;
                case A -> currentAnimation = walkingLeftAnimation;
                case D -> currentAnimation = walkingRightAnimation;
            }
        } else if (state == ActorController.ActorState.RUNNING){
            switch (direction) {
                case W -> currentAnimation = runningUpAnimation;
                case S -> currentAnimation = runningDownAnimation;
                case A -> currentAnimation = runningLeftAnimation;
                case D -> currentAnimation = runningRightAnimation;
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

    /**
     * Renders the player on teh screen.
     * @param game instance of the maze runner game.
     * @param stateTime current state time for the animation.
     */
    @Override
    public void render(MazeRunnerGame game, float stateTime) {
        if ((iFrames > 5 && iFrames <= 16) || (iFrames > 25 && iFrames < 36) || (iFrames > 45 && iFrames < 56) || (iFrames > 65 && iFrames < 76)) {
            return;
        }

        game.getSpriteBatch().draw(
                this.currentAnimation.getKeyFrame(stateTime, true),
                this.gridX * Settings.SCALED_TILE_SIZE,
                this.gridY * Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE * 2f,
                Settings.SCALED_TILE_SIZE * 2f
        );
    }

    /**
     * Updates the player's state(checks for health/shields/debuffs).
     * @param delta Change in time(in seconds) since last update.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (this.iFrames > 0) {iFrames--;}

        if(!isAlive()){
            game.setScreen(new DefeatScreen(game));
        }

        if (temporaryShieldActive) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) {
                temporaryShieldActive = false;
            }
        }

        if (isDebuffed) {
            debuffTimer -= delta;
            if (debuffTimer <= 0) {
                isDebuffed = false;
            }
        }
    }


    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    public int getLives() {
        return lives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getKeyListSize() {
        return keyList.size;
    }

    public MazeRunnerGame getGame() {
        return game;
    }

    /**
     * Removes health from player if player has no shield.
     * Reduces the amount of points the player has.
     */
    public void loseLife() {
        if (iFrames > 0) {
            return;
        }
        if (!temporaryShieldActive) {
            lives--;
            hurtSound.play();
            iFrames = 90;
            addPoints(-50);
        }
    }

    /**
     * Increases player's health if not at max health.
     */
    public void gainLife() {
        if (lives < maxLives) {
            lives++;
        }
    }

    /**
     * Checks if player is still alive.
     * @return true if player has more than 0 lives, false if not.
     */
    public boolean isAlive() { return lives > 0; }

    /**
     * Checks if player has a temporary shield.
     * @return true if player has a temporary shield, false if not.
     */
    public boolean hasTemporaryShield() {
        return temporaryShieldActive;
    }

    /**
     * Activates temporary shield for the player.
     */
    public void activateTemporaryShield() {
        temporaryShieldActive = true;
        shieldTimer = 3f;
    }

    /**
     * Applies debuff to the player.
     * @param debuffed true to apply debuff, false if not.
     */
    public void setDebuffed(boolean debuffed) {
        if (debuffed) {
            isDebuffed = true;
            debuffTimer = 2f;
        }
    }

    /**
     * Checks if player has a debuff.
     * @return true if player is debuff, false if not.
     */
    public boolean isDebuffed() {
        return isDebuffed;
    }

    /**
     * Adds key to player's inventory
     */
    public void collectKey() {
        hasKey = true;
        keySound.play();
        addPoints(100);
    }

    /**
     * Checks if player has collected the key.
     * @return true if player has collected the key, false if not.
     */
    public boolean hasKey() {
        return hasKey;
    }

    /**
     * Removes key from the player's inventory.
     */
    public void removeKey() {
        hasKey = false;
    }

    /**
     * Adds key for a specific level to the key list.
     * @param level level for which the key is needed.
     */
    public void addLevelKeyToList(int level) {
        if (level > 0 && level <= 5) {
            keyList.add("Key " + level);
        }
    }


    /**
     * Spawns the player at the specified coordinates in the specific world.
     * @param x x-coordinate for the player to spawn.
     * @param y y-coordinate for the player to spawn.
     * @param world worldgenerator instance.
     */
    public void spawnPlayer(int x, int y, WorldGenerator world) {
        this.x = x;
        this.gridX = x;
        this.y = y;
        this.gridY = y;
        this.world = world;
    }

    /**
     * Spawns the player at the specified coordinates in the specific world.
     * @param points The amount of points to be added to the player.
     */
    public void addPoints(int points) {
        this.points += points;
    }

    public int getPoints() {
        return this.points;
    }
}