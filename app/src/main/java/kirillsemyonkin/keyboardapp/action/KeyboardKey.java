package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

/**
 * Represents a key button with an action attached to it.
 */
public abstract class KeyboardKey {
    private final KeyIcon icon;
    private final float growthFactor;
    private final Highlight highlight;

    protected KeyboardKey(KeyIcon icon, float growthFactor, Highlight highlight) {
        this.icon = icon;
        this.growthFactor = growthFactor;
        this.highlight = highlight;
    }

    /**
     * Get the display icon of the key.
     */
    public KeyIcon icon() {
        return icon;
    }

    /**
     * How much of the layout growth factor to occupy.
     * <p>
     * For example,
     * if the key has a growth factor of 1,
     * and the layout has a growth factor of 10,
     * the key will occupy 10% of the row.
     */
    public float growthFactor() {
        return growthFactor;
    }

    /**
     * Whether should the key background be colored.
     */
    public Highlight highlight() {
        return highlight;
    }

    //
    // Virtual
    //

    /**
     * Perform an action for a simple tap.
     *
     * @param service Service to perform the action upon.
     */
    public void tap(KeyboardService service) {
    }

    /**
     * Perform an action for a long hold press or repeat.
     *
     * @param service   Service to perform the action upon.
     * @param pointerID Pointer that holds the key.
     */
    public void hold(KeyboardService service, int pointerID) {
    }

    /**
     * Perform an action for a long hold release.
     *
     * @param service   Service to perform the action upon.
     * @param pointerID Pointer that held the key.
     */
    public void unhold(KeyboardService service, int pointerID) {
    }
}
