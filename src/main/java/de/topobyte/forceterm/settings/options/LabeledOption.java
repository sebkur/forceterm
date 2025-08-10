package de.topobyte.forceterm.settings.options;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class LabeledOption implements TwoComponentOption {

    private JLabel label;
    private JComponent component;

    public LabeledOption(String label, JComponent component) {
        this.label = new JLabel(label);
        this.component = component;
    }

    @Override
    public JComponent getFirstComponent() {
        return label;
    }

    @Override
    public JComponent getSecondComponent() {
        return component;
    }

}
