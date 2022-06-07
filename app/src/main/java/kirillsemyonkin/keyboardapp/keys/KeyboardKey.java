package kirillsemyonkin.keyboardapp.keys;

/**
 * Represents a button with an action attached to it.
 */
public interface KeyboardKey {
    /**
     * @param currentComposingText Currently composing text (some locales require this to display different settings).
     * @return Text for display on the key
     */
    String display(String currentComposingText);

    /**
     * Transform text from previous composing text to a new one.
     * For example, for latin letter keys, it may be just appending a letter at the end.
     *
     * @param previousComposingText Text to transform.
     * @return New string for a new composing text, null to commit current composing text.
     */
    ComposingTextResult transformText(String previousComposingText);
}
