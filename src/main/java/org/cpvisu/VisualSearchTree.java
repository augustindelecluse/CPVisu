package org.cpvisu;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.cpvisu.problems.SearchTree;
import org.cpvisu.problems.SearchTreeNode;
import org.cpvisu.shapes.GroupArea;
import org.cpvisu.shapes.LabeledPath;
import org.cpvisu.shapes.VisualTextRectangle;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.cpvisu.shapes.ShapeOperation.ShapeCopy;

public class VisualSearchTree {

    SearchTree searchTree;
    private int[] levelHeight;      // y offset for each depth in the search tree
    private int[] maxLinesPerLevel; // max number of string lines for each depth
    private int fontHeight;         // height of the font for one line
    private ArrayList<Line> branches;
    private ArrayList<VisualTextRectangle> visualNodes;
    private Group pane;
    private int offset = 42;       // offset used when moving nodes
    private int xLabelSpacing = 5;  // space between a node and its label branch, in x values
    private int yLabelSpacing = -5; // space between a node and its label branch, in y values

    public VisualSearchTree(SearchTree searchTree) {
        this.searchTree = searchTree;
        fontHeight = (int) Math.ceil(new Text("").getBoundsInLocal().getHeight());
        pane = new Group();
        reset();
        //printCursorPosition();
    }

    /**
     * construct the visual representation of the current search tree
     */
    public Group update() {
        reset();
        SearchTreeNode<String> root = searchTree.toNode(0); // assume that the node 0 is the root node
        int depth = root.getMaxDepth();
        // compute the height levels and lines per level for all nodes
        levelHeight = new int[depth+1];
        maxLinesPerLevel = new int[depth+1];
        levelHeight[0] = 0;
        for (int i = 1; i <= depth; ++i)
            levelHeight[i] = levelHeight[i-1] + (3 + maxLinesPerLevel[i-1]) * fontHeight;
        // draw the tree and its branches
        pane.getChildren().add(design(root, 0));
        return pane;
    }

    public void reset() {
        branches = new ArrayList<>();
        visualNodes = new ArrayList<>();
        pane.getChildren().clear();
    }

    /**
     * gives the maximum string width of a node
     * @param node node whose max string width needs to be computed
     * @return maximum string width, ceil rounded to the nearest int
     */
    public int getMaxStringWidth(SearchTreeNode<String> node) {
        return (int) Math.ceil(new Text(node.getLabel()).getBoundsInLocal().getWidth());
    }

