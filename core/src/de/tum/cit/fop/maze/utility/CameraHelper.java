package de.tum.cit.fop.maze.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import de.tum.cit.fop.maze.actors.Player;

/**
 * Helper class for managing the game camera.
 * Handles camera movement, zooming, and map boundaries.
 */
public class CameraHelper {

    private final OrthographicCamera camera;
    private float mapWidth;
    private float mapHeight;

    /**
     * Constructor for CameraHelper.
     * @param viewportWidth The width of the viewport.
     * @param viewportHeight The height of the viewport.
     */
    public CameraHelper(float viewportWidth, float viewportHeight) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.zoom = 0.4f;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Sets the boundaries of the map.
     * @param mapWidth width of the map.
     * @param mapHeight height of the map.
     */
    public void setMapBounds(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    /**
     * Updates the camera position based on the player's position.
     * If the camera can see more than the map, the horizontal and/or vertical cameras stay in the center.
     * Handles smooth camera movement and keeps the camera within map boundaries.
     * @param player player to follow with the camera.
     */
    public void update(Player player) {
        float playerX = player.getX() * Settings.SCALED_TILE_SIZE + Settings.SCALED_TILE_SIZE / 2f;
        float playerY = player.getY() * Settings.SCALED_TILE_SIZE + Settings.SCALED_TILE_SIZE / 2f;

        float cameraWidth  = camera.viewportWidth  * camera.zoom;
        float cameraHeight = camera.viewportHeight * camera.zoom;

        float halfCamWidth  = cameraWidth  / 2f;
        float halfCamHeight = cameraHeight / 2f;

        float clampedY = MathUtils.clamp(playerY, halfCamHeight, mapHeight - halfCamHeight);
        float clampedX = MathUtils.clamp(playerX, halfCamWidth, mapWidth - halfCamWidth);

        if (cameraHeight >= mapHeight) {
            float mapCenterY = mapHeight / 2f;
            camera.position.y += (mapCenterY - camera.position.y) * 0.05f;
        } else {
            camera.position.y += (clampedY - camera.position.y) * 0.05f;
        }

        if (cameraWidth >= mapWidth) {
            float mapCenterX = mapWidth / 2f;
            camera.position.x += (mapCenterX - camera.position.x) * 0.05f;
        }
        else {
            camera.position.x += (clampedX - camera.position.x) * 0.05f;
        }

        camera.update();
    }

    /**
     * Resizes camera viewport.
     * @param width new width of the viewport.
     * @param height new height of the viewport.
     */
    public void resize(int width, int height) {
        camera.viewportWidth = width / Settings.SCALE;
        camera.viewportHeight = height / Settings.SCALE;
        camera.update();
    }

    /**
     * Sets the zoom level of the camera.
     * @param zoom  desired zoom level, clamped between 0.25 and 1.0.
     */
    public void setZoom(float zoom) {
        float minZoom = 0.25f;
        float maxZoom = 1.0f;
        camera.zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
        camera.update();
    }
}

