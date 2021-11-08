package org.cpvisu.shapes;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.cpvisu.problems.SearchTreeNode;

public class GroupArea extends Pane {

    public GroupArea() {
        super();
    }

    public void add(VisualTextRectangle child) {
        this.getChildren().add(child);
    }

    public Shape getArea() {
        Shape area = getAreaFromParent(this);
        area.setTranslateX(this.getTranslateX() + area.getTranslateX());
        area.setTranslateY(this.getTranslateY() + area.getTranslateY());
        return area;
    }

    private Shape getAreaFromParent(Pane parent) {
        Shape area = new Rectangle();
        for (Node node : parent.getChildren()) {
            area = getArea(area, node, parent);
        }
        return area;
    }

    private Shape getAreaFromParent(Group parent) {
        Shape area = new Rectangle();
        for (Node node : parent.getChildren()) {
            area = getArea(area, node, parent);
        }
        return area;
    }

    private Shape getArea(Shape area, Node node, Parent parent) {
        if (node instanceof VisualNode) {
            area = mergeShape(area, ((VisualNode) node).getArea());
        } else if (node instanceof Shape) {
            if (!(node instanceof Line || node instanceof Polyline )) { // does not take lines into account
                area = mergeShape(area, ((Shape) node));
            }
        } else if (node instanceof Pane) {
            area = mergeShape(area, getAreaFromParent((Pane) node));
        } else if (node instanceof Group) {
            area = mergeShape(area, getAreaFromParent((Group) node));
        }
        return area;
    }

    private Shape mergeShape(Shape a, Shape b) {
        return Shape.union(a, b);
    }

    public void moveByX(double x) {
        double initX = getTranslateX();
        setTranslateX(initX + x);
    }

}
