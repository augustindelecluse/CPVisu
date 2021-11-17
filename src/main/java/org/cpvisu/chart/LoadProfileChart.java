package org.cpvisu.chart;

import javafx.collections.ObservableList;
import javafx.scene.chart.*;

import java.util.HashMap;

/**
 * chart for drawing the maximum load profile
 */
public class LoadProfileChart extends AreaChart<String,Number> {

    private String selectedKey;
    private HashMap<String, Series> vehicleLoads; // set of {description, Series} for the load

    public LoadProfileChart(Axis<String> axis, Axis<Number> axis1, double maxLoad) {
        super(axis, axis1);
        this.vehicleLoads = new HashMap<>();
    }

    public LoadProfileChart() {
        super(new CategoryAxis(), new NumberAxis());
        this.vehicleLoads = new HashMap<>();
    }

    public LoadProfileChart(double maxLoad) {
        super(new CategoryAxis(), new NumberAxis("Load", 0, maxLoad, 1));
        this.vehicleLoads = new HashMap<>();
    }

    /**
     * set the current item for which to add loads values
     * @param description description for which to add loads values
     */
    public void setVehicle(String description) {
        selectedKey = description;
        // create new Series for it if they were absent
        createSeriesIfNotExist(description);
    }

    private void createSeriesIfNotExist(String name) {
        if (!vehicleLoads.containsKey(name)) {
            Series series = new Series();
            series.setName(name);
            vehicleLoads.put(name, series);
            this.getData().add(series);
        }
    }

    /**
     * remove the current vehicle from the graph
     * @param vehicle vehicle to remove from the graph
     */
    public void removeVehicle(int vehicle) {
        this.getData().remove(vehicleLoads.get(vehicle));
        vehicleLoads.remove(vehicle);
    }

    /**
     * clear the values associated to the current vehicle
     * @param description description whose values will be removed
     */
    public void clearVehicle(String description) {
        vehicleLoads.get(description).getData().clear();
    }

    /**
     * add a new load value for the current capacity graph
     * the nodes visited must be called in order of visit
     * @param node new node that is visited
     * @param cumulCapacity cumulated capacity at the node
     */
    public void addCumulCapacity(String node, double cumulCapacity) {
        ObservableList data = vehicleLoads.get(selectedKey).getData();
        if (data.size() > 0) { // a node before existed, set the capacity for transition
            double predCapa = ((Number) ((Data) data.get(data.size() - 1)).getYValue()).doubleValue();
            data.add(new Data(node, predCapa));
        }
        data.add(new Data(node, cumulCapacity));
    }

    /**
     * add all nodes and capacities to the given vehicle
     * @param description description whose capacity will be set
     * @param nodes nodes visited by the vehicle, in order
     * @param cumulCapacities cumulated capacities at each node
     */
    public void addCumulCapacity(String description, String[] nodes, double... cumulCapacities) {
        assert (nodes.length == cumulCapacities.length);
        createSeriesIfNotExist(description);
        ObservableList data = vehicleLoads.get(description).getData();
        if (data.size() > 0) {
            double predCapa = ((Number) ((Data) data.get(data.size() - 1)).getYValue()).doubleValue();
            data.add(new Data(nodes[0], predCapa));
        }
        data.add(new Data(nodes[0], cumulCapacities[0]));
        for (int i = 1; i < nodes.length ; ++i) {
            data.add(new Data(nodes[i], cumulCapacities[i-1]));
            data.add(new Data(nodes[i], cumulCapacities[i]));
        }
    }

    /**
     * add all nodes and capacities to the current vehicle
     * @param nodes nodes visited by the vehicle, in order
     * @param cumulCapacities cumulated capacities at each node
     */
    public void addCumulCapacity(String[] nodes, double... cumulCapacities) {
        addCumulCapacity(selectedKey, nodes, cumulCapacities);
    }

    /**
     * add the node and its capacity to the current vehicle
     * @param node last node currently visited by the vehicle
     * @param capacity capacity of node (not cumulated)
     */
    public void addCapacity(String node, double capacity) {
        ObservableList data = vehicleLoads.get(selectedKey).getData();
        double predCapa = 0.;
        if (data.size() > 0) {
            predCapa = ((Number) ((Data) data.get(data.size() - 1)).getYValue()).doubleValue();
            data.add(new Data(node, predCapa));
        }
        data.add(new Data(node, predCapa + capacity));
    }

    /**
     * add the node and its capacity to the current description
     * @param description description whose capacity will be set
     * @param nodes last node currently visited by the description
     * @param capacities capacity of the nodes (not cumulated)
     */
    public void addCapacity(String description, String[] nodes, double[] capacities) {
        assert (nodes.length == capacities.length);
        createSeriesIfNotExist(description);
        ObservableList data = vehicleLoads.get(description).getData();
        double predCapa = 0.;
        if (data.size() > 0) {
            predCapa = ((Number) ((Data) data.get(data.size() - 1)).getYValue()).doubleValue();
            data.add(new Data(nodes[0], predCapa));
        }
        data.add(new Data(nodes[0], predCapa + capacities[0]));
        predCapa += capacities[0];
        for (int i = 1; i < nodes.length ; ++i) {
            data.add(new Data(nodes[i], predCapa));
            data.add(new Data(nodes[i], predCapa + capacities[i]));
            predCapa += capacities[i];
        }
    }

    /**
     * add the node and its capacity to the current vehicle
     * @param nodes last node currently visited by the vehicle
     * @param capacities capacity of the nodes (not cumulated)
     */
    public void addCapacity(String[] nodes, double[] capacities) {
        addCapacity(selectedKey, nodes, capacities);
    }

    /**
     * set the value for the max load, updating the maximum y-axis value
     * @param maxLoad maximum load for the capacity graph
     */
    public void setMaxLoad(double maxLoad) {
        ((ValueAxis<Number>) getYAxis()).setUpperBound(maxLoad);
    }

}
