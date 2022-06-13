package kirillsemyonkin.keyboardapp;

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
}
