package de.topobyte.forceterm.settings;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.forceterm.settings.options.TwoComponentOption;
import de.topobyte.swing.util.BorderHelper;

public class SettingsPane extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    protected void addAsRow(JComponent component, GridBagConstraintsEditor ce) {
        ce.weightX(1).gridWidth(2).gridX(0);
        add(component, ce.getConstraints());
        ce.getConstraints().gridy++;
    }

    protected void addAsRow(TwoComponentOption option, GridBagConstraintsEditor ce) {
        addAsRow(option.getFirstComponent(), option.getSecondComponent(), ce);
        ce.getConstraints().gridy++;
    }

    protected void addAsRow(JComponent a, JComponent b, GridBagConstraintsEditor ce) {
        ce.weightX(0).gridWidth(1).gridX(0);
        BorderHelper.addEmptyBorder(a, 0, 0, 0, 5);
        add(a, ce.getConstraints());
        ce.weightX(0).gridX(1);
        add(b, ce.getConstraints());
    }

}
