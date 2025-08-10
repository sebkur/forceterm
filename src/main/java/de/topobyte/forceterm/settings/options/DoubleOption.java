package de.topobyte.forceterm.settings.options;

import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class DoubleOption implements TwoComponentOption {

    private JLabel label = new JLabel();
    private JTextField input = new JTextField();

    /**
     * Create a new option for double values
     * 
     * @param labelText
     *            the text for the label
     * @param value
     *            the initial value in the input field.
     */
    public DoubleOption(String labelText, double value) {
        label.setText(labelText);
        input.setText(String.format(Locale.US, "%.6f", value));
    }

    /**
     * @return the label.
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * @return the textfield.
     */
    public JTextField getInput() {
        return input;
    }

    @Override
    public JComponent getFirstComponent() {
        return label;
    }

    @Override
    public JComponent getSecondComponent() {
        return input;
    }

    /**
     * @return the current value.
     */
    public double getValue() {
        return Double.parseDouble(input.getText());
    }

}
