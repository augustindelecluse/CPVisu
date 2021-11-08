package org.cpvisu.shapes;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ShapeOperation {

    public static Shape ShapeCopy(Shape s) {
        return Shape.union(new Rectangle(), s);
    }

}
