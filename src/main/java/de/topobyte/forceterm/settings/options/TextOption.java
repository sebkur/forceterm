package de.topobyte.forceterm.settings.options;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TextOption implements TwoComponentOption {

    private JLabel label = new JLabel();
    private JTextField input = new JTextField();

    /**
     * Create a new option for text values
     * 
     * @param labelText
     *            the text for the label
     * @param value
     *            the initial value in the input field.
     */
    public TextOption(String labelText, String value) {
        label.setText(labelText);
        input.setText(value);
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
    public String getValue() {
        return input.getText();
    }

}
