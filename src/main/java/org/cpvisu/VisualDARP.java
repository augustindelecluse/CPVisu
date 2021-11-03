package org.cpvisu;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.cpvisu.problems.DARPInstance;
import org.cpvisu.problems.DARPNode;
import org.cpvisu.shapes.VisualCircle;
import org.cpvisu.shapes.VisualRectangle;
import org.cpvisu.shapes.VisualShape;
import org.cpvisu.util.colors.ColorFactory;

import java.util.function.Function;

public class VisualDARP {

    private DARPInstance darp;
    private Function<DARPNode, VisualShape> drawingFunction;
    private Insets innerBorder; // strict area where all nodes are included
    private double threshold = 20; // difference in coordinates between the scene and the effective drawing of the nodes
    private Group group;
    private VisualShape[] shapes;
    private Group shapesGroup;
    private int width;
    private int height;
    private Rectangle background;

    private double initDragX;
    private double initDragY;

    public VisualDARP(DARPInstance darp, int width, int height, Function<DARPNode, VisualShape> drawingFunction) {
        this.darp = darp;
        this.drawingFunction = drawingFunction;
        this.width = width;
        this.height = height;
    }

    public VisualDARP(DARPInstance darp, int width, int height) {
        this(darp, width, height, VisualDARP::DefaultMapping);
    }

    private static VisualShape DefaultMapping(DARPNode node) {
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

    public Group init() {
        Shape[] shapes = new Shape[darp.getNNodes()];
        this.shapes = new VisualShape[shapes.length];
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
                VisualShape shape = drawingFunction.apply(node);
                double x = ((node.getX() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
                double y = ((node.getY() - min) / (max - min)) * (minWindow - 2 * threshold) + threshold;
                TranslateTransition translateTransition = new TranslateTransition(Duration.ONE, shape.getShape());
                translateTransition.setToX(x);
                translateTransition.setToY(y);
                translateTransition.play();
                this.shapes[i] = shape;
                shapes[i++] = shape.getShape();
            }
        }
        innerBorder = new Insets(threshold, threshold, width, height);
        background = new Rectangle(0, 0, width, height);
        background.setFill(Color.WHITE);
        shapesGroup = new Group(shapes);
        group = new Group(background, shapesGroup);
        //registerScrollListener();
        registerDraggListener();
        return group;
    }

    /**
     * move all objects within the scene when dragged from the mouse
     */
    public void registerDraggListener() {
        group.setOnMousePressed((MouseEvent event) -> { // register the initial position for the dragging
            initDragX = event.getSceneX();
            initDragY = event.getSceneY();
        });
        group.setOnMouseDragged((MouseEvent event) -> { // move the objects in the scene
            event.consume();
            TranslateTransition translateTransition = new TranslateTransition(Duration.ONE, shapesGroup);
            translateTransition.setByX(event.getSceneX() - initDragX);
            translateTransition.setByY(event.getSceneY() - initDragY);
            translateTransition.play();
            initDragX = event.getSceneX();
            initDragY = event.getSceneY();
        });
    }

    public void registerScrollListener() {
        group.setOnScroll((ScrollEvent event) -> {
            double mouseX = event.getSceneX(); // get the coordinates of the mouse
            double mouseY = event.getSceneY();
            double delta = event.getDeltaY() / 100;
            double deltaScale = delta;
            if (deltaScale != 0.)
                System.out.println("delta scale =" + deltaScale);
            int i =0;
            if (delta != 0) {
                for (VisualShape visualShape : shapes) {
                    // compute the distance between the shape and the mouse
                    Shape shape = visualShape.getShape();
                    double dx = (visualShape.getCenterX() + shape.getTranslateX() - mouseX);
                    double dy = (visualShape.getCenterY() + shape.getTranslateY() - mouseY);
                    dx = dx * delta;
                    dy = dy * delta;
                    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(150), shape);
                    translateTransition.setByX(dx);
                    translateTransition.setByY(dy);
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), shape);
                    if (shape.getScaleX() + deltaScale > 0) { // prevent to have negative scales
                        scaleTransition.setByX(deltaScale);
                        scaleTransition.setByY(deltaScale);
                    }
                    ParallelTransition parallelTransition = new ParallelTransition(translateTransition);
                    parallelTransition.play();
                }
            }
            event.consume();
        } );
    }

}
