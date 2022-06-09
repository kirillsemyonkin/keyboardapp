package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

/**
 * This key simply appends a character to a given composing text.
 */
public class SimpleCharAppendKey extends KeyboardKey {
    private final char character;

    public SimpleCharAppendKey(KeyIcon icon, float growthFactor) {
        super(icon, growthFactor);
        this.character = icon.character();
    }

    public void action(KeyboardService service) {
        service.updateComposingText(
            service.currentComposingText() + character);
    }
}
