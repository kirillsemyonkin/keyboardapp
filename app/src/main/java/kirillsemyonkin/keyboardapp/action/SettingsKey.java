package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class SettingsKey extends KeyboardKey {
    public SettingsKey(KeyIcon icon, float growthFactor, boolean highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.openSettings();
    }

    public void hold(KeyboardService service, int pointerID) {
        // Open settings on long hold
        tap(service);
    }
}
