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

        /*
        DARPInstance instance = DARPInstance.readFromFile("data/darp/Cordeau2003/a3-24.txt");
        VisualDARP visualDARP = new VisualDARP(instance, width, height);
        Pane pane = visualDARP.nodeLayout();
        SplitPane splitPane = new SplitPane();

        VBox rightControl = new VBox(new Label("Right Control"));

        splitPane.getItems().addAll(pane, rightControl);

        Scene scene = new Scene(splitPane, width + 500, height);
         */
        String[] nodes = {"node 1", "node 2", "node 3"};
        DARPGanttChart chart = DARPGanttChart.fromCategories(nodes);
        chart.setTimeSlot(nodes[0], 0, 100, 20, 30);
        chart.setTimeSlot(nodes[1], 0, 100, 40, 50);
        chart.setTimeSlot(nodes[2], 0, 200, 50, 200);
        Scene scene = new Scene(chart, width + 500, height);
        stage.setTitle("Dial-A-Ride Problem");
        //autoResize(scene, true);
        return scene;
    }
}
