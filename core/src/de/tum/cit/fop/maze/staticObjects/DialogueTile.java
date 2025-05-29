package de.tum.cit.fop.maze.staticObjects;

import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * A tile in the maze that triggers a dialogue when the player interacts with it.
 */
public class DialogueTile extends StaticObject {

    private boolean dialogueTriggered = false;

    /**
     * Constructor for the Dialogue tile.
     * @param game maze runner game instance.
     * @param x x-coordinate of the tile.
     * @param y y-coordinate of the tile.
     * @param width width of the tile.
     * @param height height of the tile.
     */
    public DialogueTile(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "DialogueTile");
        this.isPassable = true;
    }

    /**
     * Updates state of the Dialogue tile.
     * @param delta change in time(in seconds) since last frame.
     */
    @Override
    public void update(float delta) {
        interact();
    }

    /**
     * Responsible for interaction with the player.
     * Starts the dialogue if the player is standing on the tile and dialogue hasn't been triggered yet.
     */
    @Override
    protected void interact() {
        if (isPlayerTouching() && !dialogueTriggered) {
            dialogueTriggered = true;
            game.getGameScreen().getDialogueOverlay().startDialogue();
        }
    }

    /**
     * Checks if the dialogue has been triggered.
     * @return true if dialogue has been triggered, if not false.
     */
    public boolean isTriggered() {
        return dialogueTriggered;
    }

    /**
     * Sets the triggered state of the dialogue.
     * @param b new triggered state.
     */
    public void setTriggered(boolean b) {
        dialogueTriggered = b;
    }
}
