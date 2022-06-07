package kirillsemyonkin.keyboardapp.keys;

import static kirillsemyonkin.keyboardapp.keys.ComposingTextResult.composingCommit;

public class SimpleCharAppendEnterKey implements KeyboardKey {
    public String display(String currentComposingText) {
        return "Enter";
    }

    public ComposingTextResult transformText(String previousComposingText) {
        return composingCommit(previousComposingText + '\n');
    }
}
