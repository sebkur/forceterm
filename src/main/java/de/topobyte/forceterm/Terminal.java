package de.topobyte.forceterm;

import com.jediterm.terminal.ui.JediTermWidget;
import de.topobyte.forceterm.preferences.Theme;

public class Terminal {

    private final CustomSettingsProvider settingsProvider;
    private final JediTermWidget widget;

    public Terminal(Theme theme) {
        settingsProvider = new CustomSettingsProvider(theme);
        widget = new JediTermWidget(80, 24, settingsProvider);
    }

    public CustomSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public JediTermWidget getWidget() {
        return widget;
    }
}
