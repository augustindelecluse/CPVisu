package org.cpvisu.shapes;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class VisualCircle extends Circle implements VisualNode {

    public VisualCircle() {
        super();
    }

    public VisualCircle(double radius) {
        super(radius);
    }

    public VisualCircle(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
    }

    public VisualCircle(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
    }

    public VisualCircle(double radius, Paint fill) {
        super(radius, fill);
    }

    @Override
    public void moveTo(double x, double y) {
        this.setTranslateX(x - this.getCenterX());
        this.setTranslateY(y - this.getCenterY());
    }

    @Override
    public void moveTo(Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, this);
        translateTransition.setToX(x - this.getCenterX());
        translateTransition.setToY(y - this.getCenterY());
        translateTransition.play();
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public double getX() {
        return this.getCenterX() - getRadius();
    }

    @Override
    public double getY() {
        return this.getCenterY() - getRadius();
    }

    @Override
    public double getHeight() {
        return getRadius() * 2;
    }

    @Override
    public double getWidth() {
        return getRadius() * 2;
    }

    @Override
    public Shape getArea() {
        return this;
    }

}
