package de.topobyte.forceterm.preferences;

import com.jediterm.terminal.CursorShape;

import java.util.prefs.Preferences;

public class ForceTermPreferences {

    public static final String THEME = "theme";
    public static final String CURSOR_SHAPE = "cursor-shape";

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

    public static CursorShape getCursorShape() {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        String shape = node.get(CURSOR_SHAPE, null);
        for (CursorShape cs : CursorShape.values()) {
            if (cs.name().equals(shape)) {
                return cs;
            }
        }
        return CursorShape.STEADY_BLOCK;
    }

    public static void setCursorShape(CursorShape shape) {
        Preferences node = Preferences
                .userNodeForPackage(ForceTermPreferences.class);

        node.put(CURSOR_SHAPE, shape.name());
    }

}
