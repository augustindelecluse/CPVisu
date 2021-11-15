package org.cpvisu.problems;

/**
 * Dial-A-Ride node with its visited time, vehicle and cumulated capacity set
 */
public class DARPNodeSolution {

    protected DARPNode darpNode;
    protected double eat=-1; // earliest arrival time
    protected double lat=-1; // latest arrival time
    protected int vehicle=-1; // vehicle assigned to the node
    protected int cumulCapacity=-1; // accumulated capacity at node (actualized AFTER the node is visited)

    /**
     * create a solution for a DARPNode
     * @param node node whose solution will be created
     */
    public DARPNodeSolution(DARPNode node) {
        this.darpNode = node;
    }

    public double getEat() {
        return eat;
    }

    public void setEat(double eat) {
        this.eat = eat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getVehicle() {
        return vehicle;
    }

    public void setVehicle(int vehicle) {
        this.vehicle = vehicle;
    }

    public int getCumulCapacity() {
        return cumulCapacity;
    }

    public void setCumulCapacity(int cumulCapacity) {
        this.cumulCapacity = cumulCapacity;
    }

    public DARPNode getDarpNode() {
        return darpNode;
    }

    @Override
    public String toString() {
        String visitedTime = (eat >= 0 && lat >= 0) ? String.format("Visited at [%.3f %.3f]", eat, lat) : "not visited";
        String visitedVehicle = (vehicle >= 0) ? String.format(" by vehicle %d", vehicle): "";
        String cumulCapa = cumulCapacity >= 0 ? String.format("\nAccumulated capacity = %d", cumulCapacity) : "";
        return String.format("%s\n%s%s%s", darpNode.toString(), visitedTime, visitedVehicle, cumulCapa);
    }

}
