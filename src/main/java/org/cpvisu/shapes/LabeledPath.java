package org.cpvisu.shapes;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LabeledPath extends Pane implements VisualNode {

    private Text description;
    private Polyline polyline;

    /**
     * create a path with a label at the end of the path
     * @param xSpacing x spacing between the last x coordinate of the path and the label
     * @param ySpacing y spacing between the last y coordinate of the path and the label
     * @param description label that needs to be added at the end of the path
     * @param es x and y values for the path
     */
    public LabeledPath(double xSpacing, double ySpacing, String description, double... es) {
        super();
        polyline = new Polyline(es);
        this.getChildren().add(polyline);
        if (description != null && description.length() > 0) {
            int labelCoordinates = es.length - 2;
            this.description = new Text(es[labelCoordinates] + xSpacing, es[labelCoordinates + 1] + ySpacing, description);
        } else {
            this.description = new Text();
        }
        this.getChildren().add(this.description);
    }

    /**
     * create a path with a label at the end of the path
     * @param description label that needs to be added at the end of the path
     * @param es x and y values for the path
     */
    public LabeledPath(String description, double... es) {
        this(5, -5, description, es);
    }

    @Override
    public void moveTo(double x, double y) {
        this.setTranslateX(getTranslateX() + x);
        this.setTranslateY(getTranslateY() + y);
    }

    @Override
    public void moveTo(Duration duration, double x, double y) {

    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getCenterX() {
        return 0;
    }

    @Override
    public double getCenterY() {
        return 0;
    }

    @Override
    public Shape getArea() {
        return description;
    }

    @Override
    public void setFill(Paint fill) {

    }

}
