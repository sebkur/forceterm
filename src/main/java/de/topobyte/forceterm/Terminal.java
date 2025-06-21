package de.topobyte.forceterm;

import com.jediterm.terminal.ui.JediTermWidget;

public class Terminal {

    private final CustomSettingsProvider settingsProvider;
    private final JediTermWidget widget;

    public Terminal(boolean darkMode) {
        settingsProvider = new CustomSettingsProvider(darkMode);
        widget = new JediTermWidget(80, 24, settingsProvider);
    }

    public CustomSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public JediTermWidget getWidget() {
        return widget;
    }
}
