package org.cpvisu.util.colors;

import javafx.scene.paint.Color;

public final class DefaultPalette extends ColorPalette {

    // default color cycle, same as matplotlib https://matplotlib.org/stable/users/dflt_style_changes.html

    public DefaultPalette() {
        super(Color.web("1f77b4"),
                Color.web("ff7f0e"),
                Color.web("2ca02c"),
                Color.web("d62728"),
                Color.web("9467bd"),
                Color.web("8c564b"),
                Color.web("e377c2"),
                Color.web("7f7f7f"),
                Color.web("bcbd22"),
                Color.web("17becf"));
    }

    @Override
    public String toString() {
        return "Default color palette";
    }
}
