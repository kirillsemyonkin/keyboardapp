package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class AltCharAppendKey extends KeyboardKey {
    private final char character;
    private final char[] altChars;

    public AltCharAppendKey(KeyIcon icon, float growthFactor, boolean highlight, char[] altChars) {
        super(icon, growthFactor, highlight);
        this.character = icon.character();
        this.altChars = altChars;
    }

    public void action(KeyboardService service) {
        service.appendToComposingText(character);
    }

    // TODO long hold to show alt chars and to update with selected one
}