    /**
     * design the search tree using the information of a node
     * recursively design the children of a node and center the parent between its children
     * @param node node from which the tree should be designed
     * @param level depth of the node. Used to compute the y values; needs levelHeight to be set to the desired offset
     * @return design of the search tree:
     *      hierarchy composed of a node (VisualTextRectangle), the branches and its children (same type of hierarchy)
     */
    private GroupArea design(SearchTreeNode<String> node, int level) {
        Shape placed = new Rectangle(); // area occupied by all children


        GroupArea currentNode = new GroupArea();
        int nBranches = 0;
        for (SearchTreeNode<String> child : node.getSons()) {

            GroupArea visualChild = design(child, level+1); // design the child
            Shape childArea = visualChild.getArea(); // area of the current child
            // TODO compute that the label of the branch can be added without problem
            //String branchLabel = node.getEdgeLabels().get(nBranches);
            //if (branchLabel != null && !branchLabel.equals("")) {
            //    Text text = new Text(childArea.getTranslateX() + xLabelSpacing, childArea.getTranslateY() + yLabelSpacing, branchLabel);
            //}
            int valOffset = 0;
            while (intersectArea(placed, childArea)) { // as long as the child intersects the current area
                childArea.setTranslateX(childArea.getTranslateX() + offset); // move the child to prevent the intersection
                valOffset += offset;
            }
            if (valOffset != 0) {
                visualChild.moveByX(valOffset); // effectively move the child
            }

            placed = Shape.union(placed, childArea); // merge the area of the child with the current occupied area

            currentNode.getChildren().add(visualChild); // the group of the current node includes the children
            nBranches++;
        }
        VisualTextRectangle currentDesign = new VisualTextRectangle(node.getLabel(), 50, 20);
        currentDesign.setFill(node.getColor());

        double xMax = Double.MIN_VALUE;
        double xMin = Double.MAX_VALUE;
        for (Node child : currentNode.getChildren()) { // try to place the parent in the middle of its children
            if (child instanceof GroupArea) {
                for (Node childVisual : ((GroupArea) child).getChildren()) {
                    if (childVisual instanceof VisualTextRectangle) { // consider only this type of nodes as children
                        xMax = Math.max(xMax, child.getTranslateX() + ((VisualTextRectangle) childVisual).getX() + ((VisualTextRectangle) childVisual).getWidth());
                        xMin = Math.min(xMin, child.getTranslateX() + ((VisualTextRectangle) childVisual).getX());
                    }
                }
            }
        }
        double x;
        if (xMax !=  Double.MIN_VALUE) //place the parent in the middle of its children
            x = (xMax - xMin) / 2 + xMin - currentDesign.getWidth() / 2; // intermediate nodes are centered, not aligned with top left corner
        else
            x = 0;
        currentDesign.moveTo(x, levelHeight[level]);

        // draw the lines between the parent node and its children
        Node[] branches = new Node[nBranches * 2];
        nBranches = 0;
        int i = 0;
        for (Node child : currentNode.getChildren()) {
            if (child instanceof GroupArea) {
                for (Node childVisual : ((GroupArea) child).getChildren()) {
                    if (childVisual instanceof VisualTextRectangle) {
                        double startX = currentDesign.getCenterX();
                        double startY = currentDesign.getY() + currentDesign.getHeight();
                        double endX = ((VisualTextRectangle) childVisual).getCenterX() + child.getTranslateX();
                        double endY = ((VisualTextRectangle) childVisual).getY();
                        double intermediateY = (endY - startY) * (1./4) + startY;
                        LabeledPath branch = new LabeledPath(5, -5, node.getEdgeLabels().get(nBranches++),
                                startX, startY,
                                startX, intermediateY,
                                endX, intermediateY,
                                endX, endY
                        );
                        branches[i++] = branch;
                    }
                }
            }
        }
        for (int j = 0; j < i ; ++j) {
            currentNode.getChildren().add(branches[j]);
        }
        // add event listener for the current node
        currentDesign.setOnMousePressed((MouseEvent e) -> {
            node.runAction();
        });
        currentNode.add(currentDesign); // the layout of the current node includes the node itself
        return currentNode;
    }

    /**
     * tell if 2 shapes intersects, using the bounds of the parent as criterion
     * 2 shapes can consider as being intersecting even if they do not collide, as the bounds are used instead of the (maybe sparse) area
     * this function is faster but less exact than intersectArea
     * @param a first shape
     * @param b second shape
     * @return true if the shapes intersect
     */
    private boolean intersectBounds(Shape a, Shape b) {
        return a.getBoundsInParent().intersects(b.getBoundsInParent());
    }

    /**
     * tell if 2 shapes intersects, using the exact area of the shapes as criterion
     * this function is more exact but slower than intersectBounds
     * @param a first shape
     * @param b second shape
     * @return true if the shapes intersect
     */
    private boolean intersectArea(Shape a, Shape b) {
        Shape intersection = Shape.intersect(a, b);
        double width = intersection.getBoundsInLocal().getWidth();
        if (intersection instanceof Path) {
            if (((Path) intersection).getElements().size() == 0)
                return false;
        }
        return true;
    }

    /**
     * print the position of the mouse
     */
    private void printCursorPosition() {
        AtomicBoolean scenePosted = new AtomicBoolean(false);
        pane.setOnMousePressed(e -> {
            System.out.printf("mouse at (%.3f, %.3f)\n", e.getSceneX(), e.getSceneY());
            if (!scenePosted.get()) {
                scenePosted.set(true);
                pane.getScene().setOnMousePressed(mouseEvent -> {
                    System.out.printf("mouse at (%.3f, %.3f)\n", mouseEvent.getSceneX(), mouseEvent.getSceneY());
                });
            }
        });
    }

    /**
     * export the current tree as a tikz figure
     * @return String corresponding to the tikz figure
     */
    public String toTikz() {
        return null; // TODO tikz export
    }

}
