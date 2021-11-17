package org.cpvisu;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.cpvisu.chart.DARPGanttChart;
import org.cpvisu.chart.LoadProfileChart;
import org.cpvisu.problems.DARPInstance;
import org.cpvisu.problems.DARPNode;
import org.cpvisu.problems.DARPNodeSolution;
import org.cpvisu.problems.DARPSolution;
import org.cpvisu.shapes.*;
import org.cpvisu.util.colors.ColorFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

import static org.cpvisu.AnimationFactory.moveOnDrag;
import static org.cpvisu.AnimationFactory.zoomOnSCroll;

/**
 * visualisation for a dial-a-ride problem
 * no checking of satisfiability occurs
 */
public class VisualDARP {

    private DARPSolution solution;
    private DARPInstance darp;
    private Function<DARPNode, VisualNode> drawingFunction;
    private final double threshold = 20; // difference in coordinates between the scene and the effective drawing of the nodes
    private Pane pane;
    private Pane shapesGroup;
    private final int width;
    private final int height;
    private final DARPNode[] nodeList;

    private final Color unselectedNode = Color.rgb(125, 125, 125, 0.8);

    /**
     * create a Dial-A-Ride visualisation
     * @param darp Dial-A-Ride instance that needs to be represented
     * @param width width for the node layout
     * @param height height for the node layout
     * @param drawingFunction mapping of Dial-A-Ride nodes to their representation on screen
     */
    public VisualDARP(DARPInstance darp, int width, int height, Function<DARPNode, VisualNode> drawingFunction) {
        this.darp = darp;
        this.drawingFunction = drawingFunction;
        this.width = width;
        this.height = height;
        solution = new DARPSolution(darp);
        this.nodeList = new DARPNode[darp.getNNodes()];
        int i = 0;
        for (DARPNode node: darp.getBeginDepot())
            nodeList[i++] = node;
        for (DARPNode node: darp.getNodes())
            nodeList[i++] = node;
        for (DARPNode node: darp.getEndDepot())
            nodeList[i++] = node;

    }

    /**
     * create a Dial-A-Ride visualisation
     * @param darp Dial-A-Ride instance that needs to be represented
     * @param width width for the node layout
     * @param height height for the node layout
     */
    public VisualDARP(DARPInstance darp, int width, int height) {
        this(darp, width, height, VisualDARP::DefaultMapping);
    }

    /**
     * map a DARPNode into a VisualNode, that will be drawn on the screen
     * @param node node that needs to be drawn on the screen
     * @return visual representation of the node
     */
    private static VisualNode DefaultMapping(DARPNode node) {
        double radius = 5;
        if (node.isDepot()) {
            return new VisualCircle(node.getX(), node.getY(), radius);
        } else if (node.isPickup()) {
            return new VisualCircle(node.getX(), node.getY(), radius);
        } else if (node.isDrop()) {
            double x = node.getX() - radius; // center the point
            double y = node.getY() - radius;
            VisualRectangle visualRectangle = new VisualRectangle(x, y, radius * 2, radius * 2);
            visualRectangle.setRotate(45);
            return visualRectangle;
        }
        return null;
    }

    /**
     * draw the nodes on the interface, without an assigned vehicle
     * @return pane containing the nodes
     */
    public Pane nodeLayout() {
        return nodeLayout(-1);
    }

