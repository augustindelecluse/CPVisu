package org.cpvisu.examples;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualDARP;
import org.cpvisu.chart.DARPGanttChart;
import org.cpvisu.problems.DARPInstance;

import static org.cpvisu.AnimationFactory.autoResize;

public class DARP extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        int width = 1000;
        int height = 500;
        SplitPane splitPane = new SplitPane(); // container for the whole visualisation

        // x, y visualisation
        DARPInstance instance = DARPInstance.readFromFile("data/darp/Cordeau/a3-24.txt");
        Integer [] order = new Integer[] {49, 15, 39, 1, 25, 9, 33, 14, 38, 5, 29, 52};
        instance.mapNodes(order, 1, 0, 2);

        // possible solution for this instance:
        /*
         * 48 -> 13 -> 37 -> 21 -> 45 -> 10 -> 23 -> 34 -> 47 -> 20 ->  7 -> 44 -> 31 ->  0 ->  6 -> 24 -> 30 -> 22 -> 11 -> 46 -> 35 ->  4 -> 28 ->  8 -> 32 ->  2 -> 26 -> 51
         * 49 -> 15 -> 39 ->  1 -> 25 ->  9 -> 33 -> 14 -> 38 ->  5 -> 29 -> 52
         * 50 -> 19 -> 43 -> 17 -> 41 -> 12 -> 18 -> 36 -> 42 -> 16 ->  3 -> 40 -> 27 -> 53
         */

        VisualDARP visualDARP = new VisualDARP(instance, width, height);
        Pane pane = visualDARP.nodeLayout();

        /*
        // chart visualisation
        String[] nodes = {"node 1", "node 2", "node 3"};
        DARPGanttChart chart = DARPGanttChart.fromCategories(nodes);
        chart.setTimeSlot(nodes[0], 0, 100, 20, 30);
        chart.setTimeSlot(nodes[1], 0, 100, 40, 50);
        chart.setTimeSlot(nodes[2], 0, 200, 60, 200);
        chart.setTransition(nodes[0], 20, 20);
        chart.setTransition(nodes[1], 40, 10);
         */
        visualDARP.addRoute(0, order);
        DARPGanttChart chart = visualDARP.GanttLayout(0);
        splitPane.getItems().addAll(pane, chart);
        Scene scene = new Scene(splitPane, width + 500, height);
        stage.setTitle("Dial-A-Ride Problem");
        //autoResize(scene, true);
        return scene;
    }
}
