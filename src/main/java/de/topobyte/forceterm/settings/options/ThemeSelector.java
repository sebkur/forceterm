package de.topobyte.forceterm.settings.options;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import de.topobyte.forceterm.preferences.Theme;
import de.topobyte.forceterm.settings.Configuration;
import de.topobyte.swing.util.ElementWrapper;
import de.topobyte.swing.util.combobox.ListComboBoxModel;

public class ThemeSelector extends JComboBox<ElementWrapper<ThemeSelector.Entry>> {

    private static final long serialVersionUID = 1L;
    private List<Entry> entries;

    public ThemeSelector(Configuration configuration) {
        entries = new ArrayList<>();
        entries.add(new Entry(Theme.LIGHT, "Light mode"));
        entries.add(new Entry(Theme.DARK, "Dark mode"));

        ListComboBoxModel<Entry> model = new ListComboBoxModel<>(entries) {

            @Override
            public String toString(Entry element) {
                return element.value;
            }
        };

        setModel(model);

        int index = 0;
        Theme theme = configuration.getTheme();
        if (theme != null) {
            for (int i = 0; i < entries.size(); i++) {
                if (theme.equals(entries.get(i).key)) {
                    index = i;
                    break;
                }
            }
        }

        setSelectedIndex(index);
    }

    public Theme getSelectedTheme() {
        int index = getSelectedIndex();
        Entry entry = entries.get(index);
        return entry.key;
    }

    static class Entry {

        private Theme key;
        private String value;

        public Entry(Theme key, String value) {
            this.key = key;
            this.value = value;
        }

    }

}