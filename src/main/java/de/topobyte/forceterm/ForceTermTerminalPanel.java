package de.topobyte.forceterm;

import com.jediterm.terminal.DefaultTerminalCopyPasteHandler;
import com.jediterm.terminal.TerminalCopyPasteHandler;
import com.jediterm.terminal.model.StyleState;
import com.jediterm.terminal.model.TerminalTextBuffer;
import com.jediterm.terminal.ui.TerminalAction;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.TerminalPanel;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.jediterm.terminal.ui.UtilKt.isWindows;

public class ForceTermTerminalPanel extends TerminalPanel {

    private static final Logger LOG = LoggerFactory.getLogger(ForceTermTerminalPanel.class);

    public ForceTermTerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull TerminalTextBuffer terminalTextBuffer,
            @NotNull StyleState styleState) {
        super(settingsProvider, terminalTextBuffer, styleState);
    }

    // Keep our own copy of myBracketedPasteMode mirrored from TerminalPanel
    // because it is private there
    private volatile boolean myBracketedPasteMode;
    // Keep a fixed per-panel singleton copy and paste handler that we provide
    // to the parent class, too.
    private DefaultTerminalCopyPasteHandler copyPasteHandler = null;

    private @NotNull TerminalCopyPasteHandler createOrGetPasteHandler() {
        if (copyPasteHandler == null) {
            copyPasteHandler = new DefaultTerminalCopyPasteHandler();
        }
        return copyPasteHandler;
    }

    @Override
    protected @NotNull TerminalCopyPasteHandler createCopyPasteHandler() {
        return createOrGetPasteHandler();
    }

    @Override
    public void setBracketedPasteMode(boolean bracketedPasteModeEnabled) {
        super.setBracketedPasteMode(bracketedPasteModeEnabled);
        myBracketedPasteMode = bracketedPasteModeEnabled;
    }

    public List<TerminalAction> getActions() {
        List<TerminalAction> actions = new ArrayList<>(super.getActions());
        if (PlatformUtil.getOS() == OperatingSystem.LINUX) {
            // Linux as opposed to macOS and Windows does have an additional
            // selection clipboard.
            // Add an action to paste from this one using Shift-Insert so that
            // we can have copy-paste behavior that is consistent with
            // experience
            // in other Linux apps.
            addShiftInsertPasteAction(actions);
        }
        return actions;
    }

    private void addShiftInsertPasteAction(List<TerminalAction> actions) {
        // Find existing Paste action to add our action behind it.
        int indexPaste = 0;
        for (int i = 0; i < actions.size(); i++) {
            TerminalAction action = actions.get(i);
            if (action.getName().equals("Paste")) {
                indexPaste = i;
                break;
            }
        }
        actions.add(indexPaste + 1, new TerminalAction(getPasteSystemSelectionActionPresentation(), input -> {
            pasteFromClipboard(true);
            return true;
        }).withMnemonicKey(KeyEvent.VK_S).withEnabledSupplier(() -> createOrGetPasteHandler().getContents(true) != null));
    }

    private @NotNull TerminalActionPresentation getPasteSystemSelectionActionPresentation() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK);
        return new TerminalActionPresentation("Paste Selection", keyStroke);
    }

    // Copy of private superclass method.
    private void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {
        String text = copyPasteHandler.getContents(useSystemSelectionClipboardIfAvailable);

        if (text == null) {
            return;
        }

        try {
            // Sanitize clipboard text to use CR as the line separator.
            // See https://github.com/JetBrains/jediterm/issues/136.
            if (!isWindows()) {
                // On Windows, Java automatically does this CRLF->LF
                // sanitization, but other terminals on Unix typically also do
                // this sanitization, so maybe JediTerm also should.
                text = text.replace("\r\n", "\n");
            }
            text = text.replace('\n', '\r');

            if (myBracketedPasteMode) {
                text = "\u001b[200~" + text + "\u001b[201~";
            }

            getTerminalOutputStream().sendString(text, true);
        } catch (RuntimeException e) {
            LOG.info("", e);
        }
    }

}
