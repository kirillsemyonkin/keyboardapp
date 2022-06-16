package kirillsemyonkin.keyboardapp.layout;

import java.util.List;

import kirillsemyonkin.keyboardapp.action.KeyboardKey;

public final class KeyboardLayout {
    private final List<List<KeyboardKey>> rows;
    private final float growthFactor;

    public KeyboardLayout(List<List<KeyboardKey>> rows, float growthFactor) {
        this.rows = rows;
        this.growthFactor = growthFactor;
    }

    public int rowCount() {
        return rows.size();
    }

    public int colCount(int row) {
        return row >= 0 && row < rowCount()
            ? rows.get(row).size()
            : -1;
    }

    public KeyboardKey key(int col, int row) {
        return col >= 0 && col < colCount(row)
            ? rows.get(row).get(col)
            : null;
    }

    public float growthFactor() {
        return growthFactor;
    }
}
