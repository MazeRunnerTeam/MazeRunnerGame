package de.tum.cit.fop.maze.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.screens.CutsceneScreen;

public class CutsceneTile extends StaticObject {

    private boolean triggered = false;
    private Music flashbang;

    public CutsceneTile(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "CutsceneTile");
        isPassable = true;
        flashbang = Gdx.audio.newMusic(Gdx.files.internal("Sounds/SoundFX/flashbang.mp3"));
    }

    @Override
    public void update(float delta) {
        interact();
    }

    @Override
    protected void interact() {
        if (!triggered && isPlayerTouching()) {
            triggered = true;
            flashbang.play();
            game.setScreen(new CutsceneScreen(game, 2));
        }
    }
}
