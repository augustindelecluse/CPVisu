package org.cpvisu.shapes;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

// based from https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane

/**
 * arrow composed of a line and a triangle at the end
 */
public class VisualArrow extends Group {

    private final InvalidationListener updater;
    private final Line line;
    private final Polygon triangle;

    public VisualArrow() {
        this(new Line(), new Polygon(), 1.);
    }

    public VisualArrow(double startX, double startY, double endX, double endY) {
        this(new Line(startX, startY, endX, endY), new Polygon(), 1.);
    }

    public VisualArrow(double startX, double startY, double endX, double endY, double position) {
        this(new Line(startX, startY, endX, endY), new Polygon(), position);
    }

    /**
     * create a visual arrow
     * @param startX starting x point for the line of the arrow
     * @param startY starting y point for the line of the arrow
     * @param endX ending x point for the line of the arrow
     * @param endY ending y point for the line of the arrow
     * @param position position of the triangle along the line. 0 -> arrow at the beginning of the line, 1 -> at the end of the line
     * @param minArrowWidth minimum width for the triangle
     * @param maxArrowWidth maximum width for the triangle
     * @param minArrowLength minimum length for the triangle
     * @param maxArrowLength maximum length for the triangle
     */
    public VisualArrow(double startX, double startY, double endX, double endY, double position,
                       double minArrowWidth, double maxArrowWidth, double minArrowLength, double maxArrowLength) {
        this(new Line(startX, startY, endX, endY), new Polygon(), position);
        this.minArrowWidth = minArrowWidth;
        this.maxArrowWidth = maxArrowWidth;
        this.minArrowLength = minArrowLength;
        this.maxArrowLength = maxArrowLength;
        updater.invalidated(null);
    }

    private double arrowLengthFactor = 0.025; // ratios for the drawing of the triangle, applicable if possible
    private double arrowWidthFactor = 0.0125;

    private double maxArrowWidth = 5; // max width for the arrow
    private double minArrowWidth = 5; // min width for the arrow
    private double maxArrowLength = 10; // max length for the arrow
    private double minArrowLength = 10; // min length for the arrow

    /**
     * create a visual arrow
     * @param line line composing the arrow
     * @param triangle triangle where the tip of the arrow will be added
     * @param position position of the triangle along the line. 0 -> arrow at the beginning of the line, 1 -> at the end of the line
     */
    private VisualArrow(Line line, Polygon triangle, double position) {
        super(line, triangle);
        assert(position >= 0. && position <= 1.);
        this.line = line;
        this.triangle = triangle;

        updater = o -> {
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

                double xOrigin = (ex-sx) * position + sx;
                double yOrigin = (ey-sy) * position + sy;

                double length = Math.hypot(dx, dy);
                if (length > maxArrowLength) {
                    dx = dx / (length / maxArrowLength);
                    dy = dy / (length / maxArrowLength);
                } else if (length < minArrowLength) {
                    dx = dx * (minArrowLength / length);
                    dy = dy * (minArrowLength / length);
                }

                double width = Math.hypot(ox, oy);
                if (width > maxArrowWidth) {
                    ox = ox / (width / maxArrowWidth);
                    oy = oy / (width / maxArrowWidth);
                } else if (width < minArrowWidth) {
                    ox = ox * (minArrowWidth / width);
                    oy = oy * (minArrowWidth / width);
                }

                triangle.getPoints().addAll(
                         xOrigin + dx - oy,
                         yOrigin + dy + ox,
                        xOrigin + dx + oy,
                        yOrigin + dy - ox,
                        xOrigin,
                        yOrigin);

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

    public double getMaxArrowWidth() {
        return maxArrowWidth;
    }

    public void setMaxArrowWidth(double maxArrowWidth) {
        if (this.maxArrowWidth != maxArrowWidth) {
            this.maxArrowWidth = maxArrowWidth;
            updater.invalidated(null);
        }
    }

    public double getMinArrowWidth() {
        return minArrowWidth;
    }

    public void setMinArrowWidth(double minArrowWidth) {
        if (this.minArrowWidth != minArrowWidth) {
            this.minArrowWidth = minArrowWidth;
            updater.invalidated(null);
        }
    }

    public double getMaxArrowLength() {
        return maxArrowLength;
    }

    public void setMaxArrowLength(double maxArrowLength) {
        if (this.maxArrowLength != maxArrowLength) {
            this.maxArrowLength = maxArrowLength;
            updater.invalidated(null);
        }
    }

    public double getMinArrowLength() {
        return minArrowLength;
    }

    public void setMinArrowLength(double minArrowLength) {
        if (minArrowLength != this.minArrowLength) {
            this.minArrowLength = minArrowLength;
            updater.invalidated(null);
        }
    }

    public Line getMainLine() {
        return line;
    }

    public Polygon getTip() {
        return triangle;
    }

}
