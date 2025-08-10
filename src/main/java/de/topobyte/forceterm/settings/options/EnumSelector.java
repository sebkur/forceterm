package de.topobyte.forceterm.settings.options;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JComboBox;

import de.topobyte.swing.util.ElementWrapper;
import de.topobyte.swing.util.combobox.ListComboBoxModel;

public class EnumSelector<T> extends JComboBox<ElementWrapper<EnumSelector.Entry<T>>> {

    private static final long serialVersionUID = 1L;
    private List<Entry<T>> entries;

    public EnumSelector(List<T> values, Function<T, String> namer, Supplier<T> getCurrent) {
        entries = new ArrayList<>();
        for (T value : values) {
            String name = namer.apply(value);
            entries.add(new Entry<>(value, name));
        }

        ListComboBoxModel<Entry<T>> model = new ListComboBoxModel<>(entries) {

            @Override
            public String toString(Entry<T> element) {
                return element.value;
            }
        };

        setModel(model);

        int index = 0;
        T currentValue = getCurrent.get();
        if (currentValue != null) {
            for (int i = 0; i < entries.size(); i++) {
                if (currentValue.equals(entries.get(i).key)) {
                    index = i;
                    break;
                }
            }
        }

        setSelectedIndex(index);
    }

    public T getSelectedValue() {
        int index = getSelectedIndex();
        Entry<T> entry = entries.get(index);
        return entry.key;
    }

    public static class Entry<T> {

        private T key;
        private String value;

        public Entry(T key, String value) {
            this.key = key;
            this.value = value;
        }

    }

}