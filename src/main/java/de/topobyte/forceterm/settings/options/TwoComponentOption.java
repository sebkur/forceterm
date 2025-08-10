package de.topobyte.forceterm.settings.options;

import javax.swing.JComponent;

/**
 * An interface for objects that encapsulate components that may be used for
 * instance in two column layouts.
 */
public interface TwoComponentOption {

    /**
     * @return the component for the first row
     */
    public JComponent getFirstComponent();

    /**
     * @return the component for the second row
     */
    public JComponent getSecondComponent();

}
