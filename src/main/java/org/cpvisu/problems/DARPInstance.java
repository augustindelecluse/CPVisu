package org.cpvisu.problems;

import org.cpvisu.util.io.InputReader;
import java.util.Arrays;

/**
 * Dial-A-Ride instance. Contains the information relative to the nodes and vehicle
 * Numbering used for the nodes:
 * - 0...nVehicle -> beginDepot
 * - nVehicle...(2*nRequests-nVehicle) -> node
 * - (2*nRequests-nVehicle)...end -> endDepot
 * this numbering is set as the id of each DARPNode
 */
public class DARPInstance {

    private DARPNode[] beginDepot;  // begin depot, 1 per vehicle
    private DARPNode[] endDepot;    // end depot, 1 per vehicle
    private DARPNode[] nodes;       // each node in the problem
    private int[] vehicleCapacity;  // capacity of each vehicle
    private int maxRideTime;        // max time in vehicle for a request
    private int horizonTime;        // latest return time of vehicle
    private double scaling = 1.0;

    public DARPInstance(DARPNode[] beginDepot, DARPNode[] endDepot, DARPNode[] nodes, int[] vehicleCapacity, int maxRideTime, int horizonTime) {
        this.beginDepot = beginDepot;
        this.endDepot = endDepot;
        this.nodes = nodes;
        this.vehicleCapacity = vehicleCapacity;
        this.maxRideTime = maxRideTime;
        this.horizonTime = horizonTime;
    }

    /**
     * create a DARP instance from a file
     * @param filename filename to the DARP instance
     * @return DARP instance associated to the file
     */
    public static DARPInstance readFromFile(String filename) {
        InputReader reader = new InputReader(filename);
        // first line contains general information about the problem
        int nVehicle = reader.getInt();
        int nRequests = reader.getInt();
        int timeHorizon = reader.getInt();
        int vehicleCapacity = reader.getInt();
        int maxRideTime = reader.getInt();
        DARPNode[] nodes = new DARPNode[nRequests * 2];
        DARPNode[] beginDepot = new DARPNode[nVehicle];
        DARPNode[] endDepot = new DARPNode[nVehicle];
        int[] vehicleCapacityArray = new int[nVehicle];
        Arrays.fill(vehicleCapacityArray, vehicleCapacity);
        // read the instance: begin depot
        int cnt = 0;
        beginDepot[0] = readLine(reader, cnt++, -1);
        for (int i=1; i < nVehicle; ++i) {
            beginDepot[i] = beginDepot[0].deepCopy();
            beginDepot[i].setId(cnt++);
        }
        // read the instance: requests nodes
        for (int i = 0; i < nRequests; ++i)
            nodes[i] = readLine(reader, cnt++, i);
        for (int i = 0; i < nRequests; ++i) {
            nodes[i + nRequests] = readLine(reader, cnt++, i);
        }
        // read the instance: end depot
        try {
            endDepot[0] = readLine(reader, cnt++, -1);
        } catch (RuntimeException e) {
            endDepot[0] = beginDepot[0].deepCopy(); // on some instances, the end depot is not specified and corresponds to the begin depot
            endDepot[0].setId(cnt++);
        }
        for (int i=1; i < nVehicle; ++i) {
            endDepot[i] = endDepot[0].deepCopy();
            endDepot[i].setId(cnt++);
        }
        return new DARPInstance(beginDepot, endDepot, nodes, vehicleCapacityArray, maxRideTime, timeHorizon);
    }

    private static DARPNode readLine(InputReader reader) {
        int id = reader.getInt();
        return new DARPNode(reader.getDouble(), reader.getDouble(), reader.getInt(),
                reader.getInt(), reader.getInt(), reader.getInt(), id, id);
    }

    private static DARPNode readLine(InputReader reader, int id, int requestId) {
        reader.getInt(); // skip the first entry
        return new DARPNode(reader.getDouble(), reader.getDouble(), reader.getInt(),
                reader.getInt(), reader.getInt(), reader.getInt(), id, requestId);
    }

