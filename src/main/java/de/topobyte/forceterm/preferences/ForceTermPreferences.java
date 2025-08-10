package de.topobyte.forceterm.preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.jediterm.terminal.CursorShape;

public class ForceTermPreferences {

    public static final String USE_CUSTOM_SHELL = "use-custom-shell";
    public static final String CUSTOM_SHELL_COMMAND = "custom-shell-command";

    public static final String THEME = "theme";
    public static final String CURSOR_SHAPE = "cursor-shape";

    public static boolean isUseCustomShell() {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        return node.getBoolean(USE_CUSTOM_SHELL, false);
    }

    public static void setUseCustomShell(boolean useCustomShell) {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        node.putBoolean(USE_CUSTOM_SHELL, useCustomShell);
    }

    public static String getCustomShellCommand() {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        return node.get(CUSTOM_SHELL_COMMAND, null);
    }

    public static void setCustomShellCommand(String customShellCommand) {
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);
        node.put(CUSTOM_SHELL_COMMAND, customShellCommand);
    }

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
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);

        if (theme == Theme.LIGHT) {
            node.put(THEME, "light");
        } else if (theme == Theme.DARK) {
            node.put(THEME, "dark");
        }
        try {
            node.flush(); // try to update for other instances instantly
        } catch (BackingStoreException e) {
            // Ignore
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
        Preferences node = Preferences.userNodeForPackage(ForceTermPreferences.class);

        node.put(CURSOR_SHAPE, shape.name());
        try {
            node.flush(); // try to update for other instances instantly
        } catch (BackingStoreException e) {
            // Ignore
        }
    }

}
