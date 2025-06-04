package de.topobyte.forceterm;

import javax.swing.SwingUtilities;

public class RunForceTerm {

    public static void main(String[] args) {
        // Create and show this application's GUI in the event-dispatching thread.
        ForceTerm tabbedTerminal = new ForceTerm();
        SwingUtilities.invokeLater(tabbedTerminal::createAndShowGUI);
    }

}
