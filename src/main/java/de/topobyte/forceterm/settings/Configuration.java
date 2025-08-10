package de.topobyte.forceterm.settings;

import com.jediterm.terminal.CursorShape;

import de.topobyte.forceterm.preferences.ForceTermPreferences;
import de.topobyte.forceterm.preferences.Theme;

public class Configuration {

    private boolean useCustomShell = ForceTermPreferences.isUseCustomShell();
    private String customShellCommand = ForceTermPreferences.getCustomShellCommand();

    private Theme theme = ForceTermPreferences.getTheme();
    private CursorShape cursorShape = ForceTermPreferences.getCursorShape();

    public boolean isUseCustomShell() {
        return useCustomShell;
    }

    public void setUseCustomShell(boolean useCustomShell) {
        this.useCustomShell = useCustomShell;
    }

    public String getCustomShellCommand() {
        return customShellCommand;
    }

    public void setCustomShellCommand(String customShellCommand) {
        this.customShellCommand = customShellCommand;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public CursorShape getCursorShape() {
        return cursorShape;
    }

    public void setCursorShape(CursorShape cursorShape) {
        this.cursorShape = cursorShape;
    }

}
