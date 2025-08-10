package de.topobyte.forceterm;

import org.jetbrains.annotations.NotNull;

import com.jediterm.terminal.model.StyleState;
import com.jediterm.terminal.model.TerminalTextBuffer;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.TerminalPanel;
import com.jediterm.terminal.ui.settings.SettingsProvider;

// We need our own widget class extending JediTermWidget so that we can let it create our own
// TerminalPanel subclass instead of the default one.
public class ForceTermWidget extends JediTermWidget {

    public ForceTermWidget(@NotNull SettingsProvider settingsProvider) {
        super(settingsProvider);
    }

    public ForceTermWidget(int columns, int lines, SettingsProvider settingsProvider) {
        super(columns, lines, settingsProvider);
    }

    @Override
    protected TerminalPanel createTerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull StyleState styleState,
            @NotNull TerminalTextBuffer terminalTextBuffer) {
        return new ForceTermTerminalPanel(settingsProvider, terminalTextBuffer, styleState);
    }

}
