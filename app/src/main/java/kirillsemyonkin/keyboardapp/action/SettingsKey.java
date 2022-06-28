package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class SettingsKey extends KeyboardKey {
    public SettingsKey(KeyIcon icon, float growthFactor, Highlight highlight) {
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
