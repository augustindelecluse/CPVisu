package org.cpvisu;

import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Window;
import javafx.util.Duration;

public class AnimationFactory {

    /**
     * increment the x and y values of a node using an animation
     * @param node node to animate
     * @param duration duration for the animation
     * @param x increment for the x values
     * @param y increment for the y values
     */
    public static void moveBy(Node node, Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, node);
        translateTransition.setByX(x);
        translateTransition.setByY(y);
        translateTransition.play();
    }

    /**
     * increment the x and y values of a node using an animation
     * @param node node to animate
     * @param x increment for the x values
     * @param y increment for the y values
     */
    public static void moveBy(Node node, double x, double y) {
        moveBy(node, Duration.ONE, x, y);
    }

    /**
     * move a rectangle at a specified position using an animation
     * @param rectangle rectangle to move
     * @param duration duration of the animation
     * @param x new x coordinate after animation
     * @param y new y coordinate after animation
     */
    public static void moveTo(Rectangle rectangle, Duration duration, double x, double  y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, rectangle);
        translateTransition.setToX(x - rectangle.getX());
        translateTransition.setToY(y - rectangle.getY());
        translateTransition.play();
    }

    /**
     * move a circle at a specified position using an animation
     * @param circle circle to move
     * @param duration duration of the animation
     * @param x new x coordinate after animation
     * @param y new y coordinate after animation
     */
    public static void moveTo(Circle circle, Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, circle);
        translateTransition.setToX(x - circle.getCenterX());
        translateTransition.setToY(y - circle.getCenterY());
        translateTransition.play();
    }

    /**
     * move a rectangle at a specified position
     * @param rectangle rectangle to move
     * @param x new x coordinate after animation
     * @param y new y coordinate after animation
     */
    public static void moveTo(Rectangle rectangle, double x, double  y) {
        rectangle.setTranslateX(x - rectangle.getX());
        rectangle.setTranslateY(y - rectangle.getY());
    }

    public static void animate(Runnable action) {
        animate(0, e -> action.run(), 0, 1);
    }

    public static void animate(long duration, Runnable action) {
        animate(duration, e -> action.run(), 0, 1);
    }

    /**
     * loop forever on the given action
     * @param action function to call forever
     */
    public static void animateForever(Runnable action) {
        animate(1, e -> action.run(), 0, Timeline.INDEFINITE);
    }

    /**
     * loop forever on the given action, with a delay between 2 calls to the action
     * @param durationCycle waiting time between 2 actions
     * @param action function to call forever
     */
    public static void animateForever(long durationCycle, Runnable action) {
        animate(durationCycle, e -> action.run(), 0, Timeline.INDEFINITE);
    }

    /**
     * loop forever on the given action, with a delay between 2 calls to the action and a delay before starting the series of animation
     * @param durationCycle waiting time between 2 actions
     * @param action function to call forever
     * @param durationBefore waiting time before starting the whole series of animation
     */
    public static void animateForever(long durationCycle, Runnable action, long durationBefore) {
        animate(durationCycle, e -> action.run(), durationBefore, Timeline.INDEFINITE);
    }

    /**
     * animate the given action several times, with a delay between 2 calls to the action and a delay before starting the series of animation
     * @param durationCycle waiting time between 2 actions
     * @param action function to call
     * @param durationBefore waiting time before starting the whole series of animation
     * @param cycle number of times the animation should occur
     */
    public static void animate(long durationCycle, EventHandler<ActionEvent> action, long durationBefore, int cycle) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(durationCycle), action)
        );
        timeline.setCycleCount(cycle); // always play the animation
        timeline.setDelay(Duration.millis(durationBefore));
        timeline.play();
    }

    /**
     * auto resize a set of items according to the dimensions of the scene
     * changes both their scale and their position to fit to the scene
     * @param scene scene that might be resized
     * @param items items that will be resized whenever the dimensions of the scene change
     */
    public static void autoResize(Scene scene, Parent items) {
        double initWidth = scene.getWidth();
        double initHeight = scene.getHeight();

        // changes in width
        long threshold = 125;
        Duration delay = Duration.millis(500);
        ScaleTransition stX = new ScaleTransition(Duration.ONE, items);
        TranslateTransition ttX = new TranslateTransition(Duration.ONE, items);
        stX.setDelay(delay);
        ttX.setDelay(delay);

        scene.widthProperty().addListener((ObservableValue<? extends Number> obs, Number oldVal, Number newVal) -> {
            stX.stop();
            ttX.stop();
            stX.setToX(scene.getWidth() / initWidth);
            ttX.setToX((scene.getWidth() - initWidth) / 2);
            ttX.playFromStart();
            stX.playFromStart();
        });

        // changes in height
        ScaleTransition stY = new ScaleTransition(Duration.ONE, items);
        TranslateTransition ttY = new TranslateTransition(Duration.ONE, items);
        stY.setDelay(delay);
        ttY.setDelay(delay);

        scene.heightProperty().addListener((ObservableValue<? extends Number> obs, Number oldVal, Number newVal) -> {
            stY.stop();
            ttY.stop();
            stY.setToY(scene.getHeight()/ initHeight);
            ttY.setToY((scene.getHeight() - initHeight) / 2);
            stY.playFromStart();
            ttY.playFromStart();
        });
    }

    /**
     * auto resize all items within the scene according to its current dimensions
     * @param scene
     */
    public static void autoResize(Scene scene) {
        autoResize(scene, scene.getRoot());
    }

    /**
     * auto resize all items within the scene according to its current dimensions and preserving the x/y ratio if specified
     * @param scene scene that might be resized
     * @param preserveRatio true if the x/y ratio needs to be preserved
     */
    public static void autoResize(Scene scene, boolean preserveRatio) {
        autoResize(scene, scene.getRoot(), preserveRatio);
    }

    /**
     * auto resize all items within the scene according to its current dimensions and preserving the x/y ratio if specified
     * @param scene scene that might be resized
     * @param items items that will be resized whenever the dimensions of the scene change
     * @param preserveRatio true if the x/y ratio needs to be preserved
     */
    public static void autoResize(Scene scene, Parent items, boolean preserveRatio) {
        if (preserveRatio)
            autoResizePreserveRatio(scene, items);
        else
            autoResize(scene, items);
    }

    /**
     * auto resize all items within the scene according to its current dimensions and preserving the x/y ratio
     * @param scene scene that might be resized
     * @param items items that will be resized whenever the dimensions of the scene change
     */
    public static void autoResizePreserveRatio(Scene scene, Parent items) {
        double initWidth = scene.getWidth();
        double initHeight = scene.getHeight();
        // changes in width
        long threshold = 125;
        Duration delay = Duration.millis(500);
        ScaleTransition st = new ScaleTransition(Duration.ONE, items);
        TranslateTransition tt = new TranslateTransition(Duration.ONE, items);
        st.setDelay(delay);
        tt.setDelay(delay);

        ChangeListener<Number> listener = (observableValue, o, t1) -> {
            st.stop();
            st.stop();
            double scaling = Math.min(scene.getWidth() / initWidth, scene.getHeight()/ initHeight);
            st.setToX(scaling);
            st.setToY(scaling);
            double translation = Math.min((scene.getWidth() - initWidth) / 2, (scene.getHeight() - initHeight) / 2);
            tt.setToX(translation);
            tt.setToY(translation);
            tt.playFromStart();
            st.playFromStart();
        };
        scene.heightProperty().addListener(listener);
        scene.widthProperty().addListener(listener);
    }

    public static void moveOnDrag(Scene scene, Node itemsToMove) {
        new MoveOnDrag(scene, itemsToMove);
    }

    //public static void moveOnDrag(Node container, Node itemsToMove) {
    //    new MoveOnDrag(scene, itemsToMove);
    //}

    private static class MoveOnDrag {

        private Scene scene;
        private Node itemsToMove;
        private double mouseAnchorX; // used for the position when dragging nodes
        private double mouseAnchorY;
        private double canvasTranslateX;
        private double canvasTranslateY;

        public MoveOnDrag(Scene scene, Node itemsToMove) {
            this.scene = scene;
            this.itemsToMove = itemsToMove;
            registerDragListener();
        }

        /**
         * move all objects within the scene when a drag event occurs
         */
        private void registerDragListener() {
            scene.setOnMousePressed((MouseEvent event) -> { // register the initial position for the dragging
                mouseAnchorX = event.getSceneX();
                mouseAnchorY = event.getSceneY();
                canvasTranslateX = itemsToMove.getTranslateX();
                canvasTranslateY = itemsToMove.getTranslateY();
            });
            scene.setOnMouseDragged((MouseEvent event) -> { // move the objects in the scene
                if (event.isPrimaryButtonDown()) { // only drag using the primary button
                    itemsToMove.setTranslateX(canvasTranslateX + (event.getSceneX() - mouseAnchorX) / itemsToMove.getScaleX());
                    itemsToMove.setTranslateY(canvasTranslateY + (event.getSceneY() - mouseAnchorY) / itemsToMove.getScaleY());
                    event.consume();
                }
            });
        }
    }

}
