package kirillsemyonkin.keyboardapp.util;

import static java.lang.Math.floorDiv;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_BACKGROUND_DOWN;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_BACKGROUND_HIGHLIGHT;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_CORNER_RADIUS;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_PADDING;

import android.graphics.Canvas;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.icon.PlainTextKeyIcon;

public final class AltMenu {
    private final int pointerID;
    private final AltCharAppendKey key;
    private final int borderX;
    private final int y;
    private final boolean rightBorderFirst;
    private final int altKeyWidth;

    public AltMenu(int pointerID,
                   AltCharAppendKey key,
                   int borderX,
                   int y,
                   boolean rightBorderFirst,
                   int altKeyWidth) {
        this.pointerID = pointerID;
        this.key = key;
        this.borderX = borderX;
        this.y = y;
        this.rightBorderFirst = rightBorderFirst;
        this.altKeyWidth = altKeyWidth;

        selectedIndex
            = rightBorderFirst
            ? key.altChars().length
            : 0;
    }

    public int pointerID() {
        return pointerID;
    }

    public AltCharAppendKey key() {
        return key;
    }

    public int borderX() {
        return borderX;
    }

    public int left() {
        return rightBorderFirst
            ? borderX - altKeyWidth * (1 + key.altChars().length)
            : borderX;
    }

    public int y() {
        return y;
    }

    public boolean rightBorderFirst() {
        return rightBorderFirst;
    }

    public boolean heldBy(int pointer) {
        return this.pointerID == pointer;
    }

    public boolean heldBy(int pointer, AltCharAppendKey key) {
        return heldBy(pointer) && this.key == key;
    }

    //
    // Selecting alt keys
    //

    private int selectedIndex;

    public AltMenu selectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        return this;
    }

    public int unprojectIndex(int x) {
        var left = left();
        var count = 1 + key.altChars().length;
        var index = floorDiv(x - left, altKeyWidth);
        return max(0, min(count - 1, index));
    }

    public char charAt(int i) {
        return rightBorderFirst
            ? (i == key.altChars().length ? key.character() : key.altChars()[i])
            : (i == 0 ? key.character() : key.altChars()[i - 1]);
    }

    public void draw(Canvas canvas, int keyHeight) {
        var left = left();
        var count = 1 + key.altChars().length;
        var totalMenuWidth = altKeyWidth * count;

        // Menu background
        canvas.drawRoundRect(
            left + KEY_PADDING,
            y + KEY_PADDING,
            left + totalMenuWidth - KEY_PADDING,
            y + keyHeight - KEY_PADDING,
            KEY_CORNER_RADIUS,
            KEY_CORNER_RADIUS,
            KEY_BACKGROUND_DOWN);

        // Alt keys
        for (var i = 0; i < count; i++) {
            var x = left + i * altKeyWidth;

            // Alt key background
            if (i == selectedIndex)
                canvas.drawRoundRect(
                    x + KEY_PADDING,
                    y + KEY_PADDING,
                    x + altKeyWidth - KEY_PADDING,
                    y + keyHeight - KEY_PADDING,
                    KEY_CORNER_RADIUS,
                    KEY_CORNER_RADIUS,
                    KEY_BACKGROUND_HIGHLIGHT);

            // Alt key icon
            canvas.save();
            {
                canvas.translate(
                    x + floorDiv(altKeyWidth, 2),
                    y + floorDiv(keyHeight, 2));
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
