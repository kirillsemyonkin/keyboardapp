package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class BackspaceKey extends KeyboardKey {
    public BackspaceKey(KeyIcon icon, float growthFactor, Highlight highlight) {
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
