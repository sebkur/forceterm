package de.topobyte.forceterm;

import org.jetbrains.annotations.NotNull;

import com.jediterm.core.Color;
import com.jediterm.terminal.emulator.ColorPalette;

public class ColorPaletteImpl extends ColorPalette {

    private final Color[] colors;

    public ColorPaletteImpl(@NotNull Color[] colors) {
        this.colors = colors;
    }

    @NotNull
    @Override
    public Color getForegroundByColorIndex(int colorIndex) {
        return colors[colorIndex];
    }

    @NotNull
    @Override
    protected Color getBackgroundByColorIndex(int colorIndex) {
        return colors[colorIndex];
    }
}
