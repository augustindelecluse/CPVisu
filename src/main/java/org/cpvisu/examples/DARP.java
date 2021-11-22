package org.cpvisu.examples;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualDARP;
import org.cpvisu.chart.DARPGanttChart;
import org.cpvisu.chart.LoadProfileChart;
import org.cpvisu.problems.DARPInstance;

import static org.cpvisu.AnimationFactory.autoResize;

public class DARP extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        int width = 1000;
        int height = 500;

        DARPInstance instance = DARPInstance.readFromFile("data/darp/Cordeau/a3-24.txt");

        // possible solution for this instance:
        /*
         * 48, 13, 37, 21, 45, 10, 23, 34, 47, 20,  7, 44, 31,  0,  6, 24, 30, 22, 11, 46, 35,  4, 28,  8, 32,  2, 26, 51
         * 49, 15, 39,  1, 25,  9, 33, 14, 38,  5, 29, 52
         * 50, 19, 43, 17, 41, 12, 18, 36, 42, 16,  3, 40, 27, 53
         */

        Integer [] order1 = new Integer[] {48, 13, 37, 21, 45, 10, 23, 34, 47, 20,  7, 44, 31,  0,  6, 24, 30, 22, 11, 46, 35,  4, 28,  8, 32,  2, 26, 51};
        Integer [] order2 = new Integer[] {49, 15, 39,  1, 25,  9, 33, 14, 38,  5, 29, 52};
        Integer [] order3 = new Integer[] {50, 19, 43, 17, 41, 12, 18, 36, 42, 16,  3, 40, 27, 53};

        VisualDARP visualDARP = new VisualDARP(instance, width, height);
        visualDARP.addRoute(0, order1);
        visualDARP.addRoute(1, order2);
        visualDARP.addRoute(2, order3);

        Parent root = visualDARP.completeLayout();

        Scene scene = new Scene(root, width + 500, height);
        stage.setTitle("Dial-A-Ride Problem");
        return scene;
    }
}
