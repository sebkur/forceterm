package de.topobyte.forceterm.settings.options;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BooleanOption implements TwoComponentOption {

    private JLabel label = new JLabel();
    private JPanel panel;
    private JCheckBox box;

    /**
     * Create a component that shows a checkbox with label in a boxlayout.
     * 
     * @param title
     *            the label of the checkbox
     * @param state
     *            the initial state of the checkbox
     */
    public BooleanOption(String labelText, String text, boolean state) {
        label.setText(labelText);

        panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        box = new JCheckBox(text);
        box.setSelected(state);
        panel.add(box);
    }

    /**
     * @return the checkbox within the component
     */
    public JCheckBox getCheckBox() {
        return box;
    }

    @Override
    public JComponent getFirstComponent() {
        return label;
    }

    @Override
    public JComponent getSecondComponent() {
        return panel;
    }

}
