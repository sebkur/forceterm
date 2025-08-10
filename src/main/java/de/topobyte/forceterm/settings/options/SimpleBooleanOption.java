package de.topobyte.forceterm.settings.options;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class SimpleBooleanOption extends JPanel {

    private static final long serialVersionUID = -1L;

    private JCheckBox box;

    /**
     * Create a component that shows a checkbox with label in a boxlayout.
     * 
     * @param title
     *            the label of the checkbox
     * @param state
     *            the initial state of the checkbox
     */
    public SimpleBooleanOption(String title, boolean state) {
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
        box = new JCheckBox(title);
        box.setSelected(state);
        add(box);
    }

    /**
     * @return the checkbox within the component
     */
    public JCheckBox getCheckBox() {
        return box;
    }

}
