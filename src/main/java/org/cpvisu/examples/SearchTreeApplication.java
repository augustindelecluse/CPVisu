package org.cpvisu.examples;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualSearchTree;
import org.cpvisu.problems.SearchTree;

public class SearchTreeApplication extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        SearchTree searchTree = new SearchTree();
        searchTree.addBranch(0, 1, () -> System.out.println("I'm node 1"));
        searchTree.addBranch(1, 2, () -> System.out.println("I'm node 2"));
        searchTree.addBranch(1, 3, () -> System.out.println("I'm node 3"));
        searchTree.addBranch(0, 4, () -> System.out.println("I'm node 4"));
        searchTree.addBranch(4, 5, () -> System.out.println("I'm node 5"));
        searchTree.addBranch(4, 6, () -> System.out.println("I'm node 6"));


        VisualSearchTree visualSearchTree = new VisualSearchTree(searchTree);
        Group visual = visualSearchTree.update();
        Scene scene = new Scene(visual, 1200, 600);
        return scene;
    }

}
