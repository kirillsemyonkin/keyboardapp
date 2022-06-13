package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

/**
 * Switch mode key &lt;switch&gt; changes the layout of the keyboard.
 */
public class SwitchModeKey extends KeyboardKey {
    private final String mode;

    public SwitchModeKey(KeyIcon icon, float growthFactor, boolean highlight, String mode) {
        super(icon, growthFactor, highlight);
        this.mode = mode;
    }

    public String mode() {
        return mode;
    }

    public void action(KeyboardService service) {
        service.switchMode(mode);
    }
}
