package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class BackspaceKey extends KeyboardKey {
    public BackspaceKey(KeyIcon icon, float growthFactor, boolean highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.sendBackspace();
    }

    public void hold(KeyboardService service, int pointerID) {
        // Repeat erasing on long hold
        tap(service);
    }
}
