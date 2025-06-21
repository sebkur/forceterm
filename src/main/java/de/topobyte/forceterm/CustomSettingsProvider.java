package de.topobyte.forceterm;

import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

public class CustomSettingsProvider extends DefaultSettingsProvider {

    private ColorPalette palette;

    @Override
    public ColorPalette getTerminalColorPalette() {
        return palette;
    }

    public void setTerminalColorPalette(ColorPalette palette) {
        this.palette = palette;
    }

    public CustomSettingsProvider(boolean darkMode) {
        if (darkMode) {
            palette = new ColorPaletteImpl(ColorPalettes.XTERM_COLORS_DARK);
        } else {
            palette = new ColorPaletteImpl(ColorPalettes.XTERM_COLORS);
        }
    }

}
