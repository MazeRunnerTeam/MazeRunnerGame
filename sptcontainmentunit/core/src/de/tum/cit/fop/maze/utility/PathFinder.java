package de.tum.cit.fop.maze.utility;

import java.util.*;

/**
 * The PathFinder class implements the A* pathfinding algorithm to find the shortest
 * path between a starting position and a goal position in the game world.
 * it avoids impassable tiles.
 */
public class PathFinder {

    /**
     * Finds the shortest path from a start position to a goal position using the A* algorithm.
     * returns a list with corrdinates, which form a path.
     *
     * @param startX The starting X-coordinate.
     * @param startY The starting Y-coordinate.
     * @param goalX The goal X-coordinate.
     * @param goalY The goal Y-coordinate.
     * @param world The game world.
     * @return A list of int arrays representing the path from start to goal.
     *         Each array contains two values: [x, y].
     */
    public static List<int[]> findPath(int startX, int startY, int goalX, int goalY, WorldGenerator world) {
        HashSet<Node> openSet = new HashSet<>();
        HashSet<Node> closedSet = new HashSet<>();
        openSet.add(new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null));

        while (!openSet.isEmpty()) {
            Node current = openSet.stream()
                    .min(Comparator.comparingInt(node -> node.g + node.h))
                    .orElse(null);

            if (current == null) break;

            openSet.remove(current);

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int[] direction : new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (!world.isPassable(neighborX, neighborY)) continue;

                Node neighbor = new Node(neighborX, neighborY, current.g + 1,
                        heuristic(neighborX, neighborY, goalX, goalY), current);

                if (closedSet.contains(neighbor)) continue;

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else {
                    Node existing = openSet.stream().filter(n -> n.equals(neighbor)).findFirst().orElse(null);
                    if (existing != null && existing.g > neighbor.g) {
                        openSet.remove(existing);
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Computes the heuristic cost for A* pathfinding using Manhattan distance.
     *
     * @param x The X-coordinate of the current node.
     * @param y The Y-coordinate of the current node.
     * @param goalX The X-coordinate of the goal node.
     * @param goalY The Y-coordinate of the goal node.
     * @return The heuristic value - an estimated distance to the goal.
     */
    private static int heuristic(int x, int y, int goalX, int goalY) {
        return Math.abs(x - goalX) + Math.abs(y - goalY);
    }

    /**
     * Reconstructs the path from the goal node back to the start node.
     *
     * @param node The goal node.
     * @return A list of int arrays representing the reconstructed path.
     */
    private static List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new int[]{node.x, node.y});
            node = node.parent;
        }
        return path;
    }
}
