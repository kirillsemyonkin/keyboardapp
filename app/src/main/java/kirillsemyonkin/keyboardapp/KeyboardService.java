package kirillsemyonkin.keyboardapp;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.layout.KeyboardLayout;

public interface KeyboardService {
    void switchMode(String mode);

    KeyboardLayout layout();

    //
    // Composing text
    //

    String currentComposingText();

    void appendToComposingText(char character);

    void backspaceComposingText();

    void backspaceText();

    void sendEnter();

    void showAltChars(AltCharAppendKey key);
}
