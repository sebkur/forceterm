package de.topobyte.forceterm.preferences;

import java.util.prefs.Preferences;

public class ForceTermPreferences {

    public static final String THEME = "theme";

    public static Theme getTheme() {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        String theme = node.get(THEME, null);
        if ("light".equals(theme)) {
            return Theme.LIGHT;
        } else if ("dark".equals(theme)) {
            return Theme.DARK;
        }
        return Theme.LIGHT;
    }

    public static void setTheme(Theme theme) {
        Preferences node = Preferences
                .userNodeForPackage(ForceTermPreferences.class);

        if (theme == Theme.LIGHT) {
            node.put(THEME, "light");
        } else if (theme == Theme.DARK) {
            node.put(THEME, "dark");
        }
    }

}
