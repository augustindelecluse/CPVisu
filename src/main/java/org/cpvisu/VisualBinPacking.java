package org.cpvisu;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static org.cpvisu.AnimationFactory.moveBy;
import static org.cpvisu.AnimationFactory.moveTo;

public class VisualBinPacking {

    private int nBin;
    private int width;
    private int maxHeight;
    private Stack<Rectangle>[] bins;

    public VisualBinPacking(int nBin, int width, int maxHeight) {
        this.nBin = nBin;
        this.width = width;
        this.maxHeight = maxHeight;
        bins = new Stack[nBin];
        for (int i =0; i < nBin; ++i)
            bins[i] = new Stack<>();
    }

    /**
     * move rectangle from top of a bin to top of another
     * no action performed if the rectangle cannot be inserted
     * @param from bin from where the rectangle must be moved
     * @param to bin where to move the rectangle
     * @return true if the rectangle has changed from position
     */
    public boolean moveRectangle(int from, int to) {
        if (from < 0 || from >= nBin || to < 0 || to >= nBin || from == to ||bins[from].size() == 0)
            return false;
        Rectangle rectangle = bins[from].pop();
        int binToHeight = 0;
        for (Rectangle r: bins[to])
            binToHeight += r.getHeight();
        int height = (int) rectangle.getHeight();

        if (height + binToHeight <= maxHeight) {
            bins[to].add(rectangle);
            rectangle.toFront(); // the rectangle will be the last element drawn
            int finalCoordinate = maxHeight-binToHeight-height;
            // double xDest = to * width - rectangle.getX();
            // double yDest = finalCoordinate- rectangle.getY();
            // moveBy(rectangle, Duration.millis(500), xDest, yDest);
            moveTo(rectangle, Duration.millis(1000), to * width, finalCoordinate);
            return true;
        } else {
            bins[from].add(rectangle);
            return false;
        }
    }

    /**
     * put one rectangle with random height and random color in every bin
     * @return group containing all rectangles that were created
     */
    public Group initRectangles() {
        Group group = new Group();
        ObservableList<Node> list = group.getChildren();
        for (int i = 0; i < nBin; ++i) {
            int height = ThreadLocalRandom.current().nextInt((int) (0.1 * maxHeight), (int) (0.6 * maxHeight));
            Rectangle rectangle = new Rectangle(i * width, maxHeight-height, width, height);
            int red = ThreadLocalRandom.current().nextInt(255);
            int green = ThreadLocalRandom.current().nextInt(255);
            int blue = ThreadLocalRandom.current().nextInt(255);
            Color color = Color.rgb(red, green, blue);
            rectangle.setFill(color);
            rectangle.setStroke(color.BLACK);
            list.add(rectangle);
            bins[i].add(rectangle);
        }
        return group;
    }

}
