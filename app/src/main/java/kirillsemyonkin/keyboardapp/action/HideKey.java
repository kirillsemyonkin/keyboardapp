package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class HideKey extends KeyboardKey {
    public HideKey(KeyIcon icon, float growthFactor, boolean highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.hideKeyboard();
    }

    public void hold(KeyboardService service, int pointerID) {
        tap(service);
    }
}
