package kirillsemyonkin.keyboardapp;

import static java.lang.Math.min;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import kirillsemyonkin.keyboardapp.keys.KeyboardKey;

public final class KeyboardLayout {
    private final List<KeyboardLayoutRow> rows;

    public KeyboardLayout(List<KeyboardLayoutRow> rows) {
        this.rows = rows;
    }

    public int rowCount() {
        return rows.size();
    }

    public int colCount(int row) {
        return row >= 0 && row < rowCount()
            ? rows.get(row).keys().size()
            : -1;
    }

    public KeyboardKey key(int col, int row) {
        return col >= 0 && col < colCount(row)
            ? rows.get(row).keys().get(col)
            : null;
    }

    public final static class KeyboardLayoutRow {
        private final int growthFactor;
        private final List<KeyboardKey> keys;

        public KeyboardLayoutRow(int growthFactor, List<KeyboardKey> keys) {
            this.growthFactor = growthFactor;
            this.keys = unmodifiableList(keys);
        }

        public List<KeyboardKey> keys() {
            return keys;
        }

        public int growthFactor() {
            return growthFactor;
        }
    }

    public final static class KeyboardLayoutRowBuilder {
        private final KeyboardLayoutBuilder keyboardLayoutBuilder;

        private KeyboardLayoutRowBuilder(KeyboardLayoutBuilder keyboardLayoutBuilder) {
            this.keyboardLayoutBuilder = keyboardLayoutBuilder;
        }

        public KeyboardLayoutBuilder buildRow() {
            var growthFactorFinal
                = growthFactor < 0
                ? keys.size()
                : min(growthFactor, keys.size());
            keyboardLayoutBuilder.row(new KeyboardLayoutRow(growthFactorFinal, keys));
            return keyboardLayoutBuilder;
        }

        //
        // Growth factor
        //

        private int growthFactor = -1;

        public KeyboardLayoutRowBuilder growthFactor(int factor) {
            this.growthFactor = growthFactor;
            return this;
        }

        //
        // Keys
        //

        private final List<KeyboardKey> keys = new ArrayList<>();

        public KeyboardLayoutRowBuilder append(KeyboardKey key) {
            this.keys.add(key);
            return this;
        }
    }

    //
    // Builder
    //

    public static KeyboardLayoutBuilder builder() {
        return new KeyboardLayoutBuilder();
    }

    public final static class KeyboardLayoutBuilder {
        public KeyboardLayout build() {
            return new KeyboardLayout(rows);
        }

        //
        // Rows
        //

        private final List<KeyboardLayoutRow> rows = new ArrayList<>();

        public KeyboardLayoutRowBuilder row() {
            return new KeyboardLayoutRowBuilder(this);
        }

        public KeyboardLayoutBuilder row(KeyboardLayoutRow row) {
            rows.add(row);
            return this;
        }
    }
}
