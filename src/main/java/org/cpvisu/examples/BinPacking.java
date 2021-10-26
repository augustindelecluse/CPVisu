package org.cpvisu.examples;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.stage.Stage;
import org.cpvisu.VisualApplication;
import org.cpvisu.VisualBinPacking;

import java.util.concurrent.ThreadLocalRandom;

import static org.cpvisu.AnimationFactory.*;

public class BinPacking extends VisualApplication {

    @Override
    public Scene application(Stage stage) {
        int nBin = 20;
        int width = 70;
        int maxHeight = 600;

        VisualBinPacking binPacking = new VisualBinPacking(nBin, width, maxHeight);
        Group rectangles = binPacking.initRectangles();
        Scene scene = new Scene(rectangles, nBin * width, maxHeight * 1.2);

        animateForever(1000, () -> {
            boolean inserted = false;
            do {
                int i = ThreadLocalRandom.current().nextInt(nBin);
                int j = ThreadLocalRandom.current().nextInt(nBin);
                int binSize = binPacking.binSize(i);
                if (binSize != 0) {
                    int rectangle = ThreadLocalRandom.current().nextInt(binSize);
                    inserted = binPacking.moveRectangle(500, rectangle, i, j);
                }
            } while (!inserted);
        });

        stage.setTitle("Bin Packing Problem");
        return scene;
    }

}
