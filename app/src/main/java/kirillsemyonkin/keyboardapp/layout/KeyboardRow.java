package kirillsemyonkin.keyboardapp.layout;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import kirillsemyonkin.keyboardapp.action.KeyboardKey;

public class KeyboardRow {
    private final List<KeyboardKey> keys;
    private final float growthFactor;

    public KeyboardRow(List<KeyboardKey> keys, float growthFactor) {
        this.keys = unmodifiableList(keys);
        this.growthFactor = growthFactor;
    }

    public int colCount() {
        return keys.size();
    }

    public KeyboardKey key(int col) {
        return col >= 0 && col < colCount()
            ? keys.get(col)
            : null;
    }

    public float growthFactor() {
        return growthFactor;
    }
}
