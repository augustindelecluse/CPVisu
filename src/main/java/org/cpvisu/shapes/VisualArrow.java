package org.cpvisu.shapes;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

// based from https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane

/**
 * arrow composed of a line and a triangle at the end
 */
public class VisualArrow extends Group {

    private final Line line;
    private final Polygon triangle;

    public VisualArrow() {
        this(new Line(), new Polygon());
    }

    public VisualArrow(double startX, double startY, double endX, double endY) {
        this(new Line(startX, startY, endX, endY), new Polygon());
    }

    private double arrowLengthFactor = 0.025; // ratios for the drawing of the triangle
    private double arrowWidthFactor = 0.015;

    private VisualArrow(Line line, Polygon triangle) {
        super(line, triangle);
        this.line = line;
        this.triangle = triangle;

        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();
            triangle.getPoints().clear();

            if (!(ex == sx && ey == sy)) {
                double factor = arrowLengthFactor;
                double factorO = arrowWidthFactor;

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;
                triangle.getPoints().addAll(ex + dx - oy, ey + dy + ox, ex + dx + oy, ey + dy - ox, ex, ey);
            }
        };
        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) {
        line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

    public Line getMainLine() {
        return line;
    }

    public Polygon getTip() {
        return triangle;
    }

}
