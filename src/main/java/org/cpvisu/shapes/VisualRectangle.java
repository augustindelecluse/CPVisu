package org.cpvisu.shapes;

import javafx.animation.TranslateTransition;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class VisualRectangle extends Rectangle implements VisualShape {

    public VisualRectangle() {
        super();
    }

    public VisualRectangle(double width, double height) {
        super(width, height);
    }

    public VisualRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public VisualRectangle(double width, double height, Paint fill){
        super(width, height, fill);
    }


    @Override
    public void moveTo(double x, double y) {
        moveTo(Duration.ONE, x, y);
    }

    @Override
    public void moveTo(Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, this);
        translateTransition.setToX(x - this.getX());
        translateTransition.setToY(y - this.getY());
        translateTransition.play();
    }

    @Override
    public Shape getShape() {
        return this;
    }

    @Override
    public double getCenterX() {
        return this.getX() + getWidth() / 2;
    }

    @Override
    public double getCenterY() {
        return this.getY() + getHeight() / 2;
    }
}
