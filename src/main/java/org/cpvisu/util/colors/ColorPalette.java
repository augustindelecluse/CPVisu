package org.cpvisu.util.colors;

import javafx.scene.paint.Color;

public abstract class ColorPalette {

    private final Color[] palette;
    private int n = 0; // index of the current selected color in the palette

    public ColorPalette(Color... palette) {
        this.palette = palette;
    }

    /**
     * number f colors available
     * @return
     */
    public int nColors() {return palette.length;}

    /**
     * gives the next color in the palette. Reset the palette if all elements from it have been provided
     * @return
     */
    public Color nextColor() {
        Color color = palette[n++];
        n = n % palette.length;
        return color;
    }

    public Color colorAt(int i) {
        return palette[i % palette.length];
    }

    @Override
    public String toString() {
        return "color palette";
    }

}
