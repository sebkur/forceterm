package de.topobyte.forceterm;

import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import de.topobyte.forceterm.preferences.Theme;

public class CustomSettingsProvider extends DefaultSettingsProvider {

    private ColorPalette palette;

    @Override
    public ColorPalette getTerminalColorPalette() {
        return palette;
    }

    public void setTerminalColorPalette(ColorPalette palette) {
        this.palette = palette;
    }

    public CustomSettingsProvider(Theme theme) {
        if (theme == null || theme == Theme.LIGHT) {
            palette = new ColorPaletteImpl(ColorPalettes.XTERM_COLORS);
        } else if (theme == Theme.DARK) {
            palette = new ColorPaletteImpl(ColorPalettes.XTERM_COLORS_DARK);
        }
    }

    @Override
    public boolean emulateX11CopyPaste() {
        return PlatformUtil.getOS() == OperatingSystem.LINUX;
    }
}
