package org.cpvisu.examples;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualSearchTree;
import org.cpvisu.problems.SearchTree;
import org.cpvisu.problems.SearchTreeNode;
import org.cpvisu.shapes.VisualTextRectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class DFS extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        SearchTree searchTree = new SearchTree();
        searchTree.addBranch(0, 1);
        searchTree.addBranch(0, 2);
        searchTree.addBranch(1, 3);
        VisualSearchTree visualSearchTree = new VisualSearchTree(searchTree);
        Group visual = visualSearchTree.update();
        Scene scene = new Scene(visual, 1200, 600);
        return scene;
    }

}
