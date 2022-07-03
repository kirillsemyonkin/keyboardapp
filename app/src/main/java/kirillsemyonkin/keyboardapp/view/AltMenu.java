package kirillsemyonkin.keyboardapp.view;

import static java.lang.Math.floorDiv;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_BACKGROUND_DOWN;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_BACKGROUND_HIGHLIGHT;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_CORNER_RADIUS;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_PADDING;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_TEXT;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_TEXT_FACTOR;

import android.graphics.Canvas;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.icon.PlainTextKeyIcon;

public final class AltMenu {
    private final int pointerID;
    private final AltCharAppendKey key;
    private final int baseKeyX;
    private final int baseKeyY;
    private final boolean horizontalDirectionRight;
    private final int rows;
    private final int altKeyWidth;
    private final int altKeyHeight;

    public AltMenu(int pointerID,
                   AltCharAppendKey key,
                   int baseKeyX,
                   int baseKeyY,
                   boolean horizontalDirectionRight,
                   int rows,
                   int altKeyWidth,
                   int altKeyHeight) {
        this.pointerID = pointerID;
        this.key = key;
        this.baseKeyX = baseKeyX;
        this.baseKeyY = baseKeyY;
        this.horizontalDirectionRight = horizontalDirectionRight;
        this.rows = rows;
        this.altKeyWidth = altKeyWidth;
        this.altKeyHeight = altKeyHeight;
    }

    public int pointerID() {
        return pointerID;
    }

    public AltCharAppendKey key() {
        return key;
    }

    public int baseKeyX() {
        return baseKeyX;
    }

    public int baseKeyY() {
        return baseKeyY;
    }

    public boolean horizontalDirectionRight() {
        return horizontalDirectionRight;
    }

    public boolean heldBy(int pointer) {
        return this.pointerID == pointer;
    }

    public boolean heldBy(int pointer, AltCharAppendKey key) {
        return heldBy(pointer) && this.key == key;
    }

    //
    // Utils
    //

    public int count() {
        return 1 + key.altChars().length;
    }

    public int keysPerRow() {
        return floorDiv(count() + rows - 1, rows);
    }

    public int left() {
        return horizontalDirectionRight
            ? baseKeyX
            : baseKeyX - (keysPerRow() - 1) * altKeyWidth;
    }

    public int top() {
        return baseKeyY;
    }

    //
    // Selecting alt keys
    //

    private int selectedIndex;

    public AltMenu selectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        return this;
    }

    public int unprojectIndex(int x, int y) {
        var count = count();
        var keysPerRow = keysPerRow();
        var colRightwards = floorDiv(x - baseKeyX, altKeyWidth);
        var col = max(0, min(keysPerRow - 1, colRightwards * (horizontalDirectionRight ? 1 : -1)));
        var row = max(0, min(rows - 1, floorDiv(y - baseKeyY, altKeyHeight)));
        return max(0, min(count - 1, row * keysPerRow + col));
    }

    public char charAt(int i) {
        return i == 0
            ? key.character()
            : key.altChars()[i - 1];
    }

    public void draw(Canvas canvas) {
        var count = count();
        var keysPerRow = keysPerRow();
        var totalMenuWidth = altKeyWidth * keysPerRow;

        var left = left();
        var top = top();

        // Menu background
        canvas.drawRoundRect(
            left + KEY_PADDING,
            top + KEY_PADDING,
            left + totalMenuWidth - KEY_PADDING,
            top + altKeyHeight * rows - KEY_PADDING,
            KEY_CORNER_RADIUS,
            KEY_CORNER_RADIUS,
            KEY_BACKGROUND_DOWN);

        // Alt keys
        for (var i = 0; i < count; i++) {
            var col = i % keysPerRow;
            var row = i / keysPerRow;

            var x = baseKeyX + col * altKeyWidth * (horizontalDirectionRight ? 1 : -1);
            var y = baseKeyY + row * altKeyHeight;

            // In future might be a good idea to find widest key icon
            //   and use it to make all icons always fit both dimensions
            KEY_TEXT.setTextSize(altKeyHeight * KEY_TEXT_FACTOR);

            // Alt key background
            if (i == selectedIndex)
                canvas.drawRoundRect(
                    x + KEY_PADDING,
                    y + KEY_PADDING,
                    x + altKeyWidth - KEY_PADDING,
                    y + altKeyHeight - KEY_PADDING,
                    KEY_CORNER_RADIUS,
                    KEY_CORNER_RADIUS,
                    KEY_BACKGROUND_HIGHLIGHT);

            // Alt key icon
            canvas.save();
            {
                canvas.translate(
                    x + floorDiv(altKeyWidth, 2),
                    y + floorDiv(altKeyHeight, 2));
                PlainTextKeyIcon
                    .drawText(canvas, charAt(i) + "");
            }
            canvas.restore();
        }
    }

    public char selectedCharacter() {
        return charAt(selectedIndex);
    }
}
