package org.cpvisu.problems;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * represent a node within a search tree
 */
public class SearchTreeNode<T> {

    private T label;
    private List<SearchTreeNode<T>> sons;
    private List<T> edgeLabels;
    private Color color;
    private Runnable action;

    public SearchTreeNode(T label, SearchTreeNode<T>[] sons, T[] edgeLabels, Color color, Runnable action) {
        this(label,
            sons == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(sons)),
            edgeLabels == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(edgeLabels)),
            color,
            action);
    }

    public SearchTreeNode(T label, List<SearchTreeNode<T>> sons, List<T> edgeLabels, Color color, Runnable action) {
        this.label = label;
        this.sons = sons;
        this.edgeLabels = edgeLabels;
        this.color = color;
        this.action = action;
    }

    public SearchTreeNode(T label) {
        this(label, new ArrayList<>(), new ArrayList<>(), Color.WHITE, () -> {;});
    }

    public SearchTreeNode(T label, Color color, Runnable action) {
        this(label, new ArrayList<>(), new ArrayList<>(), color, action);
    }

    public SearchTreeNode(T label, Color color) {
        this(label, new ArrayList<>(), new ArrayList<>(), color, () -> {;});
    }

    public SearchTreeNode(T label, Runnable action) {
        this(label, new ArrayList<>(), new ArrayList<>(), Color.WHITE, action);
    }

    @Override
    public String toString() {
        if (sons.isEmpty())
            return label.toString();
        return String.format("%s(%s)", label.toString(), String.join(",", sons.stream().map(SearchTreeNode::toString).toList()));
    }

    public int getMaxDepth() {
        return getMaxDepth(0);
    }

    private int getMaxDepth(int acc) {
        if (sons.isEmpty())
            return acc;
        return sons.stream().map(n -> n.getMaxDepth(acc+1)).max(Integer::compareTo).get();
    }

    public T getLabel() {
        return label;
    }

    public List<SearchTreeNode<T>> getSons() {
        return sons;
    }

    public List<T> getEdgeLabels() {
        return edgeLabels;
    }

    public Color getColor() {
        return color;
    }

    public Runnable getAction() {
        return action;
    }

}
