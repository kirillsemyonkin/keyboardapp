package kirillsemyonkin.keyboardapp.keys;

import static kirillsemyonkin.keyboardapp.keys.ComposingTextResult.composingUpdate;

public class SimpleCharAppendKey implements KeyboardKey {
    private final char display;

    public SimpleCharAppendKey(char display) {
        this.display = display;
    }

    public String display(String currentComposingText) {
        return display + "";
    }

    public ComposingTextResult transformText(String previousComposingText) {
        return composingUpdate(previousComposingText + display);
    }
}
