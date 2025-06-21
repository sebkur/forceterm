package de.topobyte.forceterm;

import de.topobyte.shared.preferences.SharedPreferences;
import de.topobyte.swing.util.SwingUtils;

import javax.swing.SwingUtilities;

public class RunForceTerm {

    public static void main(String[] args) {
        if (SharedPreferences.isUIScalePresent()) {
            SwingUtils.setUiScale(SharedPreferences.getUIScale());
        }

        // Create and show this application's GUI in the event-dispatching thread.
        ForceTerm tabbedTerminal = new ForceTerm();
        SwingUtilities.invokeLater(tabbedTerminal::createAndShowGUI);
    }

}
