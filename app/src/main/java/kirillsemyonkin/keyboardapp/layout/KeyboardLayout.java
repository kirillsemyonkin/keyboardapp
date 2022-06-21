package kirillsemyonkin.keyboardapp.layout;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public final class KeyboardLayout {
    private final List<KeyboardRow> rows;
    private final float growthFactor;

    public KeyboardLayout(List<KeyboardRow> rows, float growthFactor) {
        this.rows = unmodifiableList(rows);
        this.growthFactor = growthFactor;
    }

    public int rowCount() {
        return rows.size();
    }

    public KeyboardRow row(int row) {
        return row >= 0 && row < rowCount()
            ? rows.get(row)
            : null;
    }

    public float growthFactor() {
        return growthFactor;
    }
}
