package org.cpvisu.util.colors;

import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class RandomPalette extends ColorPalette {

    public RandomPalette() {
        super(null);
    }

    /**
     * number f colors available
     * @return
     */
    @Override
    public int nColors() {return Integer.MAX_VALUE;}

    /**
     * gives the next color in the palette. Reset the palette if all elements from it have been provided
     * @return
     */
    public Color nextColor() {
        int red = ThreadLocalRandom.current().nextInt(255);
        int green = ThreadLocalRandom.current().nextInt(255);
        int blue = ThreadLocalRandom.current().nextInt(255);
        return Color.rgb(red, green, blue);
    }

    @Override
    public String toString() {
        return "Random color palette";
    }

}
