package org.cpvisu.problems;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * search tree encoding for visualization
 * each node in the tree is identified by an integer
 */
public class SearchTree {

    // status of each node
    private HashMap<Integer, Integer> nodes_status;
    public int INTERMEDIATE = 0;  // nodes are intermediate by default
    public int SUCCESS = 1;
    public int FAILURE = 2;
    // colors for the nodes
    private final Map<Integer, Color> colorMapping = Map.of(
            INTERMEDIATE, Color.BLUE,
            SUCCESS, Color.GREEN,
            FAILURE, Color.RED
    );
    // branches in the tree
    public record Branch(int parent, int node, String nodeMessage, String branchMessage, Runnable action) {};
    private ArrayList<Branch> branches;

    public SearchTree() {
        nodes_status = new HashMap<>();
        branches = new ArrayList<>();
    }

    /**
     * add a branch to the search tree
     * @param parent node from which the branch will be created
     * @param node node that needs to be added
     * @param nodeMessage message associated with the node
     * @param branchMessage message associated with the branch binding the parent to the node
     */
    public void addBranch(int parent, int node, String nodeMessage, String branchMessage) {
        this.addBranch(parent, node, nodeMessage, branchMessage, () -> {});
    }

    /**
     * add a branch to the search tree
     * @param parent node from which the branch will be created
     * @param node node that needs to be added
     * @param nodeMessage message associated with the node
     * @param branchMessage message associated with the branch binding the parent to the node
     * @param action action to perform on the given node
     */
    public void addBranch(int parent, int node, String nodeMessage, String branchMessage, Runnable action) {
        nodes_status.put(node, INTERMEDIATE);
        if (!nodes_status.containsKey(parent))
            nodes_status.put(parent, INTERMEDIATE);
        branches.add(new Branch(parent, node, nodeMessage, branchMessage, action));
    }

    /**
     * add a branch to the search tree
     * @param parent node from which the branch will be created
     * @param node node that needs to be added
     */
    public void addBranch(int parent, int node) {
        addBranch(parent, node, null, null, () -> {});
    }

    /**
     * reset the whole search tree
     */
    public void clear() {
        nodes_status = new HashMap<>();
    }

    public boolean addSuccess(int node) {
        if (nodes_status.containsKey(node)) {
            nodes_status.put(node, SUCCESS);
            return true;
        }
        return false;
    }

    public boolean addFailure(int node) {
        if (nodes_status.containsKey(node)) {
            nodes_status.put(node, FAILURE);
            return true;
        }
        return false;
    }

    public int getNodeStatus(int node) {
        return nodes_status.get(node);
    }

    /**
     * gives the node associated to an id, as a SearchTreeNode<String>
     * @param node id of the node that needs to be transformed to a SearchTreeNode
     * @return SearchTreeNode of the node, with all its branches and children labeled
     */
    public SearchTreeNode<String> toNode(int node) {
        List<Branch> childs = children(node);
        if (childs.isEmpty())
            return new SearchTreeNode<>(Integer.toString(node), getColor(node));
        return new SearchTreeNode<>(Integer.toString(node), childs.stream().map(c -> toNode(c.node)).toList(),
                childs.stream().map(c -> c.branchMessage).toList(), getColor(node), getAction(node));
    }

    /**
     * gives all branches having node as direct parent
     * @param node node whose children need to be retrieved
     * @return branches having node as parent
     */
    public List<Branch> children(int node) {
        return branches.stream().filter(b -> b.parent == node).toList();
    }

    /**
     * gives the action associated to a given node
     * @param node node whose action needs to be retrieved
     * @return action associated to the node
     */
    public Runnable getAction(int node) {
        Branch branch = branches.stream().filter(b -> b.node == node).findFirst().orElse(null);
        return branch != null ? branch.action : () -> {};
    }

    public Color getColor(int node) {
        return colorMapping.get(getNodeStatus(node));
    }

}