    /**
     * apply a time scaling into all nodes in the instance
     * each time related value will be multiplied by a certain amount
     * @param scaling
     */
    public void setTimeScaling(double scaling) {
        for (DARPNode node: nodes) {
            node.setServingDuration((int)(node.getServingDuration() * scaling));
            node.setTwStart((int)(node.getTwStart() * scaling));
            node.setTwEnd((int)(node.getTwEnd() * scaling));
        }
        for (DARPNode node: beginDepot) {
            node.setServingDuration((int)(node.getServingDuration() * scaling));
            node.setTwStart((int)(node.getTwStart() * scaling));
            node.setTwEnd((int)(node.getTwEnd() * scaling));
        }
        for (DARPNode node: endDepot) {
            node.setServingDuration((int)(node.getServingDuration() * scaling));
            node.setTwStart((int)(node.getTwStart() * scaling));
            node.setTwEnd((int)(node.getTwEnd() * scaling));
        }
        this.scaling = scaling;
    }

    /**
     * revert the time scaling to its previous value
     * only works if at most one time change occurred since the last call to invertTimeScaling
     */
    public void invertTimeScaling() {
        setTimeScaling(1.0 / scaling);
        this.scaling = 1.0;
    }

    public DARPNode[] getBeginDepot() {
        return beginDepot;
    }

    public DARPNode[] getEndDepot() {
        return endDepot;
    }

    public DARPNode[] getNodes() {
        return nodes;
    }

    public int[] getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getMaxRideTime() {
        return maxRideTime;
    }

    public int getHorizonTime() {
        return horizonTime;
    }

    public double getScaling() {
        return scaling;
    }

    public int getNVehicle() {
        return vehicleCapacity.length;
    }

    public int getNNodes() {
        return getNVehicle() * 2 + nodes.length;
    }

    public int getNRequests() {
        return nodes.length / 2;
    }

    /**
     * gives the distance between 2 nodes
     * computed as the euclidean distance
     * @param DARPNodeA first node
     * @param DARPNodeB second node
     * @return distance between the 2 nodes
     */
    public double getDistance(DARPNode DARPNodeA, DARPNode DARPNodeB) {
        double dx = DARPNodeA.getX() - DARPNodeB.getX();
        double dy = DARPNodeA.getY() - DARPNodeB.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    protected DARPNode getNode(int node) {
        int nVehicle = getNVehicle();
        return node < nVehicle ? beginDepot[node] : node < nodes.length + nVehicle ? nodes[node - nVehicle] : endDepot[node - nodes.length];
    }

    /**
     * map the given numbering system into the inner numbering system
     *
     * example:
     *      with 2 vehicles and 4 nodes, the inner representation is
     *      - node[0, 1] -> begin nodes (comes first)
     *      - node[2..5] -> requests nodes (comes second)
     *      - node[6, 7] -> ending nodes (comes third)
     *
     *      mapNodes({0, 6, 3}, 0, 2, 1)
     *
     *      maps the nodes, indicating that in the given representation
     *      - node[0, 1] -> begin nodes (comes first)
     *      - node[2, 3] -> ending nodes (comes second)
     *      - node[4..7] -> requests nodes (comes third)
     *
     *      the array is therefore changed to {0, 4, 7}
     *
     * @param order order that needs to be mapped to the inner representation
     * @param rangeBeginDepot number in 0..2: indicates where the beginning depot are located
     * @param rangeRequestNode number in 0..2: indicates where the nodes are located
     * @param rangeEndDepot number in 0..2: indicates where the ending depot are located
     */
    public void mapNodes(Integer[] order, int rangeBeginDepot, int rangeRequestNode, int rangeEndDepot) {
        int nVehicle = getNVehicle();
        int nNodes = getNNodes();
        int nRequests = getNRequests();
        int nRequestsNodes = nRequests * 2;
        int mapping = rangeBeginDepot*100+rangeRequestNode*10+rangeEndDepot;
        switch (mapping) {
            case 12: // default mapping, nothing to change
                return;
            case 102: // nodes, begin, end -> begin, nodes, end
                for (int i = 0; i < order.length ; ++i) {
                    if (order[i] < nRequestsNodes) // request node, needs to be mapped
                        order[i] = order[i] + nVehicle; // the beginning nodes come before
                    else if (order[i] < nRequestsNodes + nVehicle) // begin node, needs to be mapped
                        order[i] = order[i] - nRequestsNodes; // the beginning nodes come before
                }
                return;
            // TODO implement other mappings
            default:
                throw new IllegalArgumentException("invalid mapping specified");
        }
    }

}
