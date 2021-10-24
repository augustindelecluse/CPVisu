package org.cpvisu;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
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
        translateTransition.setToX(x);
        translateTransition.setToY(y);
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

}
