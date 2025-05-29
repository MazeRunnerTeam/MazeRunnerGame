package de.tum.cit.fop.maze.utility;

import java.util.Objects;

/**
 * Represents a node in the A* pathfinding algorithm.
 */
public class Node implements Comparable<Node>{
    public int x, y;
    public int g;
    public int h;
    public Node parent;

    /**
     * Constructor for creating a node.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param g The cost from the start node to this node.
     * @param h The heuristic cost to the goal.
     * @param parent The parent node in the path.
     */
    public Node(int x, int y, int g, int h, Node parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    /**
     * Compares this node with another node by total cost (g + h).
     *
     * @param other The node to compare.
     * @return A negative value if this node is cheaper, positive if more expensive, and 0 if equal.
     */
    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.g + this.h, other.g + other.h);
    }

    /**
     * Checks if two nodes are the same by comparing their position.
     *
     * @param obj The object to compare.
     * @return True if they have the same position, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Node other)) return false;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Generates a hash code for the node based on its coordinates.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

