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
import static org.cpvisu.util.colors.ColorFactory.*;

public class VisualBinPacking {

    private int nBin;
    private int width;
    private int maxHeight;
    private ArrayList<Rectangle>[] bins;
    private int occupiedBins;
    private Group area; // region where the rectangles are drawn
    private Rectangle background;

    public VisualBinPacking(int nBin, int width, int maxHeight) {
        this.nBin = nBin;
        this.width = width;
        this.maxHeight = maxHeight;
        bins = new ArrayList[nBin];
        for (int i =0; i < nBin; ++i)
            bins[i] = new ArrayList<>();
        Rectangle background = new Rectangle(0, 0, nBin * width, maxHeight);
        background.setFill(Color.LIGHTGRAY);
        this.area = new Group(background);
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
        int binToHeight = binHeight(to);
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
        ObservableList<Node> list = area.getChildren();
        for (int i = 0; i < nBin; ++i) {
            int height = ThreadLocalRandom.current().nextInt((int) (0.1 * maxHeight), (int) (0.3 * maxHeight));
            Rectangle rectangle = new Rectangle(i * width, maxHeight-height, width, height);
            if (canInsertRectangle(i, rectangle)) {
                rectangle.setFill(randomColor());
                rectangle.setStroke(Color.BLACK);
                list.add(rectangle);
                bins[i].add(rectangle);
            }
        }
        occupiedBins = nBin;
        return area;
    }

    /**
     * put the given rectangle on top of a bin
     * @param bin bin where the rectangle needs to be put
     * @param width width of the rectangle that will be put in the bin
     * @param height height of the rectangle that will be put in the bin
     * @return true if the rectangle has been added to the bin, false otherwise
     */
    public boolean addRectangleToBin(int bin, int width, int height) {
        if (bin < 0 || bin >= nBin)
            return false;
        int currentHeight = binHeight(bin);
        if (currentHeight + height > maxHeight)
            return false;
        Rectangle rectangle = new Rectangle(bin * width, maxHeight - currentHeight - height, width, height);
        rectangle.setFill(randomColor());
        rectangle.setStroke(Color.BLACK);
        bins[bin].add(rectangle);
        area.getChildren().add(rectangle);
        return true;
        //TODO trigger dimensions changes for this rectangle
        // maybe using translateX, scaleX of background?
    }

    /**
     * put the given rectangle on top of a bin
     * @param bin bin where the rectangle needs to be put
     * @param rectangle rectangle that will be put in the bin. Its (x,y) coordinates will be changed in order to appear within the bin
     * @return true if the rectangle has been added to the bin, false otherwise
     */
    public boolean addRectangleToBin(int bin, Rectangle rectangle) {
        if (bin < 0 || bin >= nBin)
            return false;
        int currentHeight = binHeight(bin);
        if (currentHeight + rectangle.getHeight() > maxHeight)
            return false;
        rectangle.setX(bin * width);
        rectangle.setY(maxHeight - currentHeight - rectangle.getHeight());
        rectangle.setTranslateX(0);
        rectangle.setTranslateY(0);
        rectangle.setStroke(Color.BLACK);
        bins[bin].add(rectangle);
        area.getChildren().add(rectangle);
        return true;
    }


    /**
     * gives the number of occupied bins
     * @return number of occupied bins
     */
    public int occupiedBins() {
        return occupiedBins;
    }

    /**
     * gives the number of rectangles within each bin
     * @return number of rectangles within each bin
     */
    public int[] binsSize() {
        return IntStream.range(0, nBin).map(i -> bins[i].size()).toArray();
    }

    /**
     * return the number of rectangles within a bin
     * @param bin
     * @return
     */
    public int binSize(int bin) {
        return bins[bin].size();
    }

    /**
     * return the height of a bin
     * @param bin id of the bin
     * @return number of rectangles within the bin
     */
    public int binHeight(int bin) {
        int height = 0;
        for (Rectangle rectangle: bins[bin])
            height += rectangle.getHeight();
        return height;
    }

    /**
     * return the occupancy of a bin: the percentage of filling of the bin, as a number between 0 and 1
     * @param bin id of the bin
     * @return occupancy of the bin, in [0, 1]
     */
    public double binOccupancy(int bin) {
        return ((double) (binHeight(bin))) / maxHeight;
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

    public boolean canInsertRectangle(int bin, Rectangle rectangle) {
        return (bin >= 0 && bin < nBin && rectangle.getHeight() + binHeight(bin) < maxHeight);
    }

}
