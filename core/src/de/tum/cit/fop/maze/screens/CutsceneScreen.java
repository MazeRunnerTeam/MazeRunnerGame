package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Responsible for the cutscenes in the game.
 * Displays story text.
 */
public class CutsceneScreen implements Screen {
    private final MazeRunnerGame game;
    private final BitmapFont font;
    private String currentText = "";
    private float elapsedTime = 0;
    private int charIndex = 0;
    private int counter = 0;
    private boolean waitingForInput = false;
    private int count;

    /**
     * Constructor for CutsceneScreen.
     *
     * @param game main game instance.
     * @param count determines which set of cutscene text to display.
     */
    public CutsceneScreen(MazeRunnerGame game, int count) {
        this.game = game;
        this.count = count;
        this.font = new BitmapFont();
        this.font.getData().setScale(2f);

    }

    @Override
    public void show() {
    }

    /**
     * Renders the cutscene screen.
     *
     * @param delta time in seconds since last render.
     */
    @Override
    public void render(float delta) {
        if (count == 1) {
            ScreenUtils.clear(0, 0, 0, 1);
            game.getSpriteBatch().begin();
            if (!waitingForInput) {
                if (counter == 0) {
                    typeText("Press Space to go next\nPress Enter to skip", delta);
                } else if (counter == 1) {
                    typeText("March 14, 2134 @ 7:00", delta);
                } else if (counter == 2) {
                    typeText("Ugh! Another day of work at a highly secretive, military laboratory", delta);
                } else if (counter == 3) {
                    typeText("At first I joined because the pay was good,\nbut I did not know what I was getting myself into.", delta);
                } else if (counter == 4) {
                    typeText("They are trying to experiment on humans,\nand change them into something ... not human.", delta);
                } else if (counter == 5) {
                    typeText("I am just scared of any breakouts happening.\nI want to change jobs and transfer to a different lab...", delta);
                }  else if (counter == 6) {
                    typeText("...a safer lab.", delta);
                }
            }
        } else {
            ScreenUtils.clear(1, 1, 1, 1);
            game.getSpriteBatch().begin();
            font.setColor(1, 0, 0, 1);

            if (!waitingForInput) {
                if (counter == 0) {
                    typeText("...I guess I am dead...", delta);
                } else if (counter == 1) {
                    typeText("I thought there would be at least a timer for me to escape...", delta);
                } else if (counter == 2) {
                    typeText("That damn military general deceived me !!!", delta);
                } else if (counter == 3) {
                    typeText("At the very least, I saved the world...", delta);
                }
            }
        }

        font.draw(game.getSpriteBatch(), currentText, 100, Gdx.graphics.getHeight() / 2f);
        game.getSpriteBatch().end();


        if (waitingForInput && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            waitingForInput = false;
            charIndex = 0;
            elapsedTime = 0;
            counter++;
        }


        if (count == 1) {
            if (counter > 6) {
                game.setLevel(0);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setLevel(0);
            }
        } else if (count == 2) {
            if (counter > 3) {
                game.setScreen(new VictoryScreen(game));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setScreen(new VictoryScreen(game));
            }
        }
    }

    /**
     * Handles typing effect for a given text.
     */
    private void typeText(String fullText, float delta) {
        if (charIndex < fullText.length()) {
            elapsedTime += delta;
            if (elapsedTime > 0.03f) {
                currentText = fullText.substring(0, charIndex + 1);
                charIndex++;
                elapsedTime = 0;
            }
        } else {
            waitingForInput = true;
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * disposes resources used by the cutscene screen.
     */
    @Override
    public void dispose() {
        font.dispose();
    }

}