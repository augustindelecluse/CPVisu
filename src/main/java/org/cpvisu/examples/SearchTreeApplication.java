package org.cpvisu.examples;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualSearchTree;
import org.cpvisu.problems.SearchTree;

import static org.cpvisu.AnimationFactory.*;

public class SearchTreeApplication extends VisualApplication {

    int count = 2;
    SearchTree searchTree;
    Group visual;
    VisualSearchTree visualSearchTree;

    @Override
    public Scene application(Stage stage) {
        searchTree = new SearchTree();

        searchTree.addBranch(0, 1, null, "node 0 to 1", () -> System.out.println("I'm node 1"));
        searchTree.addBranch(1, 2, null, "node 1 to 2", () -> System.out.println("I'm node 2"));
        searchTree.addBranch(1, 3, null, "node 1 to 3", () -> System.out.println("I'm node 3"));
        searchTree.addBranch(0, 4, null, "node 0 to 4", () -> System.out.println("I'm node 4"));
        searchTree.addBranch(4, 5, null, "node 4 to 5", () -> System.out.println("I'm node 5"));
        searchTree.addBranch(4, 6, null, "node 4 to 6", () -> System.out.println("I'm node 6"));
        searchTree.addBranch(4, 7);
        searchTree.addBranch(1, 8);

        /*
        searchTree.addBranch(0, 1);
        searchTree.addBranch(1, 2);
        searchTree.addBranch(1, 3);
        searchTree.addBranch(1, 4);
        searchTree.addBranch(4, 5);
        searchTree.addBranch(4, 6);
        searchTree.addBranch(4, 7);
        searchTree.addBranch(4, 8);
         */

        autoGrowingTree();
        visualSearchTree = new VisualSearchTree(searchTree);
        visual = visualSearchTree.update();
        Scene scene = new Scene(visual, 1200, 600);
        moveOnDrag(scene, visual);
        zoomOnSCroll(visual);
        return scene;
    }

    private void autoGrowingTree() {
        if (searchTree.isEmpty()) {
            searchTree.addBranch(0, 1, () -> {
                growTree(1);
            });
        } else {
            count = searchTree.nNodes();
            for (int node : searchTree.getNodes()) {
                int finalNode = node;
                searchTree.setAction(node, () -> growTree(finalNode));
            }
        }
    }

    private void growTree(int from) {
        int val = count;
        searchTree.addBranch(from, val, () -> growTree(val));
        System.out.println("growing from node " + from + " to node " + count);
        visualSearchTree.update();
        count++;
    }

}
