package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The VictoryScreen class is responsible for the screen displayed when the player successfully completes the game.
 * It displays a message and has an option to return to the main menu.
 * This screen implements the {@link Screen} interface from libGDX.
 */
public class VictoryScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Texture background;


    /**
     * Constructs a new VictoryScreen.
     *
     * @param game main game instance to access global methods and resources.
     */
    public VictoryScreen(MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.background = new Texture(Gdx.files.internal("assets/Sprites/heaven.jpg"));
    }

    /**
     * Handles input processing and GUI for the settings screen.
     * Displays a message and has a button to return to the main menu.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table tableText = new Table();
        tableText.setFillParent(true);

        Label victoryLabel = new Label("You have successfully contained the virus\n and saved humanity!\n Was it worth it, though?", game.getSkin());
        victoryLabel.setFontScale(1.5f);
        victoryLabel.setColor(Color.MAROON);

        TextButton menuButton = new TextButton("Return to Menu", game.getSkin());

        menuButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new PauseScreen(game,null));
            }
        });
        tableText.add(victoryLabel).expandX().center().row();
        tableText.add(menuButton).pad(20).right().bottom().row();


        Label pointsLabel = new Label("You have achieved: " + game.player.getPoints() + " Points!", game.getSkin());
        pointsLabel.setFontScale(1.5f);
        pointsLabel.setColor(Color.GOLD);

        tableText.add(pointsLabel).expandX().center().row();

        stage.addActor(tableText);
    }

    /**
     * Renders the victory screen.
     * @param delta Change in time(in seconds) since last frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float screenSize=(float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        float textureSize=(float)background.getWidth()/(float)background.getHeight();

        if(screenSize>textureSize){
            height = width/textureSize;
        }
        else {
            width = height*textureSize;
        }

        float screenX = (Gdx.graphics.getWidth()-width)/2;
        float screenY = (Gdx.graphics.getHeight()-height)/2;

        game.getSpriteBatch().draw(background, screenX, screenY, width, height);
        game.getSpriteBatch().end();

        stage.act();
        stage.draw();
    }

    /**
     * Resizes viewport according to window size.
     * @param i new window width.
     * @param i1 new window height.
     */
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update( i, i1, true);
    }

    /**
     * method of interface Screen
     */
    @Override
    public void pause() {

    }

    /**
     * method of interface Screen
     */
    @Override
    public void resume() {

    }

    /**
     * method of interface Screen
     */
    @Override
    public void hide() {

    }

    /**
     * Disposes resources used for the screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
