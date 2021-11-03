package org.cpvisu.util.colors;

import javafx.scene.paint.Color;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * provides colors for drawing of shapes
 */
public class ColorFactory {

    /**
     * gives a random color
     * @return random color
     */
    public static Color randomColor() {
        int red = ThreadLocalRandom.current().nextInt(255);
        int green = ThreadLocalRandom.current().nextInt(255);
        int blue = ThreadLocalRandom.current().nextInt(255);
        return Color.rgb(red, green, blue);
    }

    /**
     * return a
     * @param palette
     * @return
     */
    public static ColorPalette getPalette(String palette) {
        switch (palette) {
            case "default": return new DefaultPalette();
            case "random": return new RandomPalette();
            default: return new DefaultPalette();
        }
    }

}
