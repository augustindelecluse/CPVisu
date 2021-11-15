package org.cpvisu;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.cpvisu.chart.DARPGanttChart;
import org.cpvisu.problems.DARPInstance;
import org.cpvisu.problems.DARPNode;
import org.cpvisu.shapes.*;
import org.cpvisu.util.colors.ColorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

/**
 * visualisation for a dial-a-ride problem
 * no checking of satisfiability occurs
 */
public class VisualDARP {

    private DARPInstance darp;
    private Function<DARPNode, VisualNode> drawingFunction;
    private Insets innerBorder; // strict area where all nodes are included
    private double threshold = 20; // difference in coordinates between the scene and the effective drawing of the nodes
    private Pane pane;
    private VisualNode[] shapes;
    private Pane shapesGroup;
    private int width;
    private int height;
    private ArrayList<Integer>[] visitedBy; // contains the list of visited node for every vehicle
    private int nVehicle;
    private DARPNode[] nodeList;
    private int selectedVehicle = 0; // current selected vehicle for the visualisation

    private double mouseAnchorX; // used for the position when dragging nodes
    private double mouseAnchorY;
    private double canvasTranslateX;
    private double canvasTranslateY;

    private final Color unselectedNode = Color.rgb(125, 125, 125, 0.8);
    private final Color selectedRoute = Color.rgb(128,0,0,0.7);

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
        nVehicle = darp.getNVehicle();
        visitedBy = new ArrayList[nVehicle];
        for (int i = 0 ; i < nVehicle; ++i)
            visitedBy[i] = new ArrayList<>();
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
            VisualCircle circle = new VisualCircle(node.getX(), node.getY(), radius);
            return circle;
        } else if (node.isPickup()) {
            VisualCircle circle = new VisualCircle(node.getX(), node.getY(), radius);
            return circle;
        } else if (node.isDrop()) {
            double x = node.getX() - radius; // center the point
            double y = node.getY() - radius;
            VisualRectangle rectangle = new VisualRectangle(x, y, radius * 2, radius * 2);
            return rectangle;
        }
        return null;
    }

    /**
     * draw the nodes on the interface
     * color the nodes in grey if they are not visited by the current vehicle, or in color otherwise
     * @return pane containing the nodes
     */
    public Pane nodeLayout() {
        Node[] shapes = new Node[darp.getNNodes()];
        VisualNode[] visualNode = new VisualNode[shapes.length];
        this.shapes = new VisualNode[shapes.length];
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
        HashSet<Integer> selectedRoute = new HashSet<>(visitedBy[selectedVehicle]);
        // space out the shapes so that they occupy the whole screen
        for (DARPNode node: nodeList) {
            VisualNode shape = drawingFunction.apply(node);
            double x = ((node.getX() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
            double y = ((node.getY() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
            shape.moveTo(x, y);
            int i = node.getId();
            this.shapes[i] = shape;
            visualNode[i] = shape;
            if (!selectedRoute.contains(i))
                visualNode[i].setFill(unselectedNode);
            else
                visualNode[i].setFill(ColorFactory.getPalette("default").colorAt(node.getId()));
            shapes[i] = shape.getNode();
            // click listener to provide information related to the node
            shapes[i].setOnMousePressed(e -> {
                System.out.println(node);
            });
        }
        // plot the transition between the nodes
        Pane transitions = new Pane();
        transitions.getStylesheets().add(getClass().getResource("visual-darp.css").toExternalForm());
        for (ArrayList<Integer> parcours: visitedBy) {
            int pred = 0;
            int j = 0;
            for (int succ: parcours) {
                if (j != 0) {
                    // plot the transition between pred and succ
                    double xFrom = visualNode[pred].getCenterX() + shapes[pred].getTranslateX();
                    double yFrom = visualNode[pred].getCenterY() + shapes[pred].getTranslateY();
                    double xTo = visualNode[succ].getCenterX() + shapes[succ].getTranslateX();
                    double yTo = visualNode[succ].getCenterY() + shapes[succ].getTranslateY();
                    VisualArrow myVisualArrow = new VisualArrow(xFrom, yFrom, xTo, yTo);
                    myVisualArrow.getMainLine().getStyleClass().add("node-layout-transition-line");
                    myVisualArrow.getTip().getStyleClass().add("node-layout-transition-tip");
                    transitions.getChildren().add(myVisualArrow);
                }
                ++j;
                pred = succ;
            }
        }
        innerBorder = new Insets(threshold, threshold, width, height);
        shapesGroup = new Pane(transitions, new Pane(shapes));
        pane = new Pane(shapesGroup);
        pane.setPrefHeight(height);
        pane.setPrefWidth(width);
        registerScrollListener();
        registerDragListener();
        return pane;
    }

    /**
     * gives the Gantt layout associated to a vehicle
     * @return Gantt layout for a vehicle
     */
    public DARPGanttChart GanttLayout(int vehicle) {
        // iterate over the current nodes
        String[] nodes = new String[visitedBy[vehicle].size()];
        Integer[] ids = new Integer[nodes.length];
        visitedBy[vehicle].toArray(ids);
        DARPNode[] nodesVisited = getNodesWithId(ids); // retrieve the visited nodes
        for (int i = 0; i < nodes.length ; ++i) {
            //nodes[i] = String.format("node %d", nodesVisited[i].getId());
            nodes[i] = String.format("node %d", ids[i]);
        }
        double[][] timeVisited = new double[nodes.length][2];  // set the time visited for each node
        // the first node should be the depot, set the starting time visited from here
        double timeReached = nodesVisited[0].getTwStart();
        timeVisited[0][0] = timeReached;
        timeReached += nodesVisited[0].getServingDuration();
        for (int i = 1; i < nodes.length ; ++i) {
            timeReached = timeReached + darp.getDistance(nodesVisited[i-1], nodesVisited[i]);
            // waiting at a node is allowed
            timeReached = Math.max(timeReached, nodesVisited[i].getTwStart());
            timeVisited[i][0] = timeReached;
            // add the service duration
            timeReached += nodesVisited[i].getServingDuration();
        }
        // set the ending time visited
        timeReached = nodesVisited[nodes.length-1].getTwEnd();
        timeVisited[nodes.length-1][1] = timeReached;
        for (int i = nodes.length-2; i >= 0 ; --i) {
            // TODO check for max service time
            timeReached = timeReached - darp.getDistance(nodesVisited[i+1], nodesVisited[i]);
            timeReached = Math.min(timeReached, nodesVisited[i].getTwEnd());
            timeVisited[i][1] = timeReached;
        }
        // create the chart
        // TODO check that the max route time is respected
        DARPGanttChart chart = DARPGanttChart.fromCategories(nodes);
        chart.setBlockHeight(20);
        double timeArrival = Math.max(timeVisited[0][0], nodesVisited[0].getTwStart()) + nodesVisited[0].getServingDuration() + darp.getDistance(nodesVisited[0], nodesVisited[1]);
        chart.setTimeSlot(nodes[0], nodesVisited[0].getTwStart(), nodesVisited[0].getTwEnd(), timeVisited[0][0], timeVisited[0][1]);
        chart.setTransition(nodes[0], Math.max(timeVisited[0][0], nodesVisited[0].getTwStart()) + nodesVisited[0].getServingDuration(), darp.getDistance(nodesVisited[0], nodesVisited[1]));

        for (int i = 1; i < nodes.length ; ++i) {
            System.out.println(String.format("%s : from %f -> %f", nodes[i], timeVisited[i][0], timeVisited[i][1]));
            chart.setTimeSlot(nodes[i], nodesVisited[i].getTwStart(), nodesVisited[i].getTwEnd(), timeVisited[i][0], timeVisited[i][1], timeArrival);
            if (i < nodes.length-1) {
                chart.setTransition(nodes[i], Math.max(timeVisited[i][0], nodesVisited[i].getTwStart()) + nodesVisited[i].getServingDuration(), darp.getDistance(nodesVisited[i], nodesVisited[i + 1]));
                timeArrival = Math.max(timeVisited[i][0], nodesVisited[i].getTwStart()) + nodesVisited[i].getServingDuration() + darp.getDistance(nodesVisited[i], nodesVisited[i+1]);
            }
        }
        return chart;
    }

    /**
     * retrieve the given nodes using their ids
     * @param ids ids of the nodes we want to retrieve
     * @return nodes with the corresponding ids
     */
    private DARPNode[] getNodesWithId(Integer... ids) {
        DARPNode[] nodes = new DARPNode[ids.length];
        /*
        HashMap<Integer, Integer> nodeMapping = new HashMap();
        for (int i = 0; i < ids.length; ++i)
            nodeMapping.put(ids[i], i);
        // iterate over the nodes provided
        for (DARPNode node: nodeList) {
            if (nodeMapping.containsKey(node.getId())) {
                nodes[nodeMapping.get(node.getId())] = node;
            }
        }
         */
        for (int i = 0; i < ids.length ; ++i)
            nodes[i] = nodeList[ids[i]];
        return nodes;
    }

    /**
     * add a route for a given vehicle
     * does not reset the exiting route: the sequence of nodes will be added to the route
     * @param vehicle vehicle whose route needs to be added
     * @param visitedOrder order of visit for each node
     */
    public void addRoute(int vehicle, Integer... visitedOrder) {
        visitedBy[vehicle].addAll(Arrays.asList(visitedOrder));
    }

    /**
     * reset the route of a vehicle
     * @param vehicle
     */
    public void resetRoute(int vehicle) {
        visitedBy[vehicle].clear();
    }

    /**
     * reset the route of every vehicle
     */
    public void resetAllRoutes() {
        for (int vehicle = 0 ; vehicle < nVehicle ; ++vehicle)
            resetRoute(vehicle);
    }

    /**
     * set the last current visited node of a vehicle
     * @param vehicle vehicle that will get a newly visited node
     * @param node that will be visited by the vehicle
     */
    public void visit(int vehicle, int node) {
        visitedBy[vehicle].add(node);
    }

    /**
     * gives the nodes visited by a given vehicle, in order
     * @param vehicle vehicle whose visit order needs to be known
     * @return order of visit of the nodes for this vehicle
     */
    public ArrayList<Integer> getVehicleVisit(int vehicle) {
        return visitedBy[vehicle];
    }

    /**
     * move all objects within the scene when a drag event occurs
     */
    public void registerDragListener() {
        pane.setOnMousePressed((MouseEvent event) -> { // register the initial position for the dragging
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            canvasTranslateX = shapesGroup.getTranslateX();
            canvasTranslateY = shapesGroup.getTranslateY();
        });
        pane.setOnMouseDragged((MouseEvent event) -> { // move the objects in the scene
            if (event.isPrimaryButtonDown()) { // only drag using the primary button
                shapesGroup.setTranslateX(canvasTranslateX + (event.getSceneX() - mouseAnchorX) / pane.getScaleX());
                shapesGroup.setTranslateY(canvasTranslateY + (event.getSceneY() - mouseAnchorY) / pane.getScaleY());
                event.consume();
            }
        });
    }

    /**
     * zoom in-out based on the mouse current position
     */
    public void registerScrollListener() {
        pane.setOnScroll((ScrollEvent event) -> {
            if (event.getDeltaY() != 0) {
                event.consume();
                double factor = 1.5;
                //double x = ((double) width) / 2; // centered zoom
                //double y = ((double) height) / 2;
                double x = event.getSceneX();
                double y = event.getSceneY();
                if (event.getDeltaY() < 0) {
                    factor = 1.0 / factor;
                }
                double oldScale = pane.getScaleX();
                double scale = oldScale * factor;
                double f = (scale / oldScale) - 1;

                // determine offset that we will have to move the group
                Bounds bounds = pane.localToScene(pane.getBoundsInLocal());
                double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
                double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

                // timeline that scales and moves the group
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().clear();
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(150), new KeyValue(pane.translateXProperty(), pane.getTranslateX() - f * dx)),
                        new KeyFrame(Duration.millis(150), new KeyValue(pane.translateYProperty(), pane.getTranslateY() - f * dy)),
                        new KeyFrame(Duration.millis(150), new KeyValue(pane.scaleXProperty(), scale)),
                        new KeyFrame(Duration.millis(150), new KeyValue(pane.scaleYProperty(), scale))
                );
                timeline.play();
            }
        });
    }
}
