package org.cpvisu.problems;

import org.cpvisu.util.io.InputReader;
import java.util.Arrays;

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
     * @return
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
        // read the instance
        beginDepot[0] = readLine(reader);
        for (int i=1; i < nVehicle; ++i)
            beginDepot[i] = beginDepot[0].deepCopy();
        for (int i = 0; i < nRequests; ++i)
            nodes[i] = readLine(reader);
        for (int i = 0; i < nRequests; ++i) {
            nodes[i + nRequests] = readLine(reader);
            nodes[i + nRequests].setId(nodes[i].getId());
        }
        try {
            endDepot[0] = readLine(reader);
            endDepot[0].setId(beginDepot[0].getId());
        } catch (RuntimeException e) {
            endDepot[0] = beginDepot[0]; // on some instances, the end depot is not specified and corresponds to the begin depot
        }
        for (int i=1; i < nVehicle; ++i)
            endDepot[i] = endDepot[0].deepCopy();
        return new DARPInstance(beginDepot, endDepot, nodes, vehicleCapacityArray, maxRideTime, timeHorizon);
    }

    private static DARPNode readLine(InputReader reader) {
        int id = reader.getInt();
        return new DARPNode(reader.getDouble(), reader.getDouble(), reader.getInt(),
                reader.getInt(), reader.getInt(), reader.getInt(), id);
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

}
