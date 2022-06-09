package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

/**
 * Represents a key button with an action attached to it.
 */
public abstract class KeyboardKey {
    private final KeyIcon icon;
    private final float growthFactor;

    protected KeyboardKey(KeyIcon icon, float growthFactor) {
        this.icon = icon;
        this.growthFactor = growthFactor;
    }

    /**
     * Get the display icon of the key.
     */
    public KeyIcon icon() {
        return icon;
    }

    /**
     * How much of the layout growth factor to occupy.
     *
     * For example,
     * if the key has a growth factor of 1,
     * and the layout has a growth factor of 10,
     * the key will occupy 10% of the row.
     */
    public float growthFactor() {
        return growthFactor;
    }

    /**
     * Perform an action.
     *
     * @param service Service to perform the action upon.
     */
    public abstract void action(KeyboardService service);
}
