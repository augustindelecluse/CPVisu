package org.cpvisu;

import javafx.animation.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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

    public static void moveBy(Node node, double x, double y) {
        moveBy(node, Duration.ONE, x, y);
    }

    /**
     * move a rectangle at a specified position
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

    public static void moveTo(Circle circle, Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, circle);
        translateTransition.setToX(x - circle.getCenterX());
        translateTransition.setToY(y - circle.getCenterY());
        translateTransition.play();
    }

    public static void moveTo(Rectangle rectangle, double x, double  y) {
        moveTo(rectangle, Duration.ONE, x, y);
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

    public static void animateForever(long durationCycle, Runnable action, long durationBefore) {
        animate(durationCycle, e -> action.run(), durationBefore, Timeline.INDEFINITE);
    }

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
            System.out.println("width change");
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
            System.out.println("height change");
        });


    }

}
