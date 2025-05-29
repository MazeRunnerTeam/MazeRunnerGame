package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Responsible for the dialogue overlay in the game.
 * Handles displaying dialogue text, progressing through dialogue, and dialogue UI.
 */
public class DialogueOverlay {
    private final Stage stage;
    private final Label dialogueLabel;
    private final Window dialogueBox;
    private final String[] dialogues;
    private int dialogueIndex = 0;
    private boolean active = false;
    private final MazeRunnerGame game;

    /**
     * Constructor for DialogueOverlay.
     * @param game main game instance.
     * @param skin skin to use for UI elements.
     */
    public DialogueOverlay(MazeRunnerGame game, Skin skin) {

        this.game = game;

        this.dialogues = new String[]{"Oh, finally someone clocked in!", "Listen quick, we don't have much time to waste!",
                "Here is what happened:", "The experiment has gone wrong, and all of the employees inside have been contaminated by this green goo. They all turned into monster slimes!",
                "Your job is to go into the depths of the base, and activate the emergency self destruct button!", "You do not have to worry about the clearance level between levels.",
                "From what we are seeing in CCTV, the high level employees who have access to the next level are instinctually standing near the Teleporter Exit!",
                "Defeat them, and their keycard will spawn in the map. Find the key to access the next level!", "I am sure you know this already, but this lab has been here since medieval times. Careful of traps!",
                "Reach Level 5, and you will have access to the self-destruct button.", "Go go go !!!!"
        };

        this.stage = new Stage(new ScreenViewport());

        Texture dialogueTexture = new Texture(Gdx.files.internal("assets/Sprites/Frame.png"));
        TextureRegionDrawable dialogueBackground = new TextureRegionDrawable(new TextureRegion(dialogueTexture));

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = dialogueBackground;
        windowStyle.titleFont = new BitmapFont();


        dialogueBox = new Window("", windowStyle);

        dialogueBox.setWidth(stage.getWidth() * 0.75f);
        dialogueBox.setHeight(stage.getHeight() * 0.5f);
        dialogueBox.setPosition(
                (stage.getWidth() - dialogueBox.getWidth()) / 2,
                stage.getHeight() * 0.05f
        );

        dialogueBox.setMovable(false);

        dialogueLabel = new Label("", skin);
        dialogueLabel.setWrap(true);
        dialogueLabel.setFontScale(1f);
        dialogueLabel.setAlignment(Align.center);
        dialogueBox.add(dialogueLabel).width(550).pad(10);

        stage.addActor(dialogueBox);
    }

    /**
     * Starts the dialogue cutscene.
     * Resets the player's movement, sets up the dialogue, and changes the input processor.
     */
    public void startDialogue() {
        game.getGameScreen().getPlayerController().resetMovement();
        dialogueIndex = 0;
        active = true;
        updateDialogue();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Updates the dialogue text to the next line.
     * Ends dialogue cutscene if all dialogues are complete.
     */
    private void updateDialogue() {
        if (dialogueIndex < dialogues.length) {
            dialogueLabel.setText(dialogues[dialogueIndex]);
        } else {
            active = false;
            game.player.collectKey();
            dialogueBox.remove();
            Gdx.input.setInputProcessor(game.getGameScreen().getPlayerController());
        }
    }

    /**
     * Renders the dialogue overlay.
     * Handles input for progressing or skipping dialogue.
     */
    public void render() {
        if (!active) return;

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            active = false;
            game.player.collectKey();
            dialogueBox.remove();
            Gdx.input.setInputProcessor(game.getGameScreen().getPlayerController());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dialogueIndex++;
            updateDialogue();
        }
    }

    /**
     * Checks if the dialogue overlay is currently active.
     * @return true if the dialogue is active, if not false.
     */
    public boolean isActive() {
        return active;
    }
}

