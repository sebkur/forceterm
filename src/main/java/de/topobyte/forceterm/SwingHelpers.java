package de.topobyte.forceterm;

import java.awt.Font;

import javax.swing.JLabel;

public class SwingHelpers {

    public static void setBold(JLabel label) {
        Font f = label.getFont();
        label.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    }

}
