package org.cpvisu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class VisualApplication extends Application {

    public abstract Scene application(Stage stage);

    public void start(Stage stage) {
        Scene scene = application(stage);
        stage.setScene(scene);
        stage.show();
    }

}
