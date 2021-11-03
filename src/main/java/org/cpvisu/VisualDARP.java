package org.cpvisu;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
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
    private Scene scene;

    private double mouseAnchorX; // used for the position when dragging nodes
    private double mouseAnchorY;
    private double canvasTranslateX;
    private double canvasTranslateY;

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

    public Scene init() {
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
        scene = new Scene(group, width, height);
        registerScrollListener();
        registerDragListener();
        return scene;
    }

    /**
     * move all objects within the scene when a drag event occurs
     */
    public void registerDragListener() {
        scene.setOnMousePressed((MouseEvent event) -> { // register the initial position for the dragging
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            canvasTranslateX = shapesGroup.getTranslateX();
            canvasTranslateY = shapesGroup.getTranslateY();
        });
        scene.setOnMouseDragged((MouseEvent event) -> { // move the objects in the scene
            if (event.isPrimaryButtonDown()) { // only drag using the primary button
                shapesGroup.setTranslateX(canvasTranslateX + event.getSceneX() - mouseAnchorX);
                shapesGroup.setTranslateY(canvasTranslateY + event.getSceneY() - mouseAnchorY);
                event.consume();
            }
        });
    }

    /**
     * zoom in-out based on the mouse current position
     */
    public void registerScrollListener() {
        scene.setOnScroll((ScrollEvent event) -> {
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
                for (VisualShape visualShape: shapes) {
                    Shape shape = visualShape.getShape();
                    double oldScale = shape.getScaleX();
                    double scale = oldScale * factor;
                    double f = (scale / oldScale) - 1;

                    // determine offset that we will have to move the node
                    Bounds bounds = shape.localToScene(shape.getBoundsInLocal());
                    double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
                    double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

                    // timeline that scales and moves the node
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().clear();
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(200), new KeyValue(shape.translateXProperty(), shape.getTranslateX() - f * dx)),
                            new KeyFrame(Duration.millis(200), new KeyValue(shape.translateYProperty(), shape.getTranslateY() - f * dy)),
                            new KeyFrame(Duration.millis(200), new KeyValue(shape.scaleXProperty(), scale)),
                            new KeyFrame(Duration.millis(200), new KeyValue(shape.scaleYProperty(), scale))
                    );
                    timeline.play();
                }
            }
        });
    }
}
