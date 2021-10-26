package org.cpvisu;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.cpvisu.AnimationFactory.*;

public class VisualBinPacking {

    private int nBin;
    private int width;
    private int maxHeight;
    private ArrayList<Rectangle>[] bins;
    private int occupiedBins;

    public VisualBinPacking(int nBin, int width, int maxHeight) {
        this.nBin = nBin;
        this.width = width;
        this.maxHeight = maxHeight;
        bins = new ArrayList[nBin];
        for (int i =0; i < nBin; ++i)
            bins[i] = new ArrayList<>();
    }

    /**
     * move the rectangle on top of a bin to the top of another
     * no action performed if the rectangle cannot be inserted
     * @param from bin from where the rectangle must be moved
     * @param to bin where to move the rectangle
     * @return true if the rectangle has changed from position
     */
    public boolean moveRectangle(int from, int to) {
        return moveRectangle(bins[from].size() - 1, from, to);
    }

    /**
     * move a rectangle within a bin to the top of another
     * no action performed if the rectangle cannot be inserted
     * @param rectangle id of the rectangle to move. is relative to the number of the rectangle in
     * @param from bin from where the rectangle must be moved
     * @param to bin where to move the rectangle
     * @param animationDuration duration for the animation of moving the rectangles
     * @return true if the rectangle has been moved, false otherwise
     */
    public boolean moveRectangle(int animationDuration, int rectangle, int from, int to) {
        if (from < 0 || from >= nBin || to < 0 || to >= nBin || from == to || rectangle >= bins[from].size() || rectangle < 0)
            return false;
        Rectangle rect = bins[from].get(rectangle);
        int binToHeight = 0;
        for (Rectangle r: bins[to])
            binToHeight += r.getHeight();
        int height = (int) rect.getHeight();

        if (height + binToHeight <= maxHeight) { // the rectangle can be inserted
            // move the rectangle to the new bin
            bins[to].add(rect);
            rect.toFront(); // the rectangle will be the last element drawn
            int finalCoordinate = maxHeight-binToHeight-height;
            moveTo(rect, Duration.millis(animationDuration), to * width, finalCoordinate);
            // reorder the original bin
            int newFromSize = bins[from].size() - 1;
            if (newFromSize == 0)
                --occupiedBins;
            bins[from].remove(rectangle); // remove the rectangle from its original bin
            for (int i = rectangle ; i < newFromSize ; ++i) { // all rectangles on top of the current rectangle are put down
                Rectangle onTop = bins[from].get(i);
                moveBy(onTop, Duration.millis(animationDuration), 0, height); // move each rectangle by the height of the other rectangle
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean moveRectangle(int rectangle, int from, int to) {
        return moveRectangle(1, rectangle, from, to);
    }

    /**
     * put one rectangle with random height and random color in every bin
     * @return group containing all rectangles that were created
     */
    public Group initRectangles() {
        Group group = new Group();
        ObservableList<Node> list = group.getChildren();
        for (int i = 0; i < nBin; ++i) {
            int height = ThreadLocalRandom.current().nextInt((int) (0.1 * maxHeight), (int) (0.3 * maxHeight));
            Rectangle rectangle = new Rectangle(i * width, maxHeight-height, width, height);
            rectangle.setFill(randomColor());
            rectangle.setStroke(Color.BLACK);
            list.add(rectangle);
            bins[i].add(rectangle);
        }
        occupiedBins = nBin;
        return group;
    }

    public int occupiedBins() {
        return occupiedBins;
    }

    public int[] binsSize() {
        return IntStream.range(0, nBin).map(i -> bins[i].size()).toArray();
    }

    public int binSize(int bin) {
        return bins[bin].size();
    }

    public void printCoordinates() {
        for (int i = 0; i < nBin; ++i) {
            StringBuilder stringBuilder = new StringBuilder("bin " + i + ": ");
            for (Rectangle rectangle: bins[i]) {
                double x = rectangle.getX() + rectangle.getTranslateX();
                stringBuilder.append("(").append(x).append("; ").append(rectangle.getY()).append("), ");
            }
            System.out.println(stringBuilder.toString());
        }
    }

}
