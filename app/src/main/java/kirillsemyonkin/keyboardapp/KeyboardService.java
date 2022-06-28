package kirillsemyonkin.keyboardapp;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.layout.KeyboardLayout;
import kirillsemyonkin.keyboardapp.layout.LayoutRenderer;
import kirillsemyonkin.keyboardapp.util.ShiftCase;

public interface KeyboardService {
    LayoutRenderer renderer();

    void switchMode(String mode);

    void switchToNextLocale();

    KeyboardLayout layout();

    void openSettings();

    void hideKeyboard();

    //
    // Composing text
    //

    void sendCharacter(char character);

    void sendSpace();

    void sendBackspace();

    void sendEnter();

    //
    // Alt char menu
    //

    void showAltChars(int pointer, AltCharAppendKey key);

    void hideAltChars(int pointer, AltCharAppendKey key);

    //
    // Shift case
    //

    ShiftCase shift();

    void shift(ShiftCase shift);
}
