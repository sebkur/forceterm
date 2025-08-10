package de.topobyte.forceterm.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import de.topobyte.forceterm.settings.panes.AppearancePane;
import de.topobyte.forceterm.settings.panes.MiscOptionsPane;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public abstract class ConfigurationEditor extends JPanel implements ActionListener {

    private static final long serialVersionUID = -1L;

    private MiscOptionsPane miscOptions;
    private AppearancePane apperance;

    /**
     * Create a new editor for the denoted configuration.
     * 
     * @param configuration
     *            the configuration instance to edit.
     */
    public ConfigurationEditor(Configuration configuration) {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JTabbedPane tabbed = new JTabbedPane(SwingConstants.LEFT);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        add(tabbed, c);

        miscOptions = new MiscOptionsPane(configuration);
        JScrollPane jspMisc = new JScrollPane();
        jspMisc.setViewportView(miscOptions);

        apperance = new AppearancePane(configuration);
        JScrollPane jspAppearance = new JScrollPane();
        jspAppearance.setViewportView(apperance);

        tabbed.add("General", jspMisc);
        tabbed.add("Appearance", jspAppearance);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridLayout(1, 2));

        JButton buttonCancel = new JButton("Cancel");
        JButton buttonOk = new JButton("Ok");

        buttonGrid.add(buttonCancel);
        buttonGrid.add(buttonOk);

        buttons.add(Box.createHorizontalGlue());
        buttons.add(buttonGrid);

        c.gridy = 1;
        c.weighty = 0.0;
        add(buttons, c);

        buttonCancel.setActionCommand("cancel");
        buttonOk.setActionCommand("ok");
        buttonCancel.addActionListener(this);
        buttonOk.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
            ok();
        } else if (e.getActionCommand().equals("cancel")) {
            cancel();
        }
    }

    /**
     * Called when the OK button has been clicked
     */
    public abstract void ok();

    /**
     * Called when the cancel button has been clicked
     */
    public abstract void cancel();

    /**
     * @return the resulting configuration.
     */
    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        miscOptions.setValues(configuration);
        apperance.setValues(configuration);
        return configuration;
    }

}