    /**
     * draw the nodes on the interface
     * color the nodes in grey if they are not visited by the current vehicle, or in color otherwise
     * @return pane containing the nodes
     */
    public Pane nodeLayout(int vehicle) {
        Node[] shapes = new Node[darp.getNNodes()];
        VisualNode[] visualNode = new VisualNode[shapes.length];
        //this.shapes = new VisualNode[shapes.length];
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        // register the bounds for the coordinates
        for (DARPNode node: nodeList) {
            minX = Math.min(minX, node.getX());
            minY = Math.min(minY, node.getY());
            maxX = Math.max(maxX, node.getX());
            maxY = Math.max(maxY, node.getY());
        }
        // keep the lowest ratio for the transformation: the coordinate system should remain isometric
        double min = Math.min(minX, minY);
        double max = Math.max(maxX, maxY);
        double minWindow = Math.min(width, height);
        // register the nodes that are visited by the current vehicle
        HashSet<Integer> selectedRoute = null;
        HashMap<Integer, Color> colorRoute = new HashMap<>();
        if (vehicle >= 0) { // a specific path for a vehicle needs to be drawn
            selectedRoute = new HashSet<>();
            int i = 0; // used to increment color count
            for (DARPNodeSolution nodeSolution : solution.getNodes(vehicle)) {
                int requestId = nodeSolution.getDarpNode().getRequestId();
                if (!colorRoute.containsKey(requestId) && requestId >= 0)
                    colorRoute.put(requestId, ColorFactory.getPalette("default").colorAt(i++));
                selectedRoute.add(nodeSolution.getDarpNode().getId());
            }
        }
        // space out the shapes so that they occupy the whole screen
        for (DARPNode node: nodeList) {
            VisualNode shape = drawingFunction.apply(node);
            double x = ((node.getX() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
            double y = ((node.getY() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
            shape.moveTo(x, y);
            int i = node.getId();
            visualNode[i] = shape;
            if (selectedRoute != null && !selectedRoute.contains(i))
                visualNode[i].setFill(unselectedNode);
            else {
                int requestId = node.getRequestId();
                Color color = colorRoute.getOrDefault(requestId, requestId >= 0 ? ColorFactory.getPalette("default").colorAt(node.getRequestId()) : unselectedNode);
                visualNode[i].setFill(color);
            }
            shapes[i] = shape.getNode();
            // click listener to provide information related to the node
            shapes[i].setOnMousePressed(e -> {
                System.out.println(node);
            });
        }
        // plot the transition between the nodes for the current vehicle
        Pane transitions = new Pane();
        transitions.getStylesheets().add(getClass().getResource("visual-darp.css").toExternalForm());
        int pred = 0;
        int j = 0;
        if (vehicle >= 0) {
            for (DARPNodeSolution nodeSolution : solution.getNodes(vehicle)) {
                int succ = nodeSolution.getDarpNode().getId();
                if (j != 0) {
                    // plot the transition between pred and succ
                    double xFrom = visualNode[pred].getCenterX() + shapes[pred].getTranslateX();
                    double yFrom = visualNode[pred].getCenterY() + shapes[pred].getTranslateY();
                    double xTo = visualNode[succ].getCenterX() + shapes[succ].getTranslateX();
                    double yTo = visualNode[succ].getCenterY() + shapes[succ].getTranslateY();
                    VisualArrow transition = new VisualArrow(xFrom, yFrom, xTo, yTo, 0.5);
                    transition.getMainLine().getStyleClass().add("node-layout-transition-line");
                    transition.getTip().getStyleClass().add("node-layout-transition-tip");
                    transitions.getChildren().add(transition);
                }
                ++j;
                pred = succ;
            }
        }
        shapesGroup = new Pane(transitions, new Pane(shapes));
        pane = new Pane(shapesGroup);
        pane.setPrefHeight(height);
        pane.setPrefWidth(width);
        zoomOnSCroll(pane);
        moveOnDrag(pane, shapesGroup);
        return pane;
    }

    /**
     * gives the Gantt layout associated to a vehicle
     * @return Gantt layout for a vehicle
     */
    public DARPGanttChart GanttLayout(int vehicle) {
        String[] nodes = nodeDescription(vehicle);
        DARPGanttChart chart = DARPGanttChart.fromCategories(nodes);
        chart.setBlockHeight(20);
        int i = 0;
        double timeArrival;
        DARPNodeSolution pred = null;
        for (DARPNodeSolution nodeSolution: solution.getNodes(vehicle)) {
            if (i==0)
                timeArrival = nodeSolution.getEat();
            else
                timeArrival = Math.max(pred.getEat(), pred.getDarpNode().getTwStart()) + pred.getDarpNode().getServingDuration() + darp.getDistance(pred.getDarpNode(), nodeSolution.getDarpNode());
            chart.setTimeSlot(nodes[i], nodeSolution.getDarpNode().getTwStart(), nodeSolution.getDarpNode().getTwEnd(), nodeSolution.getEat(), nodeSolution.getLat(), timeArrival);
            if (i > 0) {
                chart.setTransition(nodes[i-1], Math.max(pred.getEat(), pred.getDarpNode().getTwStart()) + pred.getDarpNode().getServingDuration(),
                        darp.getDistance(pred.getDarpNode(), nodeSolution.getDarpNode()));
            }
            pred = nodeSolution;
            ++i;
        }
        return chart;
    }

    /**
     * return the load profile associated to this vehicle, at each encountered node
     * @param vehicle vehicle whose load profile needs to be known
     * @return load profile of the vehicle. x-axis is indexed using the labels of the nodes
     */
    public LoadProfileChart loadProfile(int vehicle) {
        LoadProfileChart chart = new LoadProfileChart(darp.getVehicleCapacity(vehicle));
        String[] nodes = solution.getNodes(vehicle).stream().map(n -> String.format("node %d", n.getDarpNode().getId())).toArray(String[]::new);
        double[] cumulCapacity = solution.getNodes(vehicle).stream().mapToDouble(DARPNodeSolution::getCumulCapacity).toArray();
        String description = String.format("Vehicle %d", vehicle);
        chart.addCumulCapacity(description, nodes, cumulCapacity);
        return chart;
    }

    private String[] nodeDescription(int vehicle) {
        String[] nodes = new String[solution.getNodes(vehicle).size()];
        int i = 0;
        for (DARPNodeSolution nodeSolution: solution.getNodes(vehicle))
            nodes[i++] = nodeSolution.getDarpNode().shortDescription();
        return nodes;
    }

    /**
     * add a route for a given vehicle
     * does not reset the exiting route: the sequence of nodes will be added to the route
     * @param vehicle vehicle whose route needs to be added
     * @param visitedOrder order of visit for each node
     */
    public void addRoute(int vehicle, Integer... visitedOrder) {
        solution.addVisit(vehicle, visitedOrder);
    }

    /**
     * reset the route of a vehicle
     * @param vehicle
     */
    public void resetRoute(int vehicle) {
        solution.resetVisit(vehicle);
    }

    /**
     * reset the route of every vehicle
     */
    public void resetAllRoutes() {
        solution.resetAllVisits();
    }

    /**
     * set the last current visited node of a vehicle
     * @param vehicle vehicle that will get a newly visited node
     * @param node that will be visited by the vehicle
     */
    public void visit(int vehicle, int node) {
        solution.addVisit(vehicle, node);
    }

}
