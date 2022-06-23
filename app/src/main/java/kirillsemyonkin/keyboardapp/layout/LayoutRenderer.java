package kirillsemyonkin.keyboardapp.layout;

import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;
import static java.lang.Math.floorMod;
import static java.util.Map.entry;

import android.content.res.Resources;

import java.util.Map.Entry;

import kirillsemyonkin.keyboardapp.action.KeyboardKey;
import kirillsemyonkin.keyboardapp.util.KeyProjection;

public enum LayoutRenderer {
    STANDARD(false) {
        public int viewHeight(Resources resources, int suggestedMinimumHeight, int heightMeasureSpec) {
            return resources
                .getDisplayMetrics()
                .heightPixels / 2;
        }

        private Entry<int[], Integer> calculateRowKeyWidths(KeyboardRow row,
                                                            int totalViewWidth,
                                                            float rowGrowthFactor) {
            var widths = new int[row.colCount()];
            int totalKeysWidth = 0;
            for (var colNum = 0; colNum < widths.length; colNum++) {
                var key = row.key(colNum);
                assert key != null;

                totalKeysWidth
                    += widths[colNum]
                    = (int) floor(totalViewWidth * key.growthFactor() / rowGrowthFactor);
            }
            return entry(widths, totalKeysWidth);
        }

        public KeyboardKey unproject(KeyboardLayout layout,
                                     int totalViewWidth, int totalViewHeight,
                                     int posX, int posY) {
            // Determine row from posY and ensure valid
            var rows = layout.rowCount();
            var rowNum = floorDiv(posY * rows, totalViewHeight);
            var row = layout.row(rowNum);
            if (row == null) return null;

            var cols = row.colCount();
            if (cols == 0) return null;

            // Calculate key widths
            var keyWidths
                = calculateRowKeyWidths(row, totalViewWidth, layout.growthFactor(rowNum));
            var widths = keyWidths.getKey();
            var totalKeysWidth = keyWidths.getValue();

            // Go through row and find valid key
            var rowStart = floorDiv(totalViewWidth - totalKeysWidth, 2);
            for (int colNum = 0, x = rowStart; colNum < cols; x += widths[colNum++])
                if (x <= posX && posX < x + widths[colNum])
                    return row.key(colNum);

            return null;
        }

        public KeyProjection project(KeyboardLayout layout,
                                     int totalViewWidth, int totalViewHeight,
                                     KeyboardKey key) {
            var rows = layout.rowCount();
            if (rows == 0) return null;
            var keyHeight = floorDiv(totalViewHeight, rows);

            for (var rowNum = 0; rowNum < rows; rowNum++) {
                var row = layout.row(rowNum);
                assert row != null;

                var cols = row.colCount();
                if (cols == 0) continue;

                // Calculate key widths
                var keyWidths
                    = calculateRowKeyWidths(row, totalViewWidth, layout.growthFactor(rowNum));
                var widths = keyWidths.getKey();
                var totalKeysWidth = keyWidths.getValue();

                // Go through row and find valid key
                var rowStart = floorDiv(totalViewWidth - totalKeysWidth, 2);
                for (int colNum = 0, x = rowStart; colNum < cols; x += widths[colNum++])
                    if (key == row.key(colNum))
                        return new KeyProjection(
                            x, rowNum * keyHeight,
                            widths[colNum], keyHeight);
            }

            return null;
        }
    },
    BIG_BUTTONS(true) {
        public static final int INPUT_SIZE = 300;

        public int viewHeight(Resources resources,
                              int suggestedMinimumHeight,
                              int heightMeasureSpec) {
            return resources
                .getDisplayMetrics()
                .heightPixels - INPUT_SIZE;
        }

        private final static int SUBROWS_PER_ROW = 2;

        public KeyboardKey unproject(KeyboardLayout layout,
                                     int totalViewWidth, int totalViewHeight,
                                     int posX, int posY) {
            var rows = layout.rowCount();
            var rowNum = floorDiv(posY * rows, totalViewHeight);
            var row = layout.row(rowNum);
            if (row == null) return null;

            var cols = row.colCount();
            if (cols == 0) return null;

            var subcols = floorDiv(cols - 1, SUBROWS_PER_ROW) + 1;
            var rowHeight = floorDiv(totalViewHeight, rows);

            var subcol = floorDiv(posX * subcols, totalViewWidth);

            var subrowCount
                = subcol == subcols - 1
                ? floorMod(cols, SUBROWS_PER_ROW)
                : SUBROWS_PER_ROW;
            if (subrowCount == 0) subrowCount = SUBROWS_PER_ROW;
            var subrowHeight = floorDiv(rowHeight, subrowCount);

            var subrow = floorDiv(
                posY - rowNum * rowHeight, // y relative to row start
                subrowHeight);
            return row.key(subcol * SUBROWS_PER_ROW + subrow);
        }

        public KeyProjection project(KeyboardLayout layout,
                                     int totalViewWidth, int totalViewHeight,
                                     KeyboardKey key) {
            var rows = layout.rowCount();
            if (rows == 0) return null;

            for (var rowNum = 0; rowNum < rows; rowNum++) {
                var row = layout.row(rowNum);
                assert row != null;

                var cols = row.colCount();
                if (cols == 0) continue;

                for (int colNum = 0; colNum < cols; colNum++)
                    if (key == row.key(colNum)) {
                        var subcols = floorDiv(cols - 1, SUBROWS_PER_ROW) + 1;
                        var subcolWidth = floorDiv(totalViewWidth, subcols);
                        var rowHeight = floorDiv(totalViewHeight, rows);

                        var subcol = floorDiv(colNum, SUBROWS_PER_ROW);

                        var subrowCount
                            = subcol == subcols - 1
                            ? floorMod(cols, SUBROWS_PER_ROW)
                            : SUBROWS_PER_ROW;
                        if (subrowCount == 0) subrowCount = SUBROWS_PER_ROW;
                        var subrowHeight = floorDiv(rowHeight, subrowCount);

                        var subrow = floorMod(colNum, subrowCount);

                        return new KeyProjection(
                            subcol * subcolWidth, rowNum * rowHeight + subrow * subrowHeight,
                            subcolWidth, subrowHeight);
                    }
            }

            return null;
        }
    };

    private final boolean fullscreen;

    LayoutRenderer(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean fullscreen() {
        return fullscreen;
    }

    public abstract int viewHeight(Resources resources,
                                   int suggestedMinimumHeight,
                                   int heightMeasureSpec);

    public abstract KeyboardKey unproject(KeyboardLayout layout,
                                          int totalViewWidth, int totalViewHeight,
                                          int posX, int posY);

    public abstract KeyProjection project(KeyboardLayout layout,
                                          int totalViewWidth, int totalViewHeight,
                                          KeyboardKey key);
}
