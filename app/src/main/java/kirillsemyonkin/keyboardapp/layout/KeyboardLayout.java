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

    public KeyboardRow row(int rowNum) {
        return rowNum >= 0 && rowNum < rowCount()
            ? rows.get(rowNum)
            : null;
    }

    public float growthFactor() {
        return growthFactor;
    }

    public float growthFactor(int rowNum) {
        var row = row(rowNum);
        if (row == null) return growthFactor();
        var rowGrowthFactor = row.growthFactor();
        return rowGrowthFactor <= 0
            ? growthFactor()
            : rowGrowthFactor;
    }
}
