package de.topobyte.forceterm;

import de.topobyte.forceterm.preferences.Theme;

public class Terminal {

    private final CustomSettingsProvider settingsProvider;
    private final ForceTermWidget widget;

    public Terminal(Theme theme) {
        settingsProvider = new CustomSettingsProvider(theme);
        widget = new ForceTermWidget(80, 24, settingsProvider);
    }

    public CustomSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public ForceTermWidget getWidget() {
        return widget;
    }

}
