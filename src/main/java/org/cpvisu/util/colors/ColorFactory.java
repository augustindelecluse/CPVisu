package org.cpvisu;

import javafx.scene.paint.Color;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * provides colors for drawing of shapes
 */
public class ColorFactory {

    private Random random;
    private int nDefault = 0;
    // default color cycle, same as matplotlib https://matplotlib.org/stable/users/dflt_style_changes.html
    public static Color[] CycleDefault = new Color[] {
            Color.web("1f77b4"),
            Color.web("ff7f0e"),
            Color.web("2ca02c"),
            Color.web("d62728"),
            Color.web("9467bd"),
            Color.web("8c564b"),
            Color.web("e377c2"),
            Color.web("7f7f7f"),
            Color.web("bcbd22"),
            Color.web("17becf"),
    };

    public ColorFactory(int seed) {
        random = new Random(seed);
    }

    public ColorFactory() {
        this(ThreadLocalRandom.current().nextInt());
    }

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
    public static Color[] getPalette(int n, String palette) {
        return null;
    }

    /**
     * return the next color within the usable colors set
     * @return
     */
    public Color nextColor() {
        Color color = CycleDefault[nDefault++];
        nDefault = nDefault % CycleDefault.length;
        return color;
    }

}
