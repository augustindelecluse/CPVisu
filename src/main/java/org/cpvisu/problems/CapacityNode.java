package org.cpvisu.problems;

/**
 * represent a node and its capacity
 */
public class CapacityNode extends TSPNode {

    protected int capacity;

    public CapacityNode(double x, double y, int capacity) {
        super(x, y);
        this.capacity = capacity;
    }

    public boolean isPickup() {return capacity > 0;}

    public boolean isDrop() {return capacity < 0;}

    public boolean isDepot() {return capacity == 0;}
}
