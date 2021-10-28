package org.cpvisu.problems;

/**
 * represent a node for a TSP problem
 */
public class TSPNode {

    protected double x;
    protected double y;

    public TSPNode(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
