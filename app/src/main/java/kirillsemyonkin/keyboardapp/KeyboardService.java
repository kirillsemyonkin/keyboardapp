package kirillsemyonkin.keyboardapp;

public interface KeyboardService {
    void switchMode(String mode);

    KeyboardLayout layout();

    //
    // Composing text
    //

    String currentComposingText();

    void updateComposingText(String composingText);

    void commitComposingText(String composingText);

    void deleteSurroundingText(int beforeLength, int afterLength);
}
