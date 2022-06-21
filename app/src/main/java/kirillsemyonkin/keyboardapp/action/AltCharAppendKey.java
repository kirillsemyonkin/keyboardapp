package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class AltCharAppendKey extends KeyboardKey {
    private final char character;
    private final char[] altChars;

    public AltCharAppendKey(KeyIcon icon, float growthFactor, boolean highlight,
                            char... altChars) {
        super(icon, growthFactor, highlight);
        this.character = icon.character();
        this.altChars = altChars;
    }

    public char character() {
        return character;
    }

    public char[] altChars() {
        return altChars;
    }

    public void tap(KeyboardService service) {
        service.sendCharacter(character);
    }

    public void hold(KeyboardService service, int pointerID) {
        service.showAltChars(pointerID, this);
    }

    public void unhold(KeyboardService service, int pointerID) {
        service.hideAltChars(pointerID, this);
    }
}
