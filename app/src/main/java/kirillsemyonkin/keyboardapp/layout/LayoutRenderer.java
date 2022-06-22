package kirillsemyonkin.keyboardapp.layout;

import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;

import android.content.res.Resources;

import kirillsemyonkin.keyboardapp.action.KeyboardKey;
import kirillsemyonkin.keyboardapp.util.KeyProjection;

public enum LayoutRenderer {
    STANDARD {
        public int viewHeight(Resources resources, int heightMeasureSpec) {
            return resources
                .getDisplayMetrics()
                .heightPixels / 2;
        }

        public KeyboardKey unproject(KeyboardLayout layout,
                                     int totalViewWidth,
                                     int totalViewHeight,
                                     int posX, int posY) {
            // Determine row from posY and ensure valid
            var rows = layout.rowCount();
            var rowNum = floorDiv(posY * rows, totalViewHeight);
            var row = layout.row(rowNum);
            if (row == null) return null;

            // Determine col from posX and ensure valid
            var cols = row.colCount();
            var totalGrowthFactor = layout.growthFactor();

            var rowGrowthFactor = row.growthFactor();
            if (rowGrowthFactor <= 0) rowGrowthFactor = totalGrowthFactor;

            var widths = new int[cols];
            int totalKeysWidth = 0;
            for (var colNum = 0; colNum < cols; colNum++) {
                var key = row.key(colNum);
                assert key != null;

                totalKeysWidth
                    += widths[colNum]
                    = (int) floor(totalViewWidth * key.growthFactor() / rowGrowthFactor);
            }

            var rowStart = floorDiv(totalViewWidth - totalKeysWidth, 2);
            for (int colNum = 0, x = rowStart; colNum < cols; x += widths[colNum++])
                if (x <= posX && posX < x + widths[colNum])
                    return row.key(colNum);

            return null;
        }

        public KeyProjection project(KeyboardLayout layout,
                                     int totalViewWidth, int totalViewHeight,
                                     KeyboardKey key) {
            var totalGrowthFactor = layout.growthFactor();

            var rows = layout.rowCount();
            if (rows == 0) return null;
            var keyHeight = floorDiv(totalViewHeight, rows);

            for (var rowNum = 0; rowNum < rows; rowNum++) {
                var row = layout.row(rowNum);
                assert row != null;

                var cols = row.colCount();
                if (cols == 0) continue;

                var rowGrowthFactor = row.growthFactor();
                if (rowGrowthFactor <= 0) rowGrowthFactor = totalGrowthFactor;

                var widths = new int[cols];
                int totalKeysWidth = 0;
                for (var colNum = 0; colNum < cols; colNum++) {
                    var k = row.key(colNum);
                    assert k != null;

                    totalKeysWidth
                        += widths[colNum]
                        = (int) floor(totalViewWidth * k.growthFactor() / rowGrowthFactor);
                }

                var rowStart = floorDiv(totalViewWidth - totalKeysWidth, 2);
                for (int colNum = 0, x = rowStart; colNum < cols; x += widths[colNum++])
                    if (key == row.key(colNum))
                        return new KeyProjection(
                            x, rowNum * keyHeight,
                            widths[colNum], keyHeight);
            }

            return null;
        }
    };

    public abstract int viewHeight(Resources resources, int heightMeasureSpec);

    public abstract KeyboardKey unproject(KeyboardLayout layout,
                                          int totalViewWidth, int totalViewHeight,
                                          int posX, int posY);

    public abstract KeyProjection project(KeyboardLayout layout,
                                          int totalViewWidth, int totalViewHeight,
                                          KeyboardKey key);
}
