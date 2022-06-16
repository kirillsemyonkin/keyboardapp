package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

/**
 * This key simply appends a character to a given composing text.
 */
public class CharKey extends KeyboardKey {
    private final char character;

    public CharKey(KeyIcon icon, float growthFactor, boolean highlight) {
        super(icon, growthFactor, highlight);
        this.character = icon.character();
    }

    public void tap(KeyboardService service) {
        service.appendToComposingText(character);
    }
}
