package org.cpvisu.problems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * solution to a Dial-A-Ride problem
 */
public class DARPSolution {

    private final DARPInstance instance;
    private final ArrayList<DARPNodeSolution>[] routes;

    public DARPSolution(DARPInstance instance) {
        this.instance = instance;
        this.routes = new ArrayList[instance.getNVehicle()];
        for (int i = 0; i < routes.length ; ++i)
            routes[i] = new ArrayList<>();
    }

    /**
     * tell if the solution violates the constraints or not
     * @return True if the solution is valid
     */
    public boolean isValid() {
        for (int i = 0; i < instance.getNVehicle(); ++i)
            setValuesVisit(i);
        HashSet<Integer> seen = new HashSet<>(); // observed nodes in the route
        for (ArrayList<DARPNodeSolution> vehicleRoute: routes) {
            int i = 0;
            int size = vehicleRoute.size();
            HashMap<Integer, Double> pickupEat = new HashMap<>();
            HashMap<Integer, Double> pickupLat = new HashMap<>();
            for (DARPNodeSolution node: vehicleRoute) {
                if (seen.contains(node.darpNode.id)) // node has already been visited
                    return false;
                seen.add(node.darpNode.id);
                if ((i == 0 || i == size-1) && !node.darpNode.isDepot()) // starting or end node of the route is not a depot
                    return false;
                else if (node.darpNode.isPickup()) {
                    pickupEat.put(node.darpNode.requestId, node.eat);
                    pickupLat.put(node.darpNode.requestId, node.lat);
                } else {
                    if (!pickupEat.containsKey(node.getDarpNode().getRequestId())) // corresponding pickup has not been visited
                        return false;
                    double correspondingEat = pickupEat.get(node.getDarpNode().requestId);
                    double correspondingLat = pickupLat.get(node.getDarpNode().requestId);
                    // max ride time between pickup and drop violated
                    if (node.eat - correspondingEat > instance.getMaxRideTime())
                        return false;
                    // TODO check for max ride time using lat
                }
                // time window or capacity violation
                if (node.eat > node.lat || node.lat > node.getDarpNode().getTwEnd() || node.cumulCapacity < 0 || node.cumulCapacity > instance.getVehicleCapacity(node.getVehicle()))
                    return false;
                i++;
            }
        }
        return seen.size() == instance.getNNodes(); // true if all nodes have been visited
    }

    /**
     * add a visit to a DARPNode, setting its vehicle, cumulative capacity, earliest and latest arrival time
     * the node will be added at the end of the current vehicle
     * @param vehicle vehicle visiting the node
     * @param node node visited
     */
    public void addVisit(int vehicle, DARPNode... node) {
        routes[vehicle].addAll(Arrays.stream(node).map(DARPNodeSolution::new).toList());
        setValuesVisit(vehicle);
    }

    public void addVisit(int vehicle, Integer... node) {
        DARPNode[] darpNodes = new DARPNode[node.length];
        HashMap<Integer, Integer> mapping = new HashMap<>();
        for (int i = 0; i < node.length ; ++i)
            mapping.put(node[i], i);
        for (DARPNode darpNode: instance.getBeginDepot()) {
            if (mapping.containsKey(darpNode.getId()))
                darpNodes[mapping.get(darpNode.getId())] = darpNode;
        }
        for (DARPNode darpNode: instance.getNodes()) {
            if (mapping.containsKey(darpNode.getId()))
                darpNodes[mapping.get(darpNode.getId())] = darpNode;
        }
        for (DARPNode darpNode: instance.getEndDepot()) {
            if (mapping.containsKey(darpNode.getId()))
                darpNodes[mapping.get(darpNode.getId())] = darpNode;
        }
        addVisit(vehicle, darpNodes);
    }

    /**
     * set the values of the DARPNodeSolutions for a given vehicle
     * the beginning and end depot for the vehicle must have been set
     * @param vehicle vehicle whose nodes solution values will be set
     */
    private void setValuesVisit(int vehicle) {

        int nNodes = routes[vehicle].size();
        if (nNodes < 2) // the beginning and end depot have not been assigned
            return;
        DARPNode[] nodesVisited = routes[vehicle].stream().map(DARPNodeSolution::getDarpNode).toArray(DARPNode[]::new);
        // the first node should be the depot, set the starting time visited from here
        double timeReached = routes[vehicle].get(0).getDarpNode().getTwStart();
        int capacity = nodesVisited[0].getCapacity();

        // set the values for the beginning depot
        routes[vehicle].get(0).setCumulCapacity(capacity);
        routes[vehicle].get(0).setVehicle(vehicle);
        routes[vehicle].get(0).setEat(timeReached);
        timeReached += nodesVisited[0].getServingDuration();

        for (int i = 1; i < nNodes ; ++i) {
            timeReached = timeReached + instance.getDistance(nodesVisited[i-1], nodesVisited[i]);
            // waiting at a node is allowed
            timeReached = Math.max(timeReached, nodesVisited[i].getTwStart());
            // set the values for eat, cumulative capacity and serving vehicle
            capacity += nodesVisited[i].getCapacity();
            routes[vehicle].get(i).setCumulCapacity(capacity);
            routes[vehicle].get(i).setVehicle(vehicle);
            routes[vehicle].get(i).setEat(timeReached);
            // add the service duration
            timeReached += nodesVisited[i].getServingDuration();
        }
        // set the ending time visited
        timeReached = nodesVisited[nNodes-1].getTwEnd();
        routes[vehicle].get(nNodes-1).setLat(timeReached);
        for (int i = nNodes-2; i >= 0 ; --i) {
            // TODO check for service time
            timeReached = timeReached - instance.getDistance(nodesVisited[i+1], nodesVisited[i]);
            timeReached = Math.min(timeReached, nodesVisited[i].getTwEnd());
            routes[vehicle].get(i).setLat(timeReached);
        }

    }

    public void resetVisit(int vehicle) {
        routes[vehicle].clear();
    }

    public void resetAllVisits() {
        int nVehicle = instance.getNVehicle();
        for (int i = 0; i < nVehicle; ++i)
            resetVisit(i);
    }

    /**
     * gives the solution nodes assigned to a vehicle
     * @param vehicle vehicle whose nodes will be assigned
     * @return list of solution nodes
     */
    public ArrayList<DARPNodeSolution> getNodes(int vehicle) {
        return routes[vehicle];
    }

}
