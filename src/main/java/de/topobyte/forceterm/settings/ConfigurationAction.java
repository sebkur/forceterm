package de.topobyte.forceterm.settings;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.forceterm.ForceTerm;
import de.topobyte.forceterm.preferences.ForceTermPreferences;
import de.topobyte.swing.util.action.SimpleAction;

public class ConfigurationAction extends SimpleAction {

    private static final long serialVersionUID = -1L;

    final static Logger logger = LoggerFactory.getLogger(ConfigurationAction.class);

    private ForceTerm forceterm;

    private JDialog dialog;
    private ConfigurationEditor configurationEditor;

    public ConfigurationAction(ForceTerm forceterm) {
        super("Settings", "Show dialog for configuring settings");
        this.forceterm = forceterm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.debug("configure settings");
        showDialog(new Configuration());
    }

    private void showDialog(Configuration configuration) {
        configurationEditor = new ConfigurationEditor(configuration) {

            private static final long serialVersionUID = 1L;

            @Override
            public void ok() {
                ConfigurationAction.this.ok();
            }

            @Override
            public void cancel() {
                ConfigurationAction.this.cancel();
            }

        };

        JFrame frame = forceterm.getFrame();
        dialog = new JDialog(frame, "ForceTerm Settings");
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        dialog.setContentPane(configurationEditor);

        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closing();
            }
        });

        dialog.setModal(true);
        dialog.setSize(500, 500);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    /**
     * Called when dialog is submitted.
     */
    protected void ok() {
        logger.debug("OK");
        Configuration configuration = configurationEditor.getConfiguration();

        ForceTermPreferences.setUseCustomShell(configuration.isUseCustomShell());
        ForceTermPreferences.setCustomShellCommand(configuration.getCustomShellCommand());
        ForceTermPreferences.setTheme(configuration.getTheme());
        ForceTermPreferences.setCursorShape(configuration.getCursorShape());

        forceterm.setCursorShape(configuration.getCursorShape());
        forceterm.setTheme(configuration.getTheme());

        dialog.dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(dialog, message, "error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Called when dialog has been canceled.
     */
    protected void cancel() {
        logger.debug("CANCEL");
        dialog.dispose();
    }

    /**
     * Called when dialog is closing.
     */
    protected void closing() {
        logger.debug("CLOSING");
        dialog.dispose();
    }

}
