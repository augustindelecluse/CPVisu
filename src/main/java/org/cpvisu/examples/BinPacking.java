package org.cpvisu.examples;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.cpvisu.VisualBinPacking;
import java.util.concurrent.ThreadLocalRandom;

public class BinPacking extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        int nBin = 20;
        int width = 70;
        int maxHeight = 600;

        VisualBinPacking binPacking = new VisualBinPacking(nBin, width, maxHeight);
        Group rectangles = binPacking.initRectangles();
        Scene scene = new Scene(rectangles, nBin * width, maxHeight * 1.2);

        stage.setScene(scene);
        stage.setTitle("Bin Packing Problem");

        stage.show();

        Timeline timeline = new Timeline( // animation: every 1s, move a rectangle
                new KeyFrame(Duration.millis(1000), event -> {
                    boolean inserted = false;
                    do {
                        int from = ThreadLocalRandom.current().nextInt(nBin);
                        int to = ThreadLocalRandom.current().nextInt(nBin);
                        inserted = binPacking.moveRectangle(from, to);
                    } while (!inserted);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // always play the animation
        timeline.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
