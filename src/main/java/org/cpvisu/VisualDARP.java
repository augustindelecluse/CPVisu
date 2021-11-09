package org.cpvisu;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.cpvisu.problems.DARPInstance;
import org.cpvisu.problems.DARPNode;
import org.cpvisu.shapes.VisualCircle;
import org.cpvisu.shapes.VisualRectangle;
import org.cpvisu.shapes.VisualNode;
import org.cpvisu.util.colors.ColorFactory;

import java.util.ArrayList;
import java.util.Arrays;
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

    private double mouseAnchorX; // used for the position when dragging nodes
    private double mouseAnchorY;
    private double canvasTranslateX;
    private double canvasTranslateY;

    public VisualDARP(DARPInstance darp, int width, int height, Function<DARPNode, VisualNode> drawingFunction) {
        this.darp = darp;
        this.drawingFunction = drawingFunction;
        this.width = width;
        this.height = height;
        nVehicle = darp.getNVehicle();
        visitedBy = new ArrayList[nVehicle];
        for (int i = 0 ; i < nVehicle; ++i)
            visitedBy[i] = new ArrayList<>();
    }

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
            circle.setFill(Color.GRAY);
            return circle;
        } else if (node.isPickup()) {
            VisualCircle circle = new VisualCircle(node.getX(), node.getY(), radius);
            circle.setFill(ColorFactory.getPalette("default").colorAt(node.getId()));
            return circle;
        } else if (node.isDrop()) {
            double x = node.getX() - radius; // center the point
            double y = node.getY() - radius;
            VisualRectangle rectangle = new VisualRectangle(x, y, radius * 2, radius * 2);
            rectangle.setFill(ColorFactory.getPalette("default").colorAt(node.getId()));
            return rectangle;
        }
        return null;
    }

    /**
     * draw the nodes on the interface
     * @return pane containing the nodes
     */
    public Pane nodeLayout() {
        Node[] shapes = new Node[darp.getNNodes()];
        this.shapes = new VisualNode[shapes.length];
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        int i=0;
        // register the bounds for the coordinates
        for (DARPNode[] nodeList: new DARPNode[][]{darp.getBeginDepot(), darp.getNodes(), darp.getEndDepot()}) {
            for (DARPNode node: nodeList) {
                minX = Math.min(minX, node.getX());
                minY = Math.min(minY, node.getY());
                maxX = Math.max(maxX, node.getX());
                maxY = Math.max(maxY, node.getY());
            }
        }
        // keep the lowest ratio for the transformation: the coordinate system should remain isometric
        double min = Math.min(minX, minY);
        double max = Math.max(maxX, maxY);
        double minWindow = Math.min(width, height);
        // space out the shapes so that they occupy the whole screen
        for (DARPNode[] nodeList: new DARPNode[][]{darp.getBeginDepot(), darp.getNodes(), darp.getEndDepot()}) {
            for (DARPNode node: nodeList) {
                VisualNode shape = drawingFunction.apply(node);
                double x = ((node.getX() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
                double y = ((node.getY() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
                TranslateTransition translateTransition = new TranslateTransition(Duration.ONE, shape.getNode());
                translateTransition.setToX(x);
                translateTransition.setToY(y);
                translateTransition.play();
                this.shapes[i] = shape;
                shapes[i++] = shape.getNode();
            }
        }
        innerBorder = new Insets(threshold, threshold, width, height);
        shapesGroup = new Pane(shapes);
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
    public Pane GanttLayout(int vehicle) {
        // iterate over the current nodes

        return null;
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
