package org.cpvisu;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
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
        // design the shapes for the search tree
        // drawTree(root, 0);
        //design(root, 0);
        //ObservableList<Node> children = group.getChildren();
        //for (VisualTextRectangle visualTextRectangle : visualNodes) {
        //    children.add(visualTextRectangle.getNode());
        //}
        group = design(root, 0);
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

    /**
     * draw the search tree, with its nodes and branches
     * the search tree is added to the group
     * @param node node from which the tree should be drawn
     */
    private VisualTextRectangle drawTree(SearchTreeNode<String> node, int level) {
        int spacing = 10;
        VisualTextRectangle visualNode = new VisualTextRectangle(node.getLabel());
        group.getChildren().add(visualNode.getNode());
        for (SearchTreeNode<String> son: node.getSons()) {
            // draw the branch between the parent and the son
            /*
            Line line = new Line();
            line.setStartX(visualNode.getCenterX());
            line.setStartY(visualNode.getY() + visualNode.getHeight());
            line.setEndX(100.0f);
            line.setEndY(100.0f);
            branches.add(line)
             */
            // draw the son
            VisualTextRectangle visualChild = drawTree(son, level+1);
            visualChild.moveTo(0, 0);
        }
        visualNode.moveTo(0, levelHeight[level]);
        return visualNode;
    }

    /*
    private VisualTextRectangle design(SearchTreeNode<String> node, int level) {
        Shape placed = new Rectangle(); // area occupied by the children
        boolean placedUnchanged = true;
        for (SearchTreeNode<String> child : node.getSons()) {
            VisualTextRectangle visualChild = design(child, level+1); // design the child
            while (intersect(placed, visualChild.getArea()))  // as long as the child intersects the current area
                visualChild.moveByX(42); // move the child by a given value
            placed = placedUnchanged? visualChild.getArea() : Shape.union(placed, visualChild.getArea());
            placedUnchanged = false;
        }
        double x = placed.getBoundsInLocal().getWidth() / 2;
        VisualTextRectangle currentDesign = new VisualTextRectangle(node.getLabel());
        if (node.getLabel().equals("0")) {
            System.out.println("node 0");
        }
        if (x != 0) {
            currentDesign.moveTo(x, levelHeight[level]);
        } else {
            currentDesign.moveTo(x, levelHeight[level]);
        }
        visualNodes.add(currentDesign);
        if (!placedUnchanged)
            currentDesign.setArea(Shape.union(placed, currentDesign.getArea()));
        return currentDesign;
    }

     */

    /*
    private VisualTextRectangle design(SearchTreeNode<String> node, int level) {
        Shape placed = new Rectangle(); // area occupied by the children
        for (SearchTreeNode<String> child : node.getSons()) {
            VisualTextRectangle visualChild = design(child, level+1); // design the child
            while (intersect(placed, visualChild.getArea()))  // as long as the child intersects the current area
                visualChild.moveByX(42); // move the child by a given value
            placed = Shape.union(placed, visualChild.getArea());
        }
        double x = placed.getBoundsInLocal().getWidth() / 2; //place the parent in the middle of its children
        VisualTextRectangle currentDesign = new VisualTextRectangle(node.getLabel());
        currentDesign.moveTo(x, levelHeight[level]);
        //visualNodes.add(currentDesign);
        currentDesign.setArea(Shape.union(placed, currentDesign.getArea()));
        return currentDesign;
    }

     */

    private GroupArea design(SearchTreeNode<String> node, int level) {
        double offset = 42;
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
        VisualTextRectangle currentDesign = new VisualTextRectangle(node.getLabel());
        double x = currentNode.getBoundsInLocal().getWidth() / 2; //place the parent in the middle of its children
        currentDesign.moveTo(x, levelHeight[level]);

        if (node.getLabel().equals("0"))
            System.out.println("ok");

        // draw the lines between the parent node and its children
        Line[] branches = new Line[nBranches];
        int i = 0;
        for (Node child : currentNode.getChildren()) {
            if (child instanceof Group) {
                System.out.println("ok");
                Line line = new Line();
                line.setStartX(currentDesign.getCenterX());
                line.setStartY(currentDesign.getY() + currentDesign.getHeight());
                line.setEndX(child.getTranslateX());
                line.setEndY(child.getTranslateY());
                branches[i++] = line;
            }
        }

        currentNode.add(currentDesign); // the layout of the current node includes the node itself
        return currentNode;
    }


    private boolean intersect(Shape a, Shape b) {
        Bounds bB = b.getBoundsInLocal();
        return a.getBoundsInParent().intersects(b.getBoundsInParent());
    }

}
