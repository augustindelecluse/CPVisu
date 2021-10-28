package org.cpvisu.problems;

/**
 * represent a node for a DARP problem: it includes time window and capacity
 */
public class DARPNode extends TimeWindowNode {

    protected int capacity;
    protected int id; // used to identify pairs of pickup and drop

    public DARPNode(double x, double y, int servingDuration, int capacity, int twStart, int twEnd, int id) {
        super(x, y, servingDuration, twStart, twEnd);
        this.capacity = capacity;
        this.id = id;
    }

    public DARPNode deepCopy() {
        return new DARPNode(x, y, servingDuration, capacity, twStart, twEnd, id);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isPickup() {return capacity > 0;}

    public boolean isDrop() {return capacity < 0;}

    public boolean isDepot() {return capacity == 0;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
