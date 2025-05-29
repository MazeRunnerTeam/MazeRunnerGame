package de.tum.cit.fop.maze.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.utility.ActorController;
import de.tum.cit.fop.maze.utility.Settings;
import de.tum.cit.fop.maze.utility.WorldGenerator;

/**
 * Boss class is responsible for the Boss characters in the game.
 * Extends the Actor class for the animations.
 */
public class Boss extends Actor {
    private Animation<TextureRegion> currentAnimation;
    private ActorController.Direction lastDirection;
    private ActorController.ActorState actorState;

    private final Animation<TextureRegion> walkingDownAnimation;
    private final Animation<TextureRegion> walkingUpAnimation;
    private final Animation<TextureRegion> walkingLeftAnimation;
    private final Animation<TextureRegion> walkingRightAnimation;

    private final Animation<TextureRegion> runningDownAnimation;
    private final Animation<TextureRegion> runningUpAnimation;
    private final Animation<TextureRegion> runningLeftAnimation;
    private final Animation<TextureRegion> runningRightAnimation;

    private final Animation<TextureRegion> standingDownAnimation;
    private final Animation<TextureRegion> standingUpAnimation;
    private final Animation<TextureRegion> standingLeftAnimation;
    private final Animation<TextureRegion> standingRightAnimation;

    private Animation<TextureRegion> animation;

    private Texture bossWalkDownSheet;
    private Texture bossWalkUpSheet;
    private Texture bossWalkLeftSheet;
    private Texture bossWalkRightSheet;

    private Texture BossStandDownSheet;
    private Texture BossStandUpSheet;
    private Texture BossStandLeftSheet;
    private Texture BossStandRightSheet;

    private boolean isAlive = true;

    /**
     * Constructor for a Boss.
     * Loads texture sheets and initializes animations.
     * @param x initial x-coordinate of the boss.
     * @param y initial y-coordinate of the boss.
     * @param world WorldGenerator instance.
     */
    public Boss(int x, int y, WorldGenerator world) {
        super(x, y, world);

        bossWalkDownSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Down/Png/AncientSkeletonDownWalk.png"));
        bossWalkUpSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Up/Png/AncientSkeletonUpWalk.png"));
        bossWalkLeftSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Left/Png/AncientSkeletonLeftWalk.png"));
        bossWalkRightSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Right/Png/AncientSkeletonRightWalk.png"));

        BossStandDownSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Down/Png/AncientSkeletonDownIdle.png"));
        BossStandUpSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Up/Png/AncientSkeletonUpIdle.png"));
        BossStandLeftSheet =  new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Left/Png/AncientSkeletonLeftIdle.png"));
        BossStandRightSheet = new Texture(Gdx.files.internal("Sprites/BOSS_PACK/Right/Png/AncientSkeletonRightIdle.png"));


        int frameWidth = 80;
        int frameHeight = 80;
        int animationFrames = 8;

        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);

        Array<TextureRegion> standingDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> standingUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> standingLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> standingRightFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            walkDownFrames.add(new TextureRegion(bossWalkDownSheet, col * frameWidth, 0, frameWidth, frameHeight));
            walkLeftFrames.add(new TextureRegion(bossWalkLeftSheet, col * frameWidth, 0, frameWidth, frameHeight));
            walkRightFrames.add(new TextureRegion(bossWalkRightSheet, col * frameWidth, 0, frameWidth, frameHeight));
            walkUpFrames.add(new TextureRegion(bossWalkUpSheet, col * frameWidth, 0, frameWidth, frameHeight));
            if (col != 7) {
                standingDownFrames.add(new TextureRegion(BossStandDownSheet, col * frameWidth, 0, frameWidth, frameHeight));
                standingUpFrames.add(new TextureRegion(BossStandUpSheet, col * frameWidth, 0, frameWidth, frameHeight));
                standingRightFrames.add(new TextureRegion(BossStandRightSheet, col * frameWidth, 0, frameWidth, frameHeight));
                standingLeftFrames.add(new TextureRegion(BossStandLeftSheet, col * frameWidth, 0, frameWidth, frameHeight));
            }
        }

        walkingDownAnimation = new Animation<>(0.1f, walkDownFrames, Animation.PlayMode.LOOP);
        walkingUpAnimation = new Animation<>(0.1f, walkUpFrames, Animation.PlayMode.LOOP);
        walkingLeftAnimation = new Animation<>(0.1f, walkLeftFrames, Animation.PlayMode.LOOP);
        walkingRightAnimation = new Animation<>(0.1f, walkRightFrames, Animation.PlayMode.LOOP);

        runningDownAnimation = new Animation<>(0.1f, walkDownFrames, Animation.PlayMode.LOOP);
        runningUpAnimation = new Animation<>(0.1f, walkUpFrames, Animation.PlayMode.LOOP);
        runningLeftAnimation = new Animation<>(0.1f, walkLeftFrames, Animation.PlayMode.LOOP);
        runningRightAnimation = new Animation<>(0.1f, walkRightFrames, Animation.PlayMode.LOOP);

        standingDownAnimation = new Animation<>(0.1f, standingDownFrames, Animation.PlayMode.LOOP);
        standingLeftAnimation = new Animation<>(0.1f, standingLeftFrames, Animation.PlayMode.LOOP);
        standingRightAnimation = new Animation<>(0.1f, standingRightFrames, Animation.PlayMode.LOOP);
        standingUpAnimation = new Animation<>(0.1f, standingUpFrames, Animation.PlayMode.LOOP);


        currentAnimation = standingDownAnimation;
    }

    /**
     * Updates Boss's state.
     * @param delta Change in time(in seconds) since last update.
     */
    @Override
    public void update(float delta) {
        if (isAlive) {
            animation_timer += delta;
            stateTime += delta;
            super.update(delta);
        }
    }

    /**
     * Gets current animation of the boss.
     * @return the current Animation<TextureRegion> object.
     */
    @Override
    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    @Override
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
                case W -> currentAnimation = standingUpAnimation;
                case S -> currentAnimation = standingDownAnimation;
                case A -> currentAnimation = standingLeftAnimation;
                case D -> currentAnimation = standingRightAnimation;
            }
            stateTime = 0;
        }
    }



    @Override
    public void render(MazeRunnerGame game, float stateTime) {
        game.getSpriteBatch().draw(
                currentAnimation.getKeyFrame(this.stateTime, true),
                (gridX - 1) * Settings.SCALED_TILE_SIZE,
                gridY * Settings.SCALED_TILE_SIZE,
                Settings.SCALED_TILE_SIZE * 3f,
                Settings.SCALED_TILE_SIZE * 3f);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void killBoss() {
        this.isAlive = false;
    }
}