package org.cpvisu;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.cpvisu.problems.SearchTree;
import org.cpvisu.problems.SearchTreeNode;
import org.cpvisu.shapes.GroupArea;
import org.cpvisu.shapes.VisualTextRectangle;

import java.util.ArrayList;

public class VisualSearchTree {

    SearchTree searchTree;
    private int[] levelHeight;      // y offset for each depth in the search tree
    private int[] maxLinesPerLevel; // max number of string lines for each depth
    private int fontHeight;         // height of the font for one line
    private ArrayList<Line> branches;
    private ArrayList<VisualTextRectangle> visualNodes;
    private Group group;
    private int offset = 42;

    public VisualSearchTree(SearchTree searchTree) {
        this.searchTree = searchTree;
        fontHeight = (int) Math.ceil(new Text("").getBoundsInLocal().getHeight());
        reset();
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
        // draw the rectangles
        group.getChildren().add(design(root, 0));

        group.setTranslateX(200);
        return group;
    }

    public void reset() {
        branches = new ArrayList<>();
        visualNodes = new ArrayList<>();
        group = new Group();
    }

    /**
     * gives the maximum string width of a node
     * @param node node whose max string width needs to be computed
     * @return maximum string width, ceil rounded to the nearest int
     */
    public int getMaxStringWidth(SearchTreeNode<String> node) {
        return (int) Math.ceil(new Text(node.getLabel()).getBoundsInLocal().getWidth());
    }

    private GroupArea design(SearchTreeNode<String> node, int level) {
        Shape placed = new Rectangle(); // area occupied by the children
        GroupArea currentNode = new GroupArea();
        int nBranches = 0;
        for (SearchTreeNode<String> child : node.getSons()) {
            GroupArea visualChild = design(child, level+1); // design the child
            while (intersect(placed, visualChild.getArea()))  // as long as the child intersects the current area
                visualChild.moveByX(offset); // move the child by a given value
            placed = Shape.union(placed, visualChild.getArea());
            currentNode.getChildren().add(visualChild); // the group of the current node includes the children
            nBranches++;
        }
        VisualTextRectangle currentDesign = new VisualTextRectangle(node.getLabel(), 50, 20);
        double x = currentNode.getBoundsInLocal().getWidth() / 2; //place the parent in the middle of its children
        if (!node.getSons().isEmpty()) {
            x = x - currentDesign.getWidth() / 2; // intermediate nodes are centered, not aligned with top left corner
        }
        currentDesign.moveTo(x, levelHeight[level]);

        if (node.getLabel().equals("4"))
            System.out.println("ok");

        // draw the lines between the parent node and its children
        Shape[] branches = new Shape[nBranches];
        int i = 0;
        for (Node child : currentNode.getChildren()) {
            if (child instanceof GroupArea) {
                for (Node childVisual : ((GroupArea) child).getChildren()) {
                    if (childVisual instanceof VisualTextRectangle) {
                        double startX = currentDesign.getCenterX();
                        double startY = currentDesign.getY() + currentDesign.getHeight();
                        double endX = ((VisualTextRectangle) childVisual).getCenterX() + child.getTranslateX();
                        double endY = ((VisualTextRectangle) childVisual).getY();
                        Polyline branch = new Polyline();
                        branch.getPoints().addAll(startX, startY,
                                startX, (endY - startY) / 2 + startY,
                                endX, (endY - startY) / 2 + startY,
                                endX, endY) ;
                        branches[i++] = branch;
                    }
                }
            }
        }
        for (Shape branch : branches)
            currentNode.getChildren().add(branch);
        // add event listener for the child
        currentDesign.setOnMousePressed((MouseEvent e) -> {
            node.runAction();
        });

        currentNode.add(currentDesign); // the layout of the current node includes the node itself
        return currentNode;
    }

    private boolean intersect(Shape a, Shape b) {
        return a.getBoundsInParent().intersects(b.getBoundsInParent());
    }

}
