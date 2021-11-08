package org.cpvisu.shapes;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.cpvisu.problems.SearchTreeNode;

public class GroupArea extends Group {

    public GroupArea() {
        super();
    }

    public void add(VisualTextRectangle child) {
        this.getChildren().add(child);
    }

    public Shape getArea() {
        return getAreaFromParent(this);
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
        if (node instanceof Shape) {
            area = mergeShape(area, (Shape) node, parent.getTranslateX(), parent.getTranslateY());
        } else if (node instanceof VisualNode) {
            area = mergeShape(area, ((VisualNode) node).getArea(), parent.getTranslateX(), parent.getTranslateY());
        } else if (node instanceof Pane) {
            area = mergeShape(area, getAreaFromParent((Pane) node), parent.getTranslateX(), parent.getTranslateY());
        } else if (node instanceof Group) {
            area = mergeShape(area, getAreaFromParent((Group) node), parent.getTranslateX(), parent.getTranslateY());
        }
        return area;
    }

    private Shape mergeShape(Shape a, Shape b) {
        return Shape.union(a, b);
    }

    private Shape mergeShape(Shape a, Shape b, double offsetX, double offsetY) {
        b.setTranslateX(b.getTranslateX() + offsetX);
        b.setTranslateY(b.getTranslateY() + offsetY);
        return Shape.union(a, b);
    }

    public void moveByX(double x) {
        setTranslateX(getTranslateX() + x);
    }

}
