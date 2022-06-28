package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

/**
 * Switch mode key &lt;switch&gt; changes the layout of the keyboard.
 */
public class SwitchModeKey extends KeyboardKey {
    private final String mode;

    public SwitchModeKey(KeyIcon icon, float growthFactor, Highlight highlight,
                         String mode) {
        super(icon, growthFactor, highlight);
        this.mode = mode;
    }

    public String mode() {
        return mode;
    }

    public void tap(KeyboardService service) {
        service.switchMode(mode);
    }

    public void hold(KeyboardService service, int pointerID) {
        // Switch mode on long hold
        tap(service);
    }
}
