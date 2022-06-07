package kirillsemyonkin.keyboardapp.keys;

import static java.lang.Math.max;
import static kirillsemyonkin.keyboardapp.keys.ComposingTextResult.composingUpdate;

public class SimpleCharAppendBackspaceKey implements KeyboardKey {
    public String display(String currentComposingText) {
        return "<=";
    }

    public ComposingTextResult transformText(String previousComposingText) {
        return composingUpdate(
            previousComposingText.substring(
                0, max(0, previousComposingText.length() - 1)));
    }
}
