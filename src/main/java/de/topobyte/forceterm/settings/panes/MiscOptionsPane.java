package de.topobyte.forceterm.settings.panes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.forceterm.SwingHelpers;
import de.topobyte.forceterm.settings.Configuration;
import de.topobyte.forceterm.settings.SettingsPane;
import de.topobyte.forceterm.settings.options.SimpleBooleanOption;
import de.topobyte.forceterm.settings.options.TextOption;
import de.topobyte.swing.util.BorderHelper;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class MiscOptionsPane extends SettingsPane {

    private static final long serialVersionUID = 1L;

    final static Logger logger = LoggerFactory.getLogger(MiscOptionsPane.class);

    private SimpleBooleanOption useCustomShell;
    private TextOption customShellCommand;

    /**
     * Constructor
     * 
     * @param configuration
     *            the configuration to edit.
     */
    public MiscOptionsPane(Configuration configuration) {
        setLayout(new GridBagLayout());
        BorderHelper.addEmptyBorder(this, 0, 5, 0, 5);

        useCustomShell = new SimpleBooleanOption("Use custom shell", configuration.isUseCustomShell());
        customShellCommand = new TextOption("Custom shell command:", configuration.getCustomShellCommand());

        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraintsEditor ce = new GridBagConstraintsEditor(c);
        ce.fill(GridBagConstraints.BOTH).gridY(0);

        JLabel labelHeadline = new JLabel("General");
        SwingHelpers.setBold(labelHeadline);
        BorderHelper.addEmptyBorder(labelHeadline, 5, 0, 5, 0);

        addAsRow(labelHeadline, ce);
        addAsRow(useCustomShell, ce);
        addAsRow(customShellCommand, ce);

        syncState();

        useCustomShell.getCheckBox().addChangeListener(e -> {
            syncState();
        });
    }

    private void syncState() {
        customShellCommand.getSecondComponent().setEnabled(useCustomShell.getCheckBox().isSelected());
    }

    /**
     * Set the values of the denoted configuration instance according to the
     * settings in this GUI.
     * 
     * @param configuration
     *            the configuration whose values to set.
     */
    public void setValues(Configuration configuration) {
        configuration.setUseCustomShell(useCustomShell.getCheckBox().isSelected());
        configuration.setCustomShellCommand(customShellCommand.getValue());
    }

}
