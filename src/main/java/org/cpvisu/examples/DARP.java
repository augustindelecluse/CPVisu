package org.cpvisu.examples;

import javafx.scene.Scene;
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
        Scene scene = visualDARP.init();
        autoResize(scene, true);
        return scene;
    }
}
