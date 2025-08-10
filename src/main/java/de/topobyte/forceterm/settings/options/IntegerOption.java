package de.topobyte.forceterm.settings.options;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class IntegerOption implements TwoComponentOption {

    private JLabel label = new JLabel();
    private JTextField input = new JTextField();

    /**
     * Create a new option for integer values
     * 
     * @param labelText
     *            the text for the label
     * @param value
     *            the initial value in the input field.
     */
    public IntegerOption(String labelText, int value) {
        label.setText(labelText);
        input.setText(String.format("%d", value));
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
     * @return the current value
     */
    public int getValue() {
        return Integer.parseInt(input.getText());
    }

}
