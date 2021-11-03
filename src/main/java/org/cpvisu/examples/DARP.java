package org.cpvisu.examples;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualDARP;
import org.cpvisu.problems.DARPInstance;
import org.cpvisu.util.colors.ColorFactory;
import org.cpvisu.util.colors.ColorPalette;

public class DARP extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        DARPInstance instance = DARPInstance.readFromFile("data/darp/Cordeau2003/a3-24.txt");
        int width = 1000;
        int height = 500;
        VisualDARP visualDARP = new VisualDARP(instance, width, height);
        Group group = visualDARP.init();
        Scene scene = new Scene(group, width, height);
        return scene;
    }
}
