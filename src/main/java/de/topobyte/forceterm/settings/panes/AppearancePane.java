package de.topobyte.forceterm.settings.panes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jediterm.terminal.CursorShape;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.forceterm.EnumUtil;
import de.topobyte.forceterm.SwingHelpers;
import de.topobyte.forceterm.preferences.Theme;
import de.topobyte.forceterm.settings.Configuration;
import de.topobyte.forceterm.settings.SettingsPane;
import de.topobyte.forceterm.settings.options.EnumSelector;
import de.topobyte.forceterm.settings.options.LabeledOption;
import de.topobyte.swing.util.BorderHelper;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class AppearancePane extends SettingsPane {

    private static final long serialVersionUID = 1L;

    final static Logger logger = LoggerFactory.getLogger(AppearancePane.class);

    private EnumSelector<Theme> themeSelector;
    private EnumSelector<CursorShape> cursorStyleSelector;

    /**
     * Constructor
     * 
     * @param configuration
     *            the configuration to edit.
     */
    public AppearancePane(Configuration configuration) {
        setLayout(new GridBagLayout());
        BorderHelper.addEmptyBorder(this, 0, 5, 0, 5);

        cursorStyleSelector = new EnumSelector<>(Arrays.asList(CursorShape.values()), shape -> EnumUtil.toCamelCaseWithSpaces(shape.name()),
                () -> configuration.getCursorShape());

        themeSelector = new EnumSelector<>(Arrays.asList(Theme.LIGHT, Theme.DARK), t -> t == Theme.LIGHT ? "Light Theme" : "Dark Theme",
                () -> configuration.getTheme());

        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraintsEditor ce = new GridBagConstraintsEditor(c);
        ce.fill(GridBagConstraints.BOTH).gridY(0);

        JLabel labelHeadline = new JLabel("Appearance");
        SwingHelpers.setBold(labelHeadline);
        BorderHelper.addEmptyBorder(labelHeadline, 5, 0, 5, 0);

        addAsRow(labelHeadline, ce);
        addAsRow(new LabeledOption("Theme:", themeSelector), ce);
        addAsRow(new LabeledOption("Cursor style:", cursorStyleSelector), ce);
    }

    /**
     * Set the values of the denoted configuration instance according to the
     * settings in this GUI.
     * 
     * @param configuration
     *            the configuration whose values to set.
     */
    public void setValues(Configuration configuration) {
        configuration.setTheme(themeSelector.getSelectedValue());
        configuration.setCursorShape(cursorStyleSelector.getSelectedValue());
    }

}
