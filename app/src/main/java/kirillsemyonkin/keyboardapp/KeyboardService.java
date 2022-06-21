package kirillsemyonkin.keyboardapp;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.layout.KeyboardLayout;

public interface KeyboardService {
    void switchMode(String mode);

    void switchToNextLocale();

    KeyboardLayout layout();

    void openSettings();

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
}
