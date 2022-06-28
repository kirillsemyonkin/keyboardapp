package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class HideKey extends KeyboardKey {
    public HideKey(KeyIcon icon, float growthFactor, Highlight highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.hideKeyboard();
    }

    public void hold(KeyboardService service, int pointerID) {
        tap(service);
    }
}
