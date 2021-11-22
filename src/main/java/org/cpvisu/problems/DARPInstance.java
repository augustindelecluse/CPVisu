package org.cpvisu.problems;

import org.cpvisu.util.io.InputReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Dial-A-Ride instance. Contains the information relative to the nodes and vehicle
 * Numbering used for the nodes:
 * - 0...2*nRequest-1 -> request node
 * - 2*nRequest...(2*nRequests+nVehicle-1) -> beginDepot
 * - (2*nRequests+nVehicle)...end -> endDepot
 * this numbering is set as the id of each DARPNode
 */
public class DARPInstance {

    private ArrayList<DARPNode> nodes;
    private int[] vehicleCapacity;  // capacity of each vehicle
    private int maxRideTime;        // max time in vehicle for a request
    private int horizonTime;        // latest return time of vehicle
    private double scaling = 1.0;

    public DARPInstance(ArrayList<DARPNode> nodes, int[] vehicleCapacity, int maxRideTime, int horizonTime) {
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
        ArrayList<DARPNode> nodes = new ArrayList<>();
        int[] vehicleCapacityArray = new int[nVehicle];
        Arrays.fill(vehicleCapacityArray, vehicleCapacity);
        // store the beginning depot
        int cnt = 0;
        DARPNode beginDepot = readLine(reader, cnt, -1);
        // read the instance: requests nodes
        for (int i = 0; i < nRequests; ++i)
            nodes.add(readLine(reader, cnt++, i));
        for (int i = 0; i < nRequests; ++i)
            nodes.add(readLine(reader, cnt++, i));
        // add the values for the beginning depot
        beginDepot.setId(cnt++);
        nodes.add(beginDepot);
        for (int i=1; i < nVehicle; ++i) {
            DARPNode depotCopy = beginDepot.deepCopy();
            depotCopy.setId(cnt++);
            nodes.add(depotCopy);
        }
        // read the instance: end depot
        DARPNode endDepot;
        try {
            endDepot = readLine(reader, cnt, -1);
        } catch (RuntimeException e) {
            endDepot = beginDepot.deepCopy(); // on some instances, the end depot is not specified and corresponds to the begin depot
        }
        endDepot.setId(cnt++);
        nodes.add(endDepot);
        for (int i=1; i < nVehicle; ++i) {
            DARPNode depotCopy = endDepot.deepCopy();
            depotCopy.setId(cnt++);
            nodes.add(depotCopy);
        }
        return new DARPInstance(nodes, vehicleCapacityArray, maxRideTime, timeHorizon);
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
            node.setServingDuration((int) (node.getServingDuration() * scaling));
            node.setTwStart((int) (node.getTwStart() * scaling));
            node.setTwEnd((int) (node.getTwEnd() * scaling));
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
        return nodes.stream().filter(DARPNode::isBeginDepot).toArray(DARPNode[]::new);
    }

    public DARPNode[] getEndDepot() {
        return nodes.stream().filter(DARPNode::isEndDepot).toArray(DARPNode[]::new);
    }

    public DARPNode[] getNodes() {
        return nodes.stream().filter(n -> !n.isDepot()).toArray(DARPNode[]::new);
    }

    public int[] getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getVehicleCapacity(int vehicle) {
        return vehicleCapacity[vehicle];
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
        return nodes.size();
    }

    public int getNRequests() {
        return (nodes.size() - getNVehicle() * 2) / 2;
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
        return nodes.stream().filter(n -> n.getId() == node).findFirst().orElse(null);
    }

    /**
     * map the nodes from the instance with the given mapping
     * providing mapping {i -> j}, the node with id == i will be assigned id j
     * @param mapping mapping of ids for the nodes
     */
    public void mapNodes(HashMap<Integer, Integer> mapping) {
        for (DARPNode node: nodes)
            node.setId(mapping.get(node.getId()));
    }

    /**
     * map the nodes from the instance with the given mapping
     * providing mapping {i -> [a, b, c]}, the node with id == i will be multiplied and assigned id a, b, c
     * if the arraylist contained in the mapping contains only 1 element, the node will simply change its id
     * unspecified mapping of nodes are ignored
     * @param mapping mapping of ids for the nodes
     */
    public void mapNodesWithDuplicate(HashMap<Integer, ArrayList<Integer>> mapping) {
        ArrayList<DARPNode> allNodes = new ArrayList<>();
        for (DARPNode node: this.nodes) {
            for (int newId: mapping.get(node.getId())) {
                DARPNode newNode = node.deepCopy();
                newNode.setId(newId);
                allNodes.add(newNode);
            }
        }
        this.nodes = allNodes;
    }

}
