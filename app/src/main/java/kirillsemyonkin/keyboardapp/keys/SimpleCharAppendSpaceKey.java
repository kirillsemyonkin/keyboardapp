package kirillsemyonkin.keyboardapp.keys;

import static kirillsemyonkin.keyboardapp.keys.ComposingTextResult.composingCommit;

public class SimpleCharAppendSpaceKey implements KeyboardKey {
    public String display(String currentComposingText) {
        return "Space";
    }

    public ComposingTextResult transformText(String previousComposingText) {
        return composingCommit(previousComposingText + ' ');
    }
}
