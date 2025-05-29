package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.tum.cit.fop.maze.actors.Player;
import de.tum.cit.fop.maze.actors.Boss;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Responsible for the battle screen where the player fights against a boss.
 */
public class BattleScreen implements Screen {
    private final MazeRunnerGame game;
    private Texture backgroundTexture, bossTexture;

    private final Player player;
    private final Boss boss;

    private Label playerHealthLabel, playerDamageLabel, bossHealthLabel, bossDamageLabel;
    private int playerHealth, bossHealth, playerDamage, bossDamage;

    private Stage stage;

    /**
     * Constructs a new BattleScreen.
     * @param game main game instance.
     * @param player player instance.
     * @param boss boss instance to battle.
     */
    public BattleScreen(MazeRunnerGame game, Player player, Boss boss) {
        this.game = game;
        this.player = player;
        this.boss = boss;

        this.playerHealth = player.getLives() * 2;
        this.bossHealth = game.getCurrentLevel() * 15;
        this.playerDamage = game.getCurrentLevel() * 2;
        this.bossDamage = 5;

        backgroundTexture = new Texture(Gdx.files.internal("assets/Sprites/Boss Arena.png"));
        bossTexture = new Texture(Gdx.files.internal("assets/Sprites/BossBattle.png"));

        stage = new Stage(new FitViewport(1280, 720));
    }

    /**
     * Handles the UI elements for the battle screen.
     * Called when BattleScreen becomes the current screen.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        game.playMusic("assets/Sounds/Background Music/BattleScreenMusic.mp3");

        Table statsTable = new Table();
        statsTable.setFillParent(true);
        statsTable.align(Align.left | Align.center);
        stage.addActor(statsTable);

        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.align(Align.center | Align.bottom);
        stage.addActor(buttonTable);

        playerHealthLabel = new Label("Player Health: " + playerHealth, game.getSkin());
        playerDamageLabel = new Label("Player Damage: " + playerDamage, game.getSkin());
        bossHealthLabel = new Label("Boss Health: " + bossHealth, game.getSkin());
        bossDamageLabel = new Label("Boss Damage: " + bossDamage, game.getSkin());

        playerHealthLabel.setColor(Color.GREEN);
        playerDamageLabel.setColor(Color.GREEN);
        bossHealthLabel.setColor(Color.RED);
        bossDamageLabel.setColor(Color.RED);

        statsTable.add(playerHealthLabel).padBottom(20).padRight(20);
        statsTable.add(playerDamageLabel).padBottom(20).row();
        statsTable.add(bossHealthLabel).padRight(20);
        statsTable.add(bossDamageLabel).row();

        TextButton attackButton = new TextButton("Attack", game.getSkin());
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attack();
            }
        });

        TextButton instaKillButton = new TextButton("Kill (For Tutors)", game.getSkin());
        instaKillButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                killBoss();
            }
        });

        TextButton chargeButton = new TextButton("Charge", game.getSkin());
        chargeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                charge();
            }
        });

        TextButton healButton = new TextButton("Heal", game.getSkin());
        healButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                heal();
            }
        });

        buttonTable.add(attackButton).pad(10);
        buttonTable.add(instaKillButton).pad(10).row();
        buttonTable.add(chargeButton).pad(10);
        buttonTable.add(healButton).pad(10);
    }

    /**
     * Renders the battle screen.
     * @param delta time in seconds since last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().setProjectionMatrix(stage.getCamera().combined);
        game.getSpriteBatch().begin();
        {
            game.getSpriteBatch().draw(backgroundTexture,
                    0, 0,
                    stage.getViewport().getWorldWidth(),
                    stage.getViewport().getWorldHeight());

            float bossWidth  = 300f;
            float bossHeight = 300f;
            float bossX = stage.getViewport().getWorldWidth() - bossWidth - 75;
            float bossY = 375;
            game.getSpriteBatch().draw(bossTexture, bossX, bossY, bossWidth, bossHeight);
        }
        game.getSpriteBatch().end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when the screen should resize itself.
     * @param width new width of the screen.
     * @param height new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {
    }

    @Override public void resume() {
    }

    @Override public void hide() {
    }

    /**
     * Disposes resources used by this screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        bossTexture.dispose();
    }

    /**
     * Handles the player's attack action.
     */
    private void attack() {
        bossHealth -= playerDamage;
        bossHealthLabel.setText("Boss Health: " + bossHealth);

        bossMove();
        playerHealthLabel.setText("Player Health: " + playerHealth);
        checkBattleEnd();
    }

    /**
     * Instantly kills the boss (for testing purposes).
     */
    private void killBoss() {
        bossHealth = 0;
        checkBattleEnd();
    }
    /**
     * Handles the player's charge action to deal increased damage.
     */
    private void charge() {
        playerDamage += 7;
        playerDamageLabel.setText("Player Damage: " + playerDamage);

        bossMove();
        checkBattleEnd();
    }

    /**
     * Handles the player's healing move
     */
    private void heal() {
        playerHealth += 15;
        playerHealthLabel.setText("Player Health: " + playerHealth);

        bossMove();
        checkBattleEnd();
    }

    /**
     * Handles the boss's turn in the battle.
     */
    private void bossMove() {
        double bossMove = Math.random();
        if (bossMove <= 0.25) {
            playerHealth -= bossDamage;
            playerHealthLabel.setText("Player Health: " + playerHealth);
        } else if (bossMove <= 0.75) {
            bossDamage += 5;
            bossDamageLabel.setText("Boss Damage: " + bossDamage);
        } else {
            bossHealth += 5;
            bossHealthLabel.setText("Boss Health: " + bossHealth);
        }
    }
    /**
     * Checks if the battle has ended and handles the outcome based on who was defeated first.
     */
    private void checkBattleEnd() {
        if (bossHealth <= 0) {
            player.addPoints(500);
            boss.killBoss();
            game.setScreen(game.getGameScreen());
        } else if (playerHealth <= 0) {
            game.setScreen(new DefeatScreen(game));
        }
    }
}
