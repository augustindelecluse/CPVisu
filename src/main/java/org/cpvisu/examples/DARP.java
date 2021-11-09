package org.cpvisu.examples;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualDARP;
import org.cpvisu.problems.DARPInstance;

import static org.cpvisu.AnimationFactory.autoResize;

public class DARP extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        DARPInstance instance = DARPInstance.readFromFile("data/darp/Cordeau2003/a3-24.txt");
        int width = 1000;
        int height = 500;
        VisualDARP visualDARP = new VisualDARP(instance, width, height);
        Pane pane = visualDARP.nodeLayout();
        SplitPane splitPane = new SplitPane();

        VBox rightControl = new VBox(new Label("Right Control"));

        splitPane.getItems().addAll(pane, rightControl);

        Scene scene = new Scene(splitPane, width + 500, height);
        stage.setTitle("Dial-A-Ride Problem");
        //autoResize(scene, true);
        return scene;
    }
}
