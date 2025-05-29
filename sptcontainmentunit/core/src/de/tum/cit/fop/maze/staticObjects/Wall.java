package de.tum.cit.fop.maze.staticObjects;

import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Responsible for a wall in the game.
 * Handles behavior for a wall, which is impassable
 */
public class Wall extends StaticObject{
    /**
     * Constructor for a Wall object.
     *
     * @param game   MazeRunnerGame instance.
     * @param x      x-coordinate of the wall.
     * @param y      y-coordinate of the wall.
     * @param width  width of the wall.
     * @param height height of the wall.
     */
    public Wall(MazeRunnerGame game, int x, int y, int width, int height) {
        super(game, x, y, width, height, "HardWall");
        isPassable = false;
    }

    @Override
    public void update(float delta) {}

    @Override
    protected void interact() {}
}
