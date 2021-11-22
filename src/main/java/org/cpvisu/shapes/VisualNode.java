package org.cpvisu.shapes;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public interface VisualNode {

    public void moveTo(double x, double y);

    public void moveTo(Duration duration, double x, double y);

    public Node getNode();

    public double getX();

    public double getY();

    public double getCenterX();

    public double getCenterY();

    public double getHeight();

    public double getWidth();

    public Shape getArea();

    public void setFill(Paint fill);

    public void setStroke(Paint color);

}
