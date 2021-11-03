package org.cpvisu.shapes;

import javafx.scene.shape.Shape;
import javafx.util.Duration;

public interface VisualShape {

    public void moveTo(double x, double y);

    public void moveTo(Duration duration, double x, double y);

    public Shape getShape();

    public double getX();

    public double getY();

    public double getCenterX();

    public double getCenterY();

}
