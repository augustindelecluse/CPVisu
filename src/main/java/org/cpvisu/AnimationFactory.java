package org.cpvisu;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;

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

    public static void moveTo(Rectangle rectangle, double x, double  y) {
        moveTo(rectangle, Duration.ONE, x, y);
    }

    public static void animate(Runnable action) {
        animate(0, e -> action.run(), 0, 1);
    }

    public static void animate(long durationBefore, Runnable action) {
        animate(durationBefore, e -> action.run(), 0, 1);
    }

    /**
     * loop forever on the given action
     * @param action function to call forever
     */
    public static void animateForever(Runnable action) {
        animate(0, e -> action.run(), 0, Timeline.INDEFINITE);
    }

    /**
     * loop forever on the given action, with a delay between 2 calls to the action
     * @param durationBefore waiting time between 2 actions
     * @param action function to call forever
     */
    public static void animateForever(long durationBefore, Runnable action) {
        animate(durationBefore, e -> action.run(), 0, Timeline.INDEFINITE);
    }


    public static void animate(long durationBefore, EventHandler<ActionEvent> action, long durationAfter, int cycle) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(durationBefore)),
                new KeyFrame(Duration.ONE, action),
                new KeyFrame(Duration.millis(durationAfter))
        );
        timeline.setCycleCount(cycle); // always play the animation
        timeline.play();
    }

    /**
     * gives a random color
     * @return random color
     */
    public static Color randomColor() {
        int red = ThreadLocalRandom.current().nextInt(255);
        int green = ThreadLocalRandom.current().nextInt(255);
        int blue = ThreadLocalRandom.current().nextInt(255);
        return Color.rgb(red, green, blue);
    }

}
