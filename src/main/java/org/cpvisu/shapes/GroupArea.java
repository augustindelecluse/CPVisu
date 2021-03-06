package org.cpvisu.shapes;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static org.cpvisu.shapes.ShapeOperation.*;

public class GroupArea extends Pane {

    public GroupArea() {
        super();
    }

    public void add(VisualTextRectangle child) {
        this.getChildren().add(child);
    }

    /**
     * compute the area of all elements included in the group
     * current computation takes into account area from
     * - Pane
     * - Group
     * - Shape (except line and polyline)
     * - VisualNode
     * @return area relative to this node and its component
     */
    public Shape getArea() {
        Shape area = getAreaFromParent(this);
        //area.setTranslateX(this.getTranslateX() + area.getTranslateX());
        //area.setTranslateY(this.getTranslateY() + area.getTranslateY());
        return area;
    }

    private Shape getAreaFromParent(Pane parent) {
        Shape area = new Rectangle();
        for (Node node : parent.getChildren()) {
            area = getArea(area, node, parent.getTranslateX(), parent.getTranslateY());
        }
        return area;
    }

    private Shape getAreaFromParent(Group parent) {
        Shape area = new Rectangle();
        for (Node node : parent.getChildren()) {
            area = getArea(area, node, parent.getTranslateX(), parent.getTranslateY());
        }
        return area;
    }

    private Shape getArea(Shape area, Node node, Parent parent) {
        if (node instanceof VisualNode) {
            area = mergeShape(area, ((VisualNode) node).getArea());
        } else if (node instanceof Shape) {
            if (!(node instanceof Line || node instanceof Polyline)) { // does not take lines into account
                area = mergeShape(area, ((Shape) node));
            }
        } else if (node instanceof Pane) {
            area = mergeShape(area, getAreaFromParent((Pane) node));
        } else if (node instanceof Group) {
            area = mergeShape(area, getAreaFromParent((Group) node));
        }
        return area;
    }

    public Shape getArea(Shape area, Node node, double offsetX, double offsetY) {
        if (node instanceof VisualNode) {
            area = mergeShape(area, ((VisualNode) node).getArea(), offsetX, offsetY);
        } else if (node instanceof Shape) {
            if (!(node instanceof Line || node instanceof Polyline)) { // does not take lines into account
                area = mergeShape(area, ((Shape) node), offsetX, offsetY);
            }
        } else if (node instanceof Pane) {
            area = mergeShape(area, getAreaFromParent((Pane) node), offsetX + node.getTranslateX(), offsetY + node.getTranslateY());
        } else if (node instanceof Group) {
            area = mergeShape(area, getAreaFromParent((Group) node), offsetX + node.getTranslateX(), offsetY + node.getTranslateY());
        }
        area.setTranslateX(offsetX);
        area.setTranslateY(offsetY);
        return area;
    }

    private Shape mergeShape(Shape a, Shape b) {
        return Shape.union(a, b);
    }

    private Shape mergeShape(Shape a, Shape b, double bOffsetX, double bOffsetY) {
        Shape bOffset = ShapeCopy(b);
        bOffset.setTranslateX(bOffsetX);
        bOffset.setTranslateX(bOffsetY);
        return Shape.union(a, bOffset);
    }

    public void moveByX(double x) {
        double initX = getTranslateX();
        setTranslateX(initX + x);
    }

    /**
     * gives the value of the current head of the element, considered as a VisualTextRectangle
     * @return x value for the head. 0 if multiple heads exist
     */
    public double getHeadVisualTextRectangleX() {
        VisualTextRectangle head = null;
        for (Node child: getChildren()) {
            if (child instanceof VisualTextRectangle) {
                if (head != null)
                    return .0;
                head = (VisualTextRectangle) child;
            }
        }
        if (head == null)
            return .0;
        else
            return head.getX();
    }

}
