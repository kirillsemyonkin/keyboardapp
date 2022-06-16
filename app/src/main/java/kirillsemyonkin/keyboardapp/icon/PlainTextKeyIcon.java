package kirillsemyonkin.keyboardapp.icon;

import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_TEXT;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Plain text key icon draws a plain text string centered at the icon.
 * If the text contains a single character, it may be used for text operations.
 */
public class PlainTextKeyIcon implements KeyIcon {
    private final String text;

    public PlainTextKeyIcon(String text) {
        this.text = text;
    }

    public char character() throws UnsupportedOperationException {
        if (text.length() != 1)
            throw new UnsupportedOperationException("<key> without action must contain exactly 1 character");
        return text.charAt(0);
    }

    public void draw(Canvas canvas, Resources resources) {
        var bounds = new Rect();
        KEY_TEXT.getTextBounds(text, 0, text.length(), bounds);
        var metrics = KEY_TEXT.getFontMetrics();
        canvas.drawText(
            text,
            -bounds.width() / 2f - bounds.left,
            -(metrics.top - metrics.bottom) / 4f,
            KEY_TEXT);
    }
}
