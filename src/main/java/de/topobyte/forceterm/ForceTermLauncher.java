package de.topobyte.forceterm;

import java.util.List;

import javax.swing.SwingUtilities;

import de.topobyte.forceterm.init.InitMode;

public class ForceTermLauncher {

    public static void launchSingle(InitMode init) {
        // Create and show this application's GUI in the event-dispatching
        // thread.
        ForceTerm tabbedTerminal = new ForceTerm();
        SwingUtilities.invokeLater(() -> {
            tabbedTerminal.createAndShowGUI(init);
        });
    }

    public static void launchMultiple(List<InitMode> inits) {
        // Create and show this application's GUI in the event-dispatching
        // thread.
        ForceTerm tabbedTerminal = new ForceTerm();
        SwingUtilities.invokeLater(() -> {
            tabbedTerminal.createAndShowGUI(inits);
        });
    }

}
