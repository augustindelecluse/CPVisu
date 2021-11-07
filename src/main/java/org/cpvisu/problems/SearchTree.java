package org.cpvisu;

import java.util.ArrayList;

/**
 * used to represent a search tree
 * each node in the tree is identified by an integer
 */
public class VisualSearchTree {

    private ArrayList<> branches;

    public VisualSearchTree() {
        
    }

    /**
     * add a branch to the search tree
     * @param parent node from which the branch will be created
     * @param node node that needs to be added
     * @param nodeMessage message associated with the node
     * @param branchMessage message associated with the branch binding the parent to the node
     */
    public void createBranch(int parent, int node, String nodeMessage, String branchMessage) {

    }

    /**
     * reset the whole search tree
     */
    public void clear() {

    }

    /**
     * gives the tikz code to represent the search tree
     * @return
     */
    public String toTikz() {
        return null;
    }

    /**
     * update the representation of the tree on the screen
     */
    public void updateVisual() {

    }

}
