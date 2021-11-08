package org.cpvisu.problems;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * search tree encoding for visualization
 * each node in the tree is identified by an integer
 */
public class SearchTree {

    // possible status for a node
    public final int INTERMEDIATE = 0;  // nodes are intermediate by default
    public final int SUCCESS = 1;
    public final int FAILURE = 2;
    // colors for the nodes depending on the status
    private final Map<Integer, Color> colorMapping = Map.of(
            INTERMEDIATE, Color.LIGHTBLUE,
            SUCCESS, Color.LIGHTGREEN,
            FAILURE, Color.DARKSALMON
    );
    // branches in the tree
    public record Branch(int parent, int node, String nodeMessage, String branchMessage, Runnable action) {};
    private ArrayList<Branch> branches;
    // status of each node
    private HashMap<Integer, Integer> nodes_status;

    public SearchTree() {
        nodes_status = new HashMap<>();
        branches = new ArrayList<>();
    }

    /**
     * add a branch to the search tree
     * assume that the child node does not belong to the tree yet
     * @param parent node from which the branch will be created
     * @param node node that needs to be added (and was absent before)
     * @param nodeMessage message associated with the node
     * @param branchMessage message associated with the branch binding the parent to the node
     */
    public void addBranch(int parent, int node, String nodeMessage, String branchMessage) {
        this.addBranch(parent, node, nodeMessage, branchMessage, () -> {});
    }

    /**
     * add a branch to the search tree
     * assume that the child node does not belong to the tree yet
     * @param parent node from which the branch will be created
     * @param node node that needs to be added (and was absent before)
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
     * assume that the child node does not belong to the tree yet
     * @param parent node from which the branch will be created
     * @param node node that needs to be added (and was absent before)
     */
    public void addBranch(int parent, int node) {
        addBranch(parent, node, null, null, () -> {});
    }

    /**
     * add a branch to the search tree
     * assume that the child node does not belong to the tree yet
     * @param parent node from which the branch will be created
     * @param node node that needs to be added (and was absent before)
     * @param action action to perform on the node
     */
    public void addBranch(int parent, int node, Runnable action) {
        addBranch(parent, node, null, null, action);
    }

    /**
     * reset the whole search tree
     */
    public void clear() {
        nodes_status = new HashMap<>();
    }

    /**
     * notify a node as a success node, changing its status (and associated color)
     * @param node success node
     * @return true if the node belongs to the tree
     */
    public boolean addSuccess(int node) {
        if (nodes_status.containsKey(node)) {
            nodes_status.put(node, SUCCESS);
            return true;
        }
        return false;
    }

    /**
     * notify a node as a failure node, changing its status (and associated color)
     * @param node failure node
     * @return true if the node belongs to the tree
     */
    public boolean addFailure(int node) {
        if (nodes_status.containsKey(node)) {
            nodes_status.put(node, FAILURE);
            return true;
        }
        return false;
    }

    /**
     * gives the status associated to a node
     * @param node node whose status needs to be known
     * @return integer code for the status
     */
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
            return new SearchTreeNode<>(Integer.toString(node), getColor(node), getAction(node));
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

    public void runAction(int node) {
        branches.stream().filter(b -> b.node == node).findFirst().ifPresent(branch -> branch.action.run());
    }

    public Color getColor(int node) {
        return colorMapping.get(getNodeStatus(node));
    }

    /**
     * gives an iterable of all current nodes
     * @return iterable of all current nodes
     */
    public Iterable<Integer> getNodes() {
        HashSet<Integer> seen = new HashSet<>();
        branches.forEach(b -> {seen.add(b.node); seen.add(b.parent);});
        return seen;
    }

    public boolean isEmpty() {
        return branches.isEmpty();
    }

    /**
     * gives the number of nodes appearing in the tree
     * @return number of nodes appearing in the tree
     */
    public int nNodes() {
        HashSet<Integer> seen = new HashSet<>();
        branches.forEach(b -> {seen.add(b.node); seen.add(b.parent);});
        return seen.size();
    }

    /**
     * set the action for an existing node
     * @param node node whose action needs to be set
     * @param action action for the node
     * @return true if the action has been set for the node
     */
    public boolean setAction(int node, Runnable action) {
        int size = branches.size();
        int i = 0;
        Branch found = null;
        for (Branch branch : branches) {
            if (branch.node == node) {
                found = branch;
                break;
            }
            ++i;
        }
        if (found != null) {
            branches.remove(i);
            branches.add(new Branch(found.parent, found.node, found.nodeMessage, found.branchMessage, action));
            return true;
        }
        return false;
    }


}
