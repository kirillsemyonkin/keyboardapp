package kirillsemyonkin.keyboardapp.icon;

import android.graphics.Canvas;

/**
 * Represents the way the key would be displayed in the key rectangle.
 */
public interface KeyIcon {
    /**
     * If possible, turns the icon into a single character representation.
     *
     * @return Character that represents this icon.
     * @throws UnsupportedOperationException If this icon does not support being turned into a character.
     */
    char character() throws UnsupportedOperationException;

    /**
     * Draws the icon on the canvas.
     * Canvas is prepared to draw from the center.
     *
     * @param canvas Canvas to draw on.
     */
    void draw(Canvas canvas);
}
