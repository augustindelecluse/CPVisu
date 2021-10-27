package org.cpvisu.examples;

import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.cpvisu.VisualApplication;

import java.util.concurrent.atomic.AtomicInteger;

import static org.cpvisu.AnimationFactory.*;

public class Playground extends VisualApplication {
    @Override
    public Scene application(Stage stage) {
        int width = 300;
        int height = 300;
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        Group group = new Group(rectangle);

        Scene scene = new Scene(group, width, height);

        /*
        animate(1, (e) -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.ONE, rectangle);
            double x = (width*2 - width) / 2;
            double y = (height*2 - height) / 2;
            translateTransition.setToX(x);
            translateTransition.setToY(y);
            translateTransition.play();
            System.out.println(x);
            System.out.println(y);
            System.out.println(stage.getHeight());
            System.out.println(scene.getHeight());
        }, 1500, 1);

        animate(1, (e) -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.ONE, rectangle);
            double x = 2;
            double y = 2;
            scaleTransition.setToX(x);
            scaleTransition.setToY(y);
            scaleTransition.play();
            System.out.println(x);
            System.out.println(y);
        }, 3000, 1);

         */

        autoResize(scene, group);

        /*
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), rectangle);
        AtomicInteger i = new AtomicInteger(1);

        scaleTransition.setToX(1.0/ i.get());
        scaleTransition.setDelay(Duration.millis(3000));
        scaleTransition.play();

        animateForever(2000, () -> {
            i.incrementAndGet();
            scaleTransition.stop();
            scaleTransition.setToX(1.0 / i.get());
            scaleTransition.playFromStart();
            System.out.println("played");
        });

         */

        return scene;
    }
}
