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
        int binWidth = 70;
        int maxHeight = 600;

        int width = nBin * binWidth;
        int height = maxHeight;

        VisualBinPacking binPacking = new VisualBinPacking(nBin, binWidth, maxHeight);
        Group rectangles = binPacking.initRectangles();
        Scene scene = new Scene(rectangles, width, maxHeight);

        stage.setTitle("Bin Packing Problem");
        autoResize(scene, rectangles);

        animateForever(500, () -> {
            boolean inserted = false;
            do {
                int i = ThreadLocalRandom.current().nextInt(nBin);
                int j = ThreadLocalRandom.current().nextInt(nBin);
                int binSize = binPacking.binSize(i);
                if (binSize != 0) {
                    int rectangle = ThreadLocalRandom.current().nextInt(binSize);
                    inserted = binPacking.moveRectangle(250, rectangle, i, j);
                }
            } while (!inserted);
        });

        return scene;
    }

}
