package org.cpvisu.problems;

/**
 * represent a node for a Dial-A-Ride problem: it includes time window, capacity and id of the corresponding node
 */
public class DARPNode extends TimeWindowNode {

    protected int capacity;
    protected int requestId; // used to identify pairs of pickup and drop. negative means depot
    protected int id; // id of the node

    // values assigned when the node is visited. Negative values are ignored
    /*
    protected int cumulCapacity=-1;
    protected double eat=-1; // earliest arrival time
    protected double lat=-1; // latest arrival time
    protected int vehicle=-1; // vehicle assigned to the node

     */

    /**
     * create a node for a Dial-A-Ride problem
     * @param x x location of the node
     * @param y y location of the node
     * @param servingDuration serving duration to perform the task at the node
     * @param capacity capacity of the node. Pickup have positive capacity, drop negative capacity and depot zero capacity
     * @param twStart earliest arrival time at the node
     * @param twEnd latest arrival time at the node
     * @param id id of the node
     * @param requestId id of the request associated at the node. Irrelevant (and negative) if the node is a depot
     */
    public DARPNode(double x, double y, double servingDuration, int capacity, double twStart, double twEnd, int id, int requestId) {
        super(x, y, servingDuration, twStart, twEnd);
        this.capacity = capacity;
        this.id = id;
        this.requestId = requestId;
    }

    public DARPNode(DARPNode node) {
        super(node.getX(), node.getY(), node.getServingDuration(), node.getTwStart(), node.getTwEnd());
        this.capacity = node.getCapacity();
        this.id = node.getId();
        this.requestId = node.getRequestId();
    }

    /**
     * create a copy of the node with the same values
     * @return copy of the current node
     */
    public DARPNode deepCopy() {
        DARPNode copy = new DARPNode(x, y, servingDuration, capacity, twStart, twEnd, id, requestId);
        return copy;
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

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    /**
     * information relative to the node
     * Additional information are provided if the node is visited (assigned vehicle, earliest / latest arrival time, ...)
     */
    @Override
    public String toString() {
        return String.format("""
                        node %d (%-6s) at (%.2f,%.2f) %s
                        Available in [%.3f %.3f]
                          """,
                id, isPickup() ? "Pickup" : isDrop() ? "Drop" : "Depot", x, y,
                isDepot() ? "" : String.format(": request %d, capacity at node = %d", requestId, capacity),
                twStart, twEnd);
    }

    /**
     * gives a short description of the node: its id, type and request id
     * @return short description of the node
     */
    public String shortDescription() {
        return String.format("node %d (%s)", id, isDepot()? "Depot" : String.format("%c%d", isPickup() ? 'P' : 'D', requestId));
    }

}
